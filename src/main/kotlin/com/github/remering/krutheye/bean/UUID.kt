package com.github.remering.krutheye.bean

import java.util.*

data class YggdrasilUUID(val uuid: UUID) {
    private lateinit var unsignedUUID: String

    constructor(uuidString: String) : this(
        when(uuidString.length) {
            36 -> UUID.fromString(uuidString)
            32 -> UUID.fromString(uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20, 32))
            else -> throw IllegalArgumentException("Invalid UUID: $uuidString")
        }
    )

    constructor() : this(UUID.randomUUID())

    override fun toString(): String {
        if (!this::unsignedUUID.isInitialized) {
            unsignedUUID = uuid.toString().replace("-", "")
        }
        return unsignedUUID
    }
}

typealias YggdrasilUserUUID = YggdrasilUUID

typealias YggdrasilProfileUUID = YggdrasilUUID

typealias YggdrasilTextureUUID = YggdrasilUUID

fun uuidFromProfileName(profileName: String) = YggdrasilProfileUUID(UUID.nameUUIDFromBytes("OfflinePlayer:$profileName".toByteArray()))