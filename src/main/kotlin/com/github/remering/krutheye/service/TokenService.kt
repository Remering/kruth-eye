package com.github.remering.krutheye.service

import com.github.remering.krutheye.InvalidTokenException
import com.github.remering.krutheye.bean.Token
import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.bean.YggdrasilUserUUID
import com.github.remering.krutheye.config.TokenConfigurationProperties
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration
import java.time.Instant
import java.util.*


interface TokenService {

    val properties: TokenConfigurationProperties

    fun acquire(clientToken: String?, userUUID: YggdrasilUserUUID, profileUUID: YggdrasilProfileUUID?): Token

    fun refresh(clientToken: String?, accessToken: String, selectedProfileUUID: YggdrasilProfileUUID?): Token?

    fun validate(clientToken: String?, accessToken: String): Boolean

    fun invalidate(accessToken: String)

    fun revokeAll(userUUID: YggdrasilUserUUID)

    fun getToken(accessToken: String): Token?
}

interface TokenGeneratorService {
    fun nextClientToken(): String
    fun nextAccessToken(): String
}

class RandomUUIDTokenGeneratorService: TokenGeneratorService {

    private fun nextToken() = YggdrasilProfileUUID().toString()
    override fun nextClientToken() = nextToken()
    override fun nextAccessToken() = nextToken()
}


class RedisTokenService(
    override val properties: TokenConfigurationProperties,
    private val redisTemplate: StringRedisTemplate,
    private val tokenGeneratorService: TokenGeneratorService,
): TokenService {

    private fun getUserAccessTokens(userUUID: String) = redisTemplate.boundZSetOps("accessTokens:${userUUID}")

    private fun getTokenHash(accessToken: String) = redisTemplate.boundHashOps<String, String>("accessToken:${accessToken}")

    override fun getToken(accessToken: String) = runCatching {
        Token(getTokenHash(accessToken).entries()!!)
    }.getOrNull()

    override fun acquire(clientToken: String?, userUUID: YggdrasilUserUUID, profileUUID: YggdrasilProfileUUID?): Token {
        val accessToken = tokenGeneratorService.nextAccessToken()
        val (expireTime, maxTokensPerUser) = properties
        val _clientToken = clientToken?:tokenGeneratorService.nextClientToken()
        val createAt = Instant.now()!!
        val token = Token(_clientToken, accessToken, profileUUID, userUUID)
        val tokenHash = getTokenHash(accessToken)
        tokenHash.putAll(token)
        tokenHash.expire(expireTime)

        val userAccessTokens= getUserAccessTokens(userUUID.toString())
        userAccessTokens.add(accessToken, createAt.epochSecond.toDouble())!!
        val size = userAccessTokens.size()!!
        if (size > maxTokensPerUser) userAccessTokens.removeRange(maxTokensPerUser.toLong() - 1, -1)
        userAccessTokens.expire(expireTime)
        return token
    }

    override fun refresh(clientToken: String?, accessToken: String, selectedProfileUUID: YggdrasilProfileUUID?): Token? {
        val newAccessToken = tokenGeneratorService.nextAccessToken()
        val (expireTime) = properties

        val tokenHash = getTokenHash(accessToken)
        if (clientToken != null && clientToken != tokenHash[Token::clientToken.name]) return null
        val userUUID = tokenHash[Token::user.name] ?: return null
        tokenHash.rename(newAccessToken)
        clientToken?.let { tokenHash.put(Token::clientToken.name, it) }
        selectedProfileUUID?.let { tokenHash.put(Token::boundedProfile.name, it.toString()) }
        tokenHash.expire(expireTime)
        val userAccessTokens = getUserAccessTokens(userUUID)
        userAccessTokens.remove(accessToken)
        userAccessTokens.expire(expireTime)
        return Token(tokenHash.entries()!!)
    }

    override fun validate(clientToken: String?, accessToken: String): Boolean {
        val tokenHash = getTokenHash(accessToken)
        return tokenHash[Token::clientToken.name] == clientToken
    }

    override fun invalidate(accessToken: String) {
        val tokenHash = redisTemplate.boundHashOps<String, String>(accessToken)
        val userUUID = tokenHash[Token::user.name]?:return
        tokenHash.expire(Duration.ZERO)
        val userAccessTokens = getUserAccessTokens(userUUID)
        userAccessTokens.remove(accessToken)
    }

    override fun revokeAll(userUUID: YggdrasilUserUUID) {
        val userAccessTokens = getUserAccessTokens(userUUID.toString())
        userAccessTokens.range(0, -1)?.forEach {
            getTokenHash(it).expire(Duration.ZERO)
        }
        userAccessTokens.expire(Duration.ZERO)
    }

}