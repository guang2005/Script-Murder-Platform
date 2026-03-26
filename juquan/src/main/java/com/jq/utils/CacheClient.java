package com.jq.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;
    private static final Long NULL_VALUE_TTL = 2L;


    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }

    public void setWithLogicalExpire(String key,Object value,Long time,TimeUnit unit){
        //设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        //写入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(redisData));
    }

    /*public <R,ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,Long time,TimeUnit unit){
        String key=keyPrefix+id;
        //1.尝试从Redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断缓存是否存在
        if(StrUtil.isNotBlank(json)) { //判断字符串既不为null，也不是空字符串(""),且也不是空白字符
            //3.存在，返回商铺信息
            return JSONUtil.toBean(json, type);

        }
        //判断是否为空值
        if(json!=null){
            return null;
        }
        //4.不存在，根据id查询数据库
        R r = dbFallback.apply(id);
        //5.判断数据库中是否存在
        if(r==null){
            //6.不存在，返回错误状态码
            stringRedisTemplate.opsForValue().set(key,"",RedisConstants.CACHE_NULL_TTL,TimeUnit.MINUTES);
            return null;
        }
        //7.存在，写入redis，返回商铺信息
       this.set(key,r,time,unit);

        return r;

    }*/


    private static final ExecutorService CACHE_REBUILD_EXECUTOR= Executors.newFixedThreadPool(10);
    public <R,ID> R queryWithLogicalExpire(
            String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1. 从Redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);

        // 2. 判断缓存是否存在（非空字符串）
        if (StrUtil.isNotBlank(json)) {
            try {
                // 3. 反序列化为RedisData对象
                RedisData redisData = JSONUtil.toBean(json, RedisData.class);

                // 核心修复1：先判断data是否为null（空值缓存），避免空指针
                if (redisData.getData() == null) {
                    return null;
                }

                // 反序列化业务数据
                R data = JSONUtil.toBean((JSONObject) redisData.getData(), type);
                LocalDateTime expireTime = redisData.getExpireTime();

                // 4. 判断是否逻辑过期
                if (expireTime.isAfter(LocalDateTime.now())) {
                    // 4.1 未过期，直接返回数据
                    return data;
                } else {
                    // 4.2 已过期，需要缓存重建
                    String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
                    // 5. 获取互斥锁（核心修复2：加锁时设置过期时间，防止死锁）
                    boolean isLock = tryLock(lockKey); // 锁10秒过期
                    if (isLock) {
                        // 6. 获取锁成功，异步重建缓存
                        CACHE_REBUILD_EXECUTOR.submit(() -> {
                            try {
                                // 6.1 查询数据库
                                R newData = dbFallback.apply(id);
                                // 6.2 数据库有数据才更新缓存
                                if (newData != null) {
                                    // 6.2 写入Redis（更新逻辑过期时间）
                                    this.setWithLogicalExpire(key, newData, time, unit);
                                }
                            } catch (Exception e) {
                                // 核心修复3：异步线程捕获异常，避免线程池崩溃
                                System.err.println("缓存重建失败，key: " + key + "，异常：" + e.getMessage());
                            } finally {
                                // 6.3 释放锁
                                unLock(lockKey);
                            }
                        });
                    }
                    // 7. 返回过期数据（保证可用性，解决雪崩）
                    return data;
                }
            } catch (Exception e) {
                // 兜底：反序列化异常时，走数据库查询
                System.err.println("缓存反序列化失败，key: " + key + "，异常：" + e.getMessage());
            }
        }

        // 8. 缓存不存在/为空字符串（处理缓存穿透）
        // 8.1 缓存命中空值（""），直接返回null
        if (json != null) {
            return null;
        }

        // 8.2 缓存真的不存在，查询数据库
        R data = dbFallback.apply(id);
        // 8.3 数据库也不存在，写入空值缓存（核心修复4：统一写入空字符串，而非RedisData）
        if (data == null) {
            stringRedisTemplate.opsForValue().set(key, "", NULL_VALUE_TTL, TimeUnit.MINUTES);
            return null;
        }

        // 8.4 数据库存在，写入缓存（带逻辑过期）
        this.setWithLogicalExpire(key, data, time, unit);
        return data;
    }
    /**
     * 创建锁
     * @param key
     * @return
     */
    private boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 封闭锁
     * @param key
     */
    private void unLock(String key){
        stringRedisTemplate.delete(key);
    }
}
