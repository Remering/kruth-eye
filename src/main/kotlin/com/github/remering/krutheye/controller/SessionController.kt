package com.github.remering.krutheye.controller

import com.github.remering.krutheye.InvalidProfileException
import com.github.remering.krutheye.NoSuchProfileException
import com.github.remering.krutheye.NoSuchSessionException
import com.github.remering.krutheye.bean.YggdrasilUUID
import com.github.remering.krutheye.message.JoinRequest
import com.github.remering.krutheye.message.YggdrasilProfileMessage
import com.github.remering.krutheye.service.ProfileService
import com.github.remering.krutheye.service.SessionService
import com.github.remering.krutheye.service.TextureService
import com.github.remering.krutheye.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.ServletRequest

@RestController
@RequestMapping("/sessionserver/session/minecraft")
class SessionController(
    private val textureService: TextureService,
    private val profileService: ProfileService,
    private val sessionService: SessionService,
    private val tokenService: TokenService,
) {

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun join(@RequestBody request: JoinRequest, httpRequest: ServletRequest) {
        val (accessToken, selectedProfile, serverId) = request
        val token = tokenService.getToken(accessToken)?:throw InvalidProfileException()
        if (token.boundedProfile != selectedProfile) throw InvalidProfileException()
        sessionService.join(accessToken, selectedProfile, serverId, httpRequest.remoteHost)
    }

    @GetMapping("/hasJoined")
    fun hasJoined(
        @RequestParam("username") username: String,
        @RequestParam("serverId") serverId: String,
        @RequestParam("ip") ip: String?
    ): YggdrasilProfileMessage {
        val profileEntity = sessionService.hasJoin(username, serverId, ip)?:throw NoSuchSessionException()
        return YggdrasilProfileMessage(profileEntity).apply {
            textureService.attachTexture(profileEntity.uuid, this)
        }
    }

    @GetMapping("/profile/{uuid:[a-f0-9]{32}}")
    fun profile(
        @PathVariable("uuid") uuid: YggdrasilUUID,
        @RequestParam("unsigned") unsigned: String? = null
    ): YggdrasilProfileMessage {
        val signed = "false" == unsigned
        val profile = profileService.getByUUID(uuid) ?: throw NoSuchProfileException()
        return YggdrasilProfileMessage(profile).apply {
            textureService.attachTexture(profile.uuid, this, signed)
        }
    }

}