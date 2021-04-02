package com.github.remering.krutheye.message

import com.github.remering.krutheye.bean.YggdrasilUUID
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

@Validated
data class JoinRequest(
    @NotEmpty
    val accessToken: String,
    @NotEmpty
    val selectedProfile: YggdrasilUUID,
    @NotEmpty
    val serverId: String
)