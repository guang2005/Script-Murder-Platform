package com.jq.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Redis数据封装类
 * 用于存储需要缓存到Redis的数据及其过期时间
 */
@Data  // 使用Lombok注解自动生成getter、setter、toString等方法
public class RedisData {
    /**
     * 过期时间
     * 表示该数据在Redis中的过期时间点
     */
    private LocalDateTime expireTime;
    /**
     * 实际存储的数据
     * 可以是任何需要缓存的对象
     */
    private Object data;
}
