-- ==================== 1. 参数列表 ====================
-- 1.1 优惠券id
local voucherId = ARGV[1]
-- 1.2 用户id
local userId = ARGV[2]
-- 1.3 订单id
local orderId = ARGV[3]

-- ==================== 2. 数据key ====================
-- 2.1 库存key
local stockKey = 'seckill:stock:' .. voucherId
-- 2.2 订单key（用户集合，防止重复下单）
local orderKey = 'seckill:order:' .. voucherId

-- ==================== 3. 核心业务逻辑 ====================
-- 3.1 防护：校验参数合法性（空参数返回-1，匹配Java的"其他错误"逻辑）
if not voucherId or not userId or not orderId then
    return -1  -- 参数非法，返回-1（统一归为Java的"秒杀请求异常"）
end

-- 3.2 获取库存（处理key不存在的情况，默认0）
local stock = tonumber(redis.call('get', stockKey)) or 0
-- 3.3 判断库存是否充足
if stock <= 0 then
    return 1  -- 库存不足，返回1（和Java的r==1完全匹配）
end

-- 3.4 判断用户是否已下单（重复下单校验）
if redis.call('sismember', orderKey, userId) == 1 then
    return 2  -- 重复下单，返回2（和Java的r==2完全匹配）
end

-- 3.5 扣减库存（原子操作）
redis.call('incrby', stockKey, -1)
-- 3.6 记录用户下单（原子操作）
redis.call('sadd', orderKey, userId)

-- 3.7 移除原RabbitMQ的Stream消息（适配RocketMQ逻辑）
-- redis.call('xadd','stream.orders','*', 'userId',userId,'voucherId',voucherId,'id',orderId)

-- 3.8 全部成功，返回0（和Java的r==0匹配）
return 0
