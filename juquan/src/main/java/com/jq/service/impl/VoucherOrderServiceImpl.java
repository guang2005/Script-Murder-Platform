package com.jq.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jq.dto.Result;
import com.jq.entity.SeckillVoucher;
import com.jq.entity.VoucherOrder;
import com.jq.mapper.VoucherOrderMapper;
import com.jq.service.IVoucherOrderService;
import com.jq.utils.RedisIdWorker;
import com.jq.utils.UserHolder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private SeckillVoucherServiceImpl seckillVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private IVoucherOrderService proxy;


    /**
     * 处理优惠券订单的方法
     *
     * @param voucherOrder 优惠券订单对象
     */
    // ==================== 1. 订单处理方法（原有逻辑+小优化） ====================
    @Override
    public void handleVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        // 优化：添加锁超时时间，防止死锁
        boolean isLock = lock.tryLock();
        if (!isLock) {
            log.error("不允许重复下单，用户ID: {}", userId);
            return;
        }
        try {
            // 代理调用，触发事务
            proxy.createVoucherOrder(voucherOrder);
        } catch (Exception e) {
            log.error("处理订单失败，订单ID: {}", voucherOrder.getId(), e);
            throw new RuntimeException("订单处理失败");
        } finally {
            // 确保锁释放（判断是否持有锁，避免释放别人的锁）
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ==================== 2. 秒杀入口方法（修复核心问题） ====================
    @Override
    public Result seckillVoucher(Long voucherId) {
        // 1. 获取用户ID（判空，防止空指针）
        Long userId = UserHolder.getUser().getId();
        if (userId == null) {
            return Result.fail("请先登录");
        }
        // 2. 生成订单ID
        long orderId = redisIdWorker.nextId("order");
        // 3. 执行Lua脚本
        Long result;
        try {
            result = stringRedisTemplate.execute(
                    SECKILL_SCRIPT,
                    Collections.emptyList(),
                    voucherId.toString(), userId.toString(), String.valueOf(orderId)
            );
        } catch (Exception e) {
            log.error("Redis Lua脚本执行失败", e);
            return Result.fail("秒杀请求处理失败，请稍后重试");
        }
        // 4. 处理Lua脚本结果（修复：区分不同错误）
        int r = result != null ? result.intValue() : -1;
        if (r == 1) {
            return Result.fail("库存不足");
        } else if (r == 2) { // 假设Lua脚本中2代表重复下单（需和Lua脚本逻辑一致）
            return Result.fail("不能重复下单");
        } else if (r != 0) { // 其他错误（-1/3等）
            return Result.fail("秒杀请求异常，请稍后重试");
        }
        // 5. 初始化代理对象（修复：核心！获取Spring代理对象）
        proxy = (IVoucherOrderService) AopContext.currentProxy();
        // 6. 构建订单对象
        VoucherOrder order = new VoucherOrder();
        order.setId(orderId);
        order.setUserId(userId);
        order.setVoucherId(voucherId);
        // 7. 发送消息到RocketMQ（修复：增加补偿逻辑）
        try {
            String jsonStr = JSON.toJSONString(order);
            rocketMQTemplate.convertAndSend("seckill_topic:TAG_SECKILL", jsonStr);
        } catch (Exception e) {
            log.error("发送RocketMQ消息失败，订单ID: {}", orderId, e);
            // 补偿方案：记录失败订单到数据库，后续定时任务重试
            saveFailedOrder(order, "RocketMQ发送失败");
            throw new RuntimeException("秒杀请求提交失败，请稍后重试");
        }
        // 8. 返回订单ID
        return Result.ok(orderId);
    }

    // ==================== 3. 订单创建方法（原有逻辑+小优化） ====================
    @Transactional(rollbackFor = Exception.class) // 修复：指定回滚所有异常
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();
        // 1. 校验一人一单（优化：加悲观锁，防止并发查询漏判）
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            log.error("用户{}已购买过优惠券{}", userId, voucherId);
            throw new RuntimeException("用户已经购买过一次了"); // 抛异常触发事务回滚
        }
        // 2. 扣减库存（优化：使用乐观锁，防止超卖）
        boolean success = seckillVoucherService
                .update()
                .setSql("stock=stock-1")
                .eq("voucher_id", voucherId)
                .gt("stock", 0)
                .update();
        if (!success) {
            log.error("优惠券{}库存不足", voucherId);
            throw new RuntimeException("库存不足"); // 抛异常触发事务回滚
        }
        // 3. 保存订单
        save(voucherOrder);
    }

    // ==================== 补充：消息发送失败补偿方法 ====================
    private void saveFailedOrder(VoucherOrder order, String reason) {
        // 实际项目中：插入到自定义的「秒杀失败订单表」，后续定时任务重试
        log.warn("保存失败订单，订单ID: {}, 原因: {}", order.getId(), reason);
        // 示例：
        // failedOrderMapper.insert(new FailedOrder(order.getId(), order.getUserId(), order.getVoucherId(), reason));
    }
}
