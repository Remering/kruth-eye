package com.github.remering.krutheye.controller

import com.github.remering.krutheye.NoSuchTextureException
import com.github.remering.krutheye.bean.TextureHash
import com.github.remering.krutheye.mapper.YggdrasilProfileMapper
import com.github.remering.krutheye.mapper.YggdrasilTextureMapper
import com.github.remering.krutheye.service.TextureService
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException
import java.time.Duration

@RestController
class TextureController(
    private val textureService: TextureService
) {

    @GetMapping("/textures/{hash:[0-9a-f]{64}}")
    fun getTexture(@PathVariable hash: TextureHash): ResponseEntity<Resource> {
        val pngInputStream = textureService.getPngInputStream(hash)?: throw NoSuchTextureException()

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .eTag(hash.toString())
            .cacheControl(CacheControl.maxAge(Duration.ofDays(30L)).cachePublic())
            .body(InputStreamResource(pngInputStream))
    }

}

@ResponseBody
class LegacyTextureController(
    val textureService: TextureService,
    val textureController: TextureController,
    val textureMapper: YggdrasilTextureMapper,
    val profileMapper: YggdrasilProfileMapper,
) {
    @GetMapping("/skins/MinecraftSkins/{username}.png")
    fun getTexture(@PathVariable username: String): ResponseEntity<Resource> {
        throw NotImplementedError()
    }
}