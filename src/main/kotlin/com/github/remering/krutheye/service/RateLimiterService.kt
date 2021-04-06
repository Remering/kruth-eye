package com.github.remering.krutheye.service

import com.github.remering.krutheye.InvalidCredentialsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration
import java.time.Instant


interface RateLimiterService {
    val limitDuration: Duration
    @Throws(InvalidCredentialsException::class)
    fun check(username: String)
}

class RedisRateLimiterService(
    override val limitDuration: Duration,
    private val redisTemplate: StringRedisTemplate,
): RateLimiterService {

    private val log = LoggerFactory.getLogger(RedisRateLimiterService::class.java)!!

    init {
        redisTemplate.setEnableTransactionSupport(true)
    }

    override fun check(username: String) {
        val key = "timing:$username"
        redisTemplate.watch(key)
        val valueOps = redisTemplate.boundValueOps(key)
        redisTemplate.multi()
        valueOps.setIfAbsent(Instant.now().toString())
        valueOps.expire(limitDuration)
        val result = redisTemplate.exec().firstOrNull()
        if (result != true) {
            log.info("reject authentication with username {}", username)
            throw InvalidCredentialsException()
        }
    }

}