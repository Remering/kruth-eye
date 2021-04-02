package com.github.remering.krutheye.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.remering.krutheye.bean.*
import com.github.remering.krutheye.entity.MessageEntity
import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import com.github.remering.krutheye.service.CodecService
import java.net.URL
import java.time.Instant
import java.util.*


interface EntityMessage<E: MessageEntity<E, M>, M: EntityMessage<E, M>>

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PropertyEntry(
    val name: String,
    val value: String,
    val signature: String? = null
)

data class YggdrasilUserMessage(
    val id: YggdrasilUserUUID,
    val properties: MutableList<PropertyEntry> = LinkedList()
): EntityMessage<YggdrasilUserEntity, YggdrasilUserMessage> {
    constructor(entity: YggdrasilUserEntity): this(entity.uuid) {
        properties += PropertyEntry("name", entity.username)
        properties += PropertyEntry("email", entity.email)
    }
}

data class YggdrasilProfileMessage @JvmOverloads constructor(
    val name: String,
    val id: YggdrasilProfileUUID,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val properties: MutableList<PropertyEntry> = LinkedList<PropertyEntry>().also {
        it += PropertyEntry("uploadableTextures", "skin,cape")
    }
): EntityMessage<YggdrasilProfileEntity, YggdrasilProfileMessage> {
    constructor(
        entity: YggdrasilProfileEntity,
        includeProperties: Boolean = true,
    ): this(entity.name, entity.uuid,
        LinkedList<PropertyEntry>()
    ) {
        if (!includeProperties) return
        properties += PropertyEntry("uploadableTextures", "skin,cape")
    }
}

data class TextureDataMessage(
    @JsonIgnore
    val model: TextureModel,
    @JsonIgnore
    val url: URL,
    val metadata: Map<String, String> = hashMapOf(
        "model" to model.toString(),
        "url" to url.toExternalForm(),
    ),
)

data class TextureMessage(
    val profileName: String,
    val profileId: YggdrasilProfileUUID,
    val textures: Map<TextureType, TextureDataMessage>,
    val timestamp: Instant = Instant.now(),
)