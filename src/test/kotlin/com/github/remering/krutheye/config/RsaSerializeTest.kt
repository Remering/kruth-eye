package com.github.remering.krutheye.config

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher



@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableConfigurationProperties(RsaConfigurationProperties::class)
class RsaOpensslTest {

    @Autowired
    lateinit var properties: RsaConfigurationProperties

    @Value("\${kruth-eye.rsa.openssl.enable}")
    var enable: Boolean = false

    val rsaKeyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider())!!

    lateinit var privateKey: PrivateKey

    lateinit var publicKey: PublicKey

    val signature = Signature.getInstance("SHA1withRSA")!!

    @BeforeAll
    fun init() {

        val (privateKeyLocation, publicKeyLocation) = properties.openssl

        assumeTrue(enable)
        assumeTrue(privateKeyLocation != null)
        assumeTrue(publicKeyLocation != null)

        var pemObj = PemReader(privateKeyLocation!!.inputStream.reader()).use {
            it.readPemObject()
        }

        var keySpec: KeySpec = PKCS8EncodedKeySpec(pemObj.content)
        privateKey = rsaKeyFactory.generatePrivate(keySpec)

        pemObj = PemReader(publicKeyLocation!!.inputStream.reader()).use {
            it.readPemObject()
        }

        keySpec = X509EncodedKeySpec(pemObj.content)

        publicKey = rsaKeyFactory.generatePublic(keySpec)
    }

    @Test
    fun `sign and verify`() {
        val content = UUID.randomUUID().toString()
        signature.initSign(privateKey)
        signature.update(content.toByteArray())

        val signature = signature.sign()

        this.signature.initVerify(publicKey)
        this.signature.update(content.toByteArray())
        assertTrue(this.signature.verify(signature))
    }

    @Test
    fun `encrypt and decrypt`() {
        val cipher = Cipher.getInstance("RSA")
        val content = UUID.randomUUID().toString()
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)
        val encryptedBytes = cipher.doFinal(content.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, publicKey)
        val decryptedContent = String(cipher.doFinal(encryptedBytes))
        assertEquals(content, decryptedContent)
    }
}