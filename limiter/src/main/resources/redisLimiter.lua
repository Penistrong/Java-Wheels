-- 获取唯一资源key
local key = KEYS[1]

-- ARGV中的参数依次为:
-- ARGV[1]: 限流毫秒时间戳
-- ARGV[2]: 限流时间窗口大小, 单位ms
-- ARGV[4]: 获取不到令牌时的最大等待时间, 单位ms
-- ARGV[5]: Redis Key过期时间, 单位s
-- ARGV[6]: 有序集成员元素值
local curTime = tonumber(ARGV[1])
local windowTime = tonumber(ARGV[2])
local limitCount = tonumber(ARGV[3])
local maxWaitTime = tonumber(ARGV[4])
local expireTime = tonumber(ARGV[5])
local value = ARGV[6]

-- 移除时间窗口之前的过期记录
redis.call("ZREMRANGEBYSCORE", key, 0, curTime - windowTime)

local curCount = tonumber(redis.call('ZCARD', key))
local nextCount = curCount + 1

-- 返回0表示超过限流阈值，没有获取到分布式锁
if nextCount > limitCount then
    return 0
else
    redis.call("ZADD", key, curTime, value)
    redis.call("EXPIRE", key, expireTime)
    return nextCount
end
