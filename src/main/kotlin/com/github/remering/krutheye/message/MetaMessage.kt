package com.github.remering.krutheye.message

import com.github.remering.krutheye.bean.ServerMeta


data class ServerMetaMessage(
    val meta: ServerMeta,
    val skinDomains: List<String>,
    val signaturePublicKey: String,
)