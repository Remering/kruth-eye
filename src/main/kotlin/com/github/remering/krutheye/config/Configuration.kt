package com.github.remering.krutheye.config

import com.github.remering.krutheye.bean.ServerMeta
import com.github.remering.krutheye.bean.ServerPageLink
import com.github.remering.krutheye.controller.LegacyTextureController
import com.github.remering.krutheye.message.ServerMetaMessage
import com.github.remering.krutheye.service.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.security.*
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


@Configuration
@EnableConfigurationProperties(RsaConfigurationProperties::class)
class RsaSignatureConfiguration(private val properties: RsaConfigurationProperties) {

    val rsaKeyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider())!!

    @get:Bean
    val signature: Signature
        get() = Signature.getInstance("SHA1withRSA")

    @ConditionalOnProperty(
        prefix = "kruth-eye.rsa.openssl",
        value = ["enable", "private-key-location", "public-key-location"]
    )
    @Configuration
    @AutoConfigureOrder(100000)
    inner class RsaOpensslKeyPairConfiguration {
        private val privateKeyLocation = properties.openssl.privateKeyLocation
        private val publicKeyLocation = properties.openssl.publicKeyLocation

        init {
            requireNotNull(privateKeyLocation)
            requireNotNull(publicKeyLocation)
        }

        @Bean
        fun opensslPublicKey(): PrivateKey {
            val pemObj = PemReader(privateKeyLocation!!.inputStream.reader()).use {
                it.readPemObject()
            }

            val keySpec: KeySpec = PKCS8EncodedKeySpec(pemObj.content)
            return rsaKeyFactory.generatePrivate(keySpec)
        }

        @Bean
        fun opensslPrivateKey(): PublicKey {
            val pemObj = PemReader(publicKeyLocation!!.inputStream.reader()).use {
                it.readPemObject()
            }

            val keySpec: KeySpec = X509EncodedKeySpec(pemObj.content)
            return rsaKeyFactory.generatePublic(keySpec)
        }


    }

    @Configuration
    @AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnMissingBean(value = [PrivateKey::class, PublicKey::class])
    inner class DefaultRsaKeyPairConfiguration {

        val keyPair by lazy {
            val keyGenerator = KeyPairGenerator.getInstance("RSA")
            keyGenerator.initialize(4096)
            keyGenerator.generateKeyPair()!!
        }

        @Bean
        fun defaultPrivateKey() = keyPair.private!!


        @Bean
        fun defaultPublicKey() = keyPair.public!!

    }
}

@Configuration
@EnableConfigurationProperties(TokenConfigurationProperties::class)
class TokenConfiguration {

    val randomUUIDTokenGeneratorService by lazy { RandomUUIDTokenGeneratorService() }

    @Bean
    @ConditionalOnMissingBean(TokenGeneratorService::class)
    fun defaultTokenGeneratorService() = randomUUIDTokenGeneratorService

    @Configuration
    @ConditionalOnClass(StringRedisTemplate::class)
    @Import(RedisTokenService::class)
    inner class RedisTokenConfiguration
}


@Configuration
@EnableConfigurationProperties(SessionConfigurationProperties::class)
class SessionConfiguration {

    @Configuration
    @ConditionalOnBean(StringRedisTemplate::class)
    @AutoConfigureAfter(RedisAutoConfiguration::class)
    @Import(RedisSessionService::class)
    inner class RedisSessionConfiguration
}


@Configuration
@EnableConfigurationProperties(RateLimiterConfigurationProperties::class)
class RateLimiterConfiguration(val properties: RateLimiterConfigurationProperties) {

    @Configuration
    @ConditionalOnBean(StringRedisTemplate::class)
    @AutoConfigureAfter(RedisAutoConfiguration::class)
    @Import(RedisRateLimiterService::class)
    inner class RedisRateLimiterConfiguration
}

@Configuration
@EnableConfigurationProperties(TextureConfigurationProperties::class)
class TextureConfiguration(val properties: TextureConfigurationProperties) {

    @Bean
    fun textureBaseUriBuilder(): UriBuilder =
        UriComponentsBuilder.fromUri(properties.textureBaseUri)
            .pathSegment("textures", "{hash}")
}

@Configuration
@EnableConfigurationProperties(value = [
    FeatureConfigurationProperties::class,
    ServerPageLinkConfigurationProperties::class,
    ServerMetadataConfigurationProperties::class,
])
class ServerMetaConfiguration(
    val feature: FeatureConfigurationProperties,
    val serverPageLink: ServerPageLinkConfigurationProperties,
    val serverMetadata: ServerMetadataConfigurationProperties,
) {

    @Bean
    fun serverMeta() = ServerMeta(
        serverName = serverMetadata.serverName,
        implementationName = serverMetadata.implementationName,
        implementationVersion = serverMetadata.implementationVersion,
        links = ServerPageLink(
            homepage = serverPageLink.homepage,
            register = serverPageLink.register,
        ),
        nonEmailLogin = feature.loginWithProfileName,
        legacySkinApi = feature.legacySkinApi,
        noMojangNamespace = feature.noMojangNameSpace,
    )
}

@Configuration
class FeatureConfiguration {

    @Configuration
    @ConditionalOnProperty("kruth-eye.feature.legacySkinApi")
    @Import(LegacyTextureController::class)
    class LegacySkinConfiguration
}