package com.github.remering.krutheye.bean

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonInclude
import java.net.URI

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServerPageLink(
    val homepage: URI?,
    val register: URI?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServerMeta(
    val serverName: String?,
    val implementationName: String?,
    val implementationVersion: KotlinVersion?,
    val links: ServerPageLink?,
    @JsonAlias("feature.non_email_login")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    val nonEmailLogin: Boolean,
    @JsonAlias("feature.legacy_skin_api")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    val legacySkinApi: Boolean,
    @JsonAlias("feature.no_mongjang_namespace")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    val noMojangNamespace: Boolean,
)