package com.github.remering.krutheye.controller

import com.github.remering.krutheye.InvalidTokenException
import com.github.remering.krutheye.bean.TextureModel
import com.github.remering.krutheye.bean.TextureType
import com.github.remering.krutheye.bean.Token
import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.message.ProfilesRequest
import com.github.remering.krutheye.message.ProfilesResponse
import com.github.remering.krutheye.message.YggdrasilProfileMessage
import com.github.remering.krutheye.service.ProfileService
import com.github.remering.krutheye.service.TextureService
import com.github.remering.krutheye.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class ProfileController(
    private val profileService: ProfileService,
    private val textureService: TextureService,
    private val tokenService: TokenService,
) {

    private fun parseAuthorizationHeader(header: String?): Token {
        val authorization = header?.toLowerCase()?:throw InvalidTokenException()
        if (authorization.startsWith("bearer ")) {
            val accessToken = authorization.substring("bearer ".length)
            return tokenService.getToken(accessToken)?:throw InvalidTokenException()
        }
        throw InvalidTokenException()
    }


    @PostMapping("/api/profiles/minecraft")
    fun getProfilesByNames(@RequestBody @Validated profileNames: ProfilesRequest): ProfilesResponse {
        val messages = LinkedList<YggdrasilProfileMessage>()
        val uuids = profileService.getByNames(profileNames)
        for(i in profileNames.indices) {
            messages += YggdrasilProfileMessage(
                profileNames[i],
                uuids[i],
            )
        }
        return messages
    }

    @DeleteMapping("/api/user/profile/{uuid}/{textureType}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTexture(
        @PathVariable uuid: YggdrasilProfileUUID,
        @PathVariable textureType: TextureType,
        @RequestHeader authorization: String?,
    ) {
        val token = parseAuthorizationHeader(authorization)
        if (token.boundedProfile != uuid) throw InvalidTokenException()
        textureService.unlinkTexture(uuid, textureType)

    }

    @PutMapping("/api/user/profile/{uuid}/{textureType}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun uploadTexture(
        @PathVariable uuid: YggdrasilProfileUUID,
        @PathVariable textureType: TextureType,
        @RequestHeader authorization: String?,
        @RequestPart("file") imageFile: MultipartFile,
        @RequestPart("model") model: TextureModel = TextureModel.STEVE,
    ) {
        val token = parseAuthorizationHeader(authorization)
        if (token.boundedProfile != uuid) throw InvalidTokenException()
        textureService.addAndBindTexture(
            profileUUID = uuid,
            imageInputStream = imageFile.inputStream,
            model = model,
            type = textureType,
        )
    }
}