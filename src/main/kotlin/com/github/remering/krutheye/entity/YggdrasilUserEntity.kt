package com.github.remering.krutheye.entity

import com.github.remering.krutheye.bean.YggdrasilUUID
import com.github.remering.krutheye.bean.YggdrasilUserUUID
import com.github.remering.krutheye.message.EntityMessage
import com.github.remering.krutheye.message.YggdrasilUserMessage
import org.apache.ibatis.annotations.Param
import java.net.Inet4Address
import java.net.InetAddress
import java.time.Instant
import java.util.*

data class YggdrasilUserEntity (
    @Param("id") var id: Int? = null,
    @Param("username") var username: String = "",
    @Param("uuid") var uuid: YggdrasilUserUUID = YggdrasilUUID(),
    @Param("registerIp") var registerIp: InetAddress = InetAddress.getLocalHost(),
    @Param("passwordChangedAt") var passwordChangedAt: Instant = Instant.now(),
    @Param("emailVerified") var emailVerified: Boolean = false,
): MessageEntity<YggdrasilUserEntity, YggdrasilUserMessage> {
    override fun toMessage() = YggdrasilUserMessage(this)
    val email: String
        get() = username
}

