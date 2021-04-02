package com.github.remering.krutheye.service;

import com.github.remering.krutheye.NoSuchSessionException
import com.github.remering.krutheye.bean.Session
import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.config.SessionConfigurationProperties
import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import org.springframework.data.redis.core.StringRedisTemplate

interface SessionService {
    fun join(accessToken: String, selectedProfile: YggdrasilProfileUUID, serverId: String, ip: String)
    @Throws(NoSuchSessionException::class)
    fun hasJoin(username: String, serverId: String, ip: String?): YggdrasilProfileEntity?
}

class RedisSessionService(
    private val properties: SessionConfigurationProperties,
    private val profileService: ProfileService,
    private val redisTemplate: StringRedisTemplate,
    private val tokenService: TokenService,
): SessionService {

    private fun getSession(serverId: String) = redisTemplate.boundHashOps<String, String>("session:$serverId")

    override fun join(accessToken: String, selectedProfile: YggdrasilProfileUUID, serverId: String, ip: String) {
        val (expireTime) = properties
        val sessionHash = getSession(serverId)
        val session = Session(accessToken, selectedProfile, serverId, ip)
        sessionHash.putAll(session)
        sessionHash.expire(expireTime)
    }

    override fun hasJoin(username: String, serverId: String, ip: String?): YggdrasilProfileEntity? {
        val sessionHash = getSession(serverId)
        val sessionResult = runCatching {
            Session(sessionHash.entries()!!)
        }
        val session = sessionResult.getOrNull()?:return null
        val profile = profileService.getByName(username)?:return null
        var valid = if (ip == null) true else ip == session.ip
        valid = valid && serverId == session.serverId
        if (!valid) return null
        val token = tokenService.getToken(session.accessToken)?:return null
        valid = token.boundedProfile == profile.uuid
        return if (valid) profile else null
    }
}