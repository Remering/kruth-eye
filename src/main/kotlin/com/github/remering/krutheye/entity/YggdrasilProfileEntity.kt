package com.github.remering.krutheye.entity

import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.bean.uuidFromProfileName
import com.github.remering.krutheye.message.EntityMessage
import com.github.remering.krutheye.message.YggdrasilProfileMessage
import org.apache.ibatis.annotations.Param
import java.time.Instant

data class YggdrasilProfileEntity @JvmOverloads constructor(
    @Param("id") var id: Int? = null,
    @Param("name") var name: String = "",
    @Param("uuid") var uuid: YggdrasilProfileUUID = uuidFromProfileName(name),
    @Param("createAt") var createAt: Instant = Instant.now(),
    @Param("user") var user: YggdrasilUserEntity = YggdrasilUserEntity(),
): MessageEntity<YggdrasilProfileEntity, YggdrasilProfileMessage> {
    override fun toMessage() = YggdrasilProfileMessage(this)
}