package com.github.remering.krutheye.service

import com.github.remering.krutheye.bean.TextureHash
import com.github.remering.krutheye.bean.TextureModel
import com.github.remering.krutheye.bean.TextureType
import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.entity.YggdrasilTextureEntity
import com.github.remering.krutheye.mapper.YggdrasilTextureMapper
import com.github.remering.krutheye.message.PropertyEntry
import com.github.remering.krutheye.message.TextureDataMessage
import com.github.remering.krutheye.message.TextureMessage
import com.github.remering.krutheye.message.YggdrasilProfileMessage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriBuilder
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import javax.imageio.ImageIO

@Service
class TextureService(
    val textureMapper: YggdrasilTextureMapper,
    val codecService: CodecService,
    val textureUriBuilder: UriBuilder,
) {

    fun attachTexture(profileUUID: YggdrasilProfileUUID, profileMessage: YggdrasilProfileMessage, signed: Boolean = true) {
        val textures = textureMapper.getByProfileUUID(profileUUID)
        val textureDataMessages = hashMapOf<TextureType, TextureDataMessage>()
        for (texture in textures) {
            textureDataMessages.put(texture.type, TextureDataMessage(
                model = texture.model,
                url = getURL(texture.hash)
            ))
        }
        val textureMessage = TextureMessage(
            profileName = profileMessage.name,
            profileId = profileMessage.id,
            textures = textureDataMessages
        )

        val textureMessageEncoded = codecService.encodeTextureMessage(textureMessage)
        profileMessage.properties += PropertyEntry(
            name = "textures",
            value = textureMessageEncoded,
            signature = if (signed) codecService.signBase64ToBase64(textureMessageEncoded) else null
        )
    }

    fun getURL(textureHash: TextureHash): URL = textureUriBuilder.build(textureHash.toString()).toURL()

    fun getPngInputStream(textureHash: TextureHash) = textureMapper.getPngByHash(textureHash)

    fun unlinkTexture(profileUUID: YggdrasilProfileUUID, textureType: TextureType) = textureMapper.unlinkTexture(profileUUID, textureType)

    @Transactional
    fun addTexture(imageInputStream: InputStream, model: TextureModel, type: TextureType): YggdrasilTextureEntity {
        val image = ImageIO.read(imageInputStream)
        val hash = codecService.computeTextureImageHash(image)
        val existent = textureMapper.getByHash(hash)
        if (existent != null) return existent
        val buf = ByteArrayOutputStream()
        ImageIO.write(image, "png", buf)
        val textureEntity = YggdrasilTextureEntity(
            hash = hash,
            model = model,
            type = type
        )
        textureMapper.add(textureEntity)
        textureMapper.addPng(textureEntity.id!!, buf.toByteArray())
        return textureEntity
    }

    fun bindTexture(profileUUID: YggdrasilProfileUUID, textureId: Int) {
        textureMapper.bindTexture(profileUUID, textureId)
    }

    @Transactional
    fun addAndBindTexture(profileUUID: YggdrasilProfileUUID, imageInputStream: InputStream, model: TextureModel, type: TextureType): YggdrasilTextureEntity {
        val entity = addTexture(imageInputStream, model, type)
        bindTexture(profileUUID, entity.id!!)
        return entity
    }
}