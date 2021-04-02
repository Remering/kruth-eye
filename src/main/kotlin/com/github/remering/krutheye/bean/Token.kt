package com.github.remering.krutheye.bean

data class Token(
    val clientToken: String,
    val accessToken: String,
    val boundedProfile: YggdrasilProfileUUID?,
    val user: YggdrasilUserUUID,
    val map: Map<String, String?> = mapOf(
        "clientToken" to clientToken,
        "accessToken" to accessToken,
        "boundedProfile" to boundedProfile?.toString(),
        "user" to user.toString(),
    )

): Map<String, String?> by map {
    constructor(map: Map<String, String?>): this(
        clientToken = map["clientToken"]!!,
        accessToken =  map["accessToken"]!!,
        boundedProfile = map["boundedProfile"]?.let { YggdrasilProfileUUID(it) },
        user = YggdrasilProfileUUID(map["user"]!!),
        map = map,
    )
}

data class Session(
    val accessToken: String,
    val selectedProfile: YggdrasilProfileUUID,
    val serverId: String,
    val ip: String,
    val map: Map<String, String> = mapOf(
        "accessToken" to accessToken,
        "selectedProfile" to selectedProfile.toString(),
        "serverId" to serverId,
        "ip" to ip
    )
): Map<String, String?> by map {
    constructor(map: Map<String, String>): this(
        accessToken = map["accessToken"]!!,
        selectedProfile = YggdrasilProfileUUID(map["selectedProfile"]!!),
        serverId = map["serverId"]!!,
        ip = map["ip"]!!,
        map = map,
    )
}