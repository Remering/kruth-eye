package com.github.remering.krutheye.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.core.io.Resource
import java.net.URI
import java.time.Duration

@ConfigurationProperties("kruth-eye.rsa")
@ConstructorBinding
data class RsaConfigurationProperties(
    @NestedConfigurationProperty
    val openssl: RsaOpensslConfigurationProperties = RsaOpensslConfigurationProperties(),
)

@ConstructorBinding
data class RsaOpensslConfigurationProperties(
    val privateKeyLocation: Resource? = null,
    val publicKeyLocation: Resource? = null,
)


@ConstructorBinding
@ConfigurationProperties("kruth-eye.feature")
data class FeatureConfigurationProperties(
    val loginWithProfileName: Boolean = true,
    val legacySkinApi: Boolean = false,
    val noMojangNameSpace: Boolean = false,
)

@ConstructorBinding
@ConfigurationProperties("kruth-eye.token")
data class TokenConfigurationProperties(
    val expireTime: Duration = Duration.ofMinutes(10),
    val maxTokensPerUser: Int = 5,
)


@ConstructorBinding
@ConfigurationProperties("kruth-eye.session")
data class SessionConfigurationProperties(
    val expireTime: Duration = Duration.ofMinutes(10)
)

@ConstructorBinding
@ConfigurationProperties("kruth-eyes.texture")
data class TextureConfigurationProperties(
    val textureBaseUri: URI = URI("http://localhost:8080"),
)


@ConstructorBinding
@ConfigurationProperties("kruth-eye.links")
data class ServerPageLinkConfigurationProperties(
    val homepage: URI? = null,
    val register: URI? = null,
)

@ConstructorBinding
@ConfigurationProperties("kruth-eye.meta")
data class ServerMetadataConfigurationProperties(
    val serverName: String? = null,
    val implementationName: String? = "Kruth Eye",
    val implementationVersion: KotlinVersion? = KotlinVersion(0, 1, 0),
    val skinDomains: List<String> = listOf(),
)

@ConstructorBinding
@ConfigurationProperties("kruth-eye.rate-limiter")
data class RateLimiterConfigurationProperties(
    val limitDuration: Duration = Duration.ofMillis(300L)
)