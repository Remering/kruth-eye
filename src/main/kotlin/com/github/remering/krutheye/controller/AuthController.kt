package com.github.remering.krutheye.controller

import com.github.remering.krutheye.InvalidCredentialsException
import com.github.remering.krutheye.InvalidTokenException
import com.github.remering.krutheye.bean.ServerMeta
import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import com.github.remering.krutheye.message.*
import com.github.remering.krutheye.service.ProfileService
import com.github.remering.krutheye.service.RateLimiterService
import com.github.remering.krutheye.service.TokenService
import com.github.remering.krutheye.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


val EMAIL_REGEX = Regex("^[A-Za-z0-9]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$")


private data class Tuple3<T1, T2, T3>(
    val value1: T1,
    val value2: T2,
    val value3: T3,
)

private fun <T1, T2, T3> tupleOf(value1: T1, value2: T2, value3: T3) = Tuple3(value1, value2, value3)

@RestController
@RequestMapping("/authserver")
class AuthController(
    private val tokenService: TokenService,
    private val userService: UserService,
    private val profileService: ProfileService,
    private val serverMeta: ServerMeta,
    private val rateLimiterService: RateLimiterService,
) {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)!!

    private fun authenticate(username: String, password: String): Tuple3<YggdrasilUserEntity, YggdrasilProfileEntity?, List<YggdrasilProfileEntity>> {
        val userEntity: YggdrasilUserEntity

        val selectedProfile: YggdrasilProfileEntity?

        val profiles: List<YggdrasilProfileEntity>

        if (serverMeta.nonEmailLogin && EMAIL_REGEX.matches(username)) {
            userEntity = userService.authenticateByEmail(username, password) ?: throw InvalidCredentialsException()
            profiles = profileService.getByOwner(userEntity)
            selectedProfile = profiles.singleOrNull()
        } else {
            selectedProfile = profileService.authenticateByNamePassword(username, password)
            userEntity = selectedProfile?.user?: throw InvalidCredentialsException()
            profiles = profileService.getByOwner(userEntity)
        }

        rateLimiterService.check(userEntity.username)

        return tupleOf(userEntity, selectedProfile, profiles)
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody request: AuthenticateRequest): AuthenticateResponse {
        val (username, password, clientToken, requestUser) = request

        val (userEntity, selectedProfile, profiles) = authenticate(username, password)

        val token = tokenService.acquire(clientToken, userEntity.uuid, selectedProfile?.uuid)

        logger.info("username $username (uuid = ${userEntity.uuid}) authenticated with access token ${token.accessToken} and client token ${token.clientToken}")

        return AuthenticateResponse(
            token.accessToken,
            token.clientToken,
            profiles.map(YggdrasilProfileEntity::toMessage),
            selectedProfile?.toMessage(),
            if (requestUser) userEntity.toMessage() else null
        )
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): RefreshResponse {
        val (accessToken, clientToken, requestUser, selectedProfile) = request
        val newToken = tokenService.refresh(clientToken, accessToken, selectedProfile?.id)?:throw InvalidTokenException()
        val newSelectedProfileMessage = newToken.boundedProfile?.let {
            profileService.getByUUID(it)
        }?.toMessage()
        val userEntity = if (requestUser) userService.getByUUID(newToken.user) else null

        logger.info("refresh access token from $accessToken to ${newToken.accessToken} with ${newToken.clientToken}")

        return RefreshResponse(
            accessToken = newToken.accessToken,
            clientToken = newToken.clientToken,
            selectedProfile = newSelectedProfileMessage,
            user = userEntity?.toMessage()
        )
    }

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validate(@RequestBody request: ValidateRequest) {
        val (accessToken, clientToken) = request
        val valid = tokenService.validate(clientToken, accessToken)
        if (!valid) {
            logger.info("validation failed with access token $accessToken and client token $clientToken")
            throw InvalidTokenException()
        }
        logger.info("validate successfully with access token $accessToken and client token $clientToken")
    }


    @PostMapping("/invalidate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun invalidate(@RequestBody request: InvalidateRequest) {
        val (accessToken, _) = request
        tokenService.invalidate(accessToken)
        logger.info("invalidate with access token $accessToken")
    }

    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signout(@RequestBody request: SignOutRequest) {
        val (username, password) = request
        val (userEntity, _, _) = authenticate(username, password)
        tokenService.revokeAll(userEntity.uuid)
        logger.info("user $username (uuid = ${userEntity.uuid}) sign out")
    }
}