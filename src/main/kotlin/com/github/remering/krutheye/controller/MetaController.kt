package com.github.remering.krutheye.controller

import com.github.remering.krutheye.bean.ServerMeta
import com.github.remering.krutheye.config.ServerMetadataConfigurationProperties
import com.github.remering.krutheye.message.ServerMetaMessage
import com.github.remering.krutheye.service.CodecService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MetaController(
    val meta: ServerMeta,
    val codecService: CodecService,
    val properties: ServerMetadataConfigurationProperties
) {

    val metaMessage by lazy {
        ServerMetaMessage(
            meta = meta,
            properties.skinDomains,
            codecService.pemPublicKeyString
        )
    }

    @GetMapping("/")
    fun index() = metaMessage
}