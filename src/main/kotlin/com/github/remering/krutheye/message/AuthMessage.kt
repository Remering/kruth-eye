package com.github.remering.krutheye.message

import com.fasterxml.jackson.annotation.JsonInclude
import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class AuthenticateRequest(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
    val clientToken: String? = null,
    val requestUser: Boolean = false,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthenticateResponse(
    val accessToken: String,
    val clientToken: String,
    val availableProfiles: List<YggdrasilProfileMessage>,
    val selectedProfile: YggdrasilProfileMessage? = null,
    val user: YggdrasilUserMessage? = null,
)

@Validated
data class RefreshRequest(
    @NotBlank
    val accessToken: String,
    val clientToken: String? = null,
    val requestUser: Boolean = false,
    val selectedProfile: YggdrasilProfileMessage? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RefreshResponse(
    val accessToken: String,
    val clientToken: String,
    val selectedProfile: YggdrasilProfileMessage? = null,
    val user: YggdrasilUserMessage? = null,
)

@Validated
data class ValidateRequest(
    @NotBlank
    val accessToken: String,
    val clientToken: String? = null,
)


@Validated
data class InvalidateRequest(
    @NotBlank
    val accessToken: String,
    val clientToken: String? = null
)

@Validated
data class SignOutRequest(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
)


