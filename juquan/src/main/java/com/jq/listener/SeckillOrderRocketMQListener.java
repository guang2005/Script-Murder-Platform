package com.jq.listener;

import com.alibaba.fastjson.JSON;
import com.jq.entity.VoucherOrder;
import com.jq.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 必须交给Spring容器管理，否则无法监听
@RocketMQMessageListener(
        topic = "seckill_topic",          // 与生产者发送的Topic一致
        selectorExpression = "TAG_SECKILL", // 与生产者发送的Tag一致
        consumerGroup = "seckill-consumer-group" // 唯一消费者组名，自定义
)
public class SeckillOrderRocketMQListener implements RocketMQListener<String> {

    // 注入你的优惠券订单服务（包含handleVoucherOrder核心方法）
    @Autowired
    private IVoucherOrderService voucherOrderService;

    /**
     * RocketMQ消息监听核心方法：消息到达后自动执行
     * @param message 生产者发送的订单JSON字符串
     */
    @Override
    public void onMessage(String message) {
        // 1. 日志记录接收到的消息
        log.info("RocketMQ监听到秒杀订单消息：{}", message);

        try {
            // 2. 将JSON字符串解析为VoucherOrder对象（和你业务实体一致）
            VoucherOrder voucherOrder = JSON.parseObject(message, VoucherOrder.class);

            // 3. 调用你写的handleVoucherOrder方法处理订单（加分布式锁+创建订单）
            voucherOrderService.handleVoucherOrder(voucherOrder);

            // 4. 记录处理成功日志
            log.info("秒杀订单处理完成，订单ID：{}", voucherOrder.getId());

        } catch (Exception e) {
            // 5. 消费失败处理：打印日志+触发重试（RocketMQ会自动重试配置的次数）
            log.error("处理秒杀订单失败，消息内容：{}，异常信息：{}", message, e.getMessage(), e);
            // 抛出异常让RocketMQ重试（如果不想重试，可注释此行，自行处理）
            throw new RuntimeException("订单消费失败，触发RocketMQ重试", e);
        }
    }
}
