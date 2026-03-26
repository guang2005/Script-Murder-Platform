package com.jq.utils;

/**
 * Redis常量类，用于存储所有Redis相关的键名和过期时间等配置信息
 */
public class RedisConstants {
    // 登录验证码相关常量
    public static final String LOGIN_CODE_KEY = "login:code:";  // 登录验证码的键前缀
    public static final Long LOGIN_CODE_TTL = 2L;              // 登录验证码的过期时间（分钟）
    // 登录用户相关常量
    public static final String LOGIN_USER_KEY = "login:token:"; // 登录用户的键前缀
    public static final Long LOGIN_USER_TTL = 36000L;          // 登录用户的过期时间（秒）
    // 缓存空值相关常量
    public static final Long CACHE_NULL_TTL = 2L;              // 空值的缓存过期时间（分钟）
    // 商店缓存相关常量
    public static final Long CACHE_SHOP_TTL = 30L;             // 商店缓存的过期时间（分钟）
    public static final String CACHE_SHOP_KEY = "cache:shop:"; // 商店缓存的键前缀
    // 商店锁相关常量
    public static final String LOCK_SHOP_KEY = "lock:shop:";   // 商店锁的键前缀
    public static final Long LOCK_SHOP_TTL = 10L;              // 商店锁的过期时间（秒）
    // 秒杀相关常量
    public static final String SECKILL_STOCK_KEY = "seckill:stock:"; // 秒杀库存的键前缀
    // 博客点赞相关常量
    public static final String BLOG_LIKED_KEY = "blog:liked:";  // 博客点赞的键前缀
    // 推流相关常量
    public static final String FEED_KEY = "feed:";             // 推流的键前缀
    // 商店地理位置相关常量
    public static final String SHOP_GEO_KEY = "shop:geo:";     // 商店地理位置的键前缀
    // 用户签到相关常量
    public static final String USER_SIGN_KEY = "sign:";        // 用户签到的键前缀
}
