package com.github.remering.krutheye.service;

import com.github.remering.krutheye.NoSuchSessionException
import com.github.remering.krutheye.bean.Session
import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.config.SessionConfigurationProperties
import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(RedisSessionService::class.java)

    private fun getSession(serverId: String) = redisTemplate.boundHashOps<String, String>("session:$serverId")

    override fun join(accessToken: String, selectedProfile: YggdrasilProfileUUID, serverId: String, ip: String) {
        val (expireTime) = properties
        val sessionHash = getSession(serverId)
        val session = Session(accessToken, selectedProfile, serverId, ip)
        sessionHash.putAll(session)
        sessionHash.expire(expireTime)
        logger.info("Received join request from $ip with selected profile uuid = ${selectedProfile.uuid}, server id = $serverId, access token = $accessToken")
    }

    override fun hasJoin(username: String, serverId: String, ip: String?): YggdrasilProfileEntity? {
        val sessionHash = getSession(serverId)
        val sessionResult = runCatching {
            Session(sessionHash.entries()!!)
        }
        val session = sessionResult.getOrNull()

        if (session == null) {
            logger.info("Rejected has join request from $ip with server id = $serverId, username = $username: No such session")
            return null
        }

        val profile = profileService.getByName(username)

        if (profile == null) {
            logger.info("Rejected has join request from $ip with server id = $serverId, username = $username: No such profile")
            return null
        }

        var valid = if (ip == null) true else ip == session.ip

        if (!valid) {
            logger.info("Rejected has join request from $ip with server id = $serverId, username = $username, access token = ${session.accessToken}: IP is incorrect, expect ${session.ip}")
            return null
        }

        valid = serverId == session.serverId
        if (!valid) {
            logger.info("Rejected has join request from $ip with server id = $serverId, username = $username, access token = ${session.accessToken}: Server id is incorrect, expect ${session.serverId}")
            return null
        }
        val token = tokenService.getToken(session.accessToken)
        if (token == null) {
            logger.info("Rejected has join request from $ip with server id = $serverId, username = $username, access token = ${session.accessToken}: No such access token")
            return null
        }

        if (token.boundedProfile != profile.uuid) {
            logger.info("Rejected has join request from $ip with server id = $serverId, username = $username, access token = ${session.accessToken}: Incorrect profile with uuid ${profile.uuid} was bounded, expect profile uuid = ${token.boundedProfile}")
        }

        return profile
    }
}