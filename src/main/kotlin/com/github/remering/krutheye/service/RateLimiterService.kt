package com.github.remering.krutheye.service

import com.github.remering.krutheye.InvalidCredentialsException
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

    override fun check(username: String) {
        val key = "timing:$username"
        redisTemplate.watch(key)
        val valueOps = redisTemplate.boundValueOps(key)
        valueOps.setIfAbsent(Instant.now().toString())
        valueOps.expire(limitDuration)
        val result = redisTemplate.exec().firstOrNull()
        if (result != true) throw InvalidCredentialsException()
    }

}