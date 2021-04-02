package com.github.remering.krutheye.service;

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.remering.krutheye.bean.TextureHash
import com.github.remering.krutheye.message.TextureDataMessage
import com.github.remering.krutheye.message.TextureMessage
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.*
import javax.imageio.ImageIO

@Service
class CodecService(
    val signature: Signature,
    val privateKey: PrivateKey,
    val publicKey: PublicKey,
    val objectMapper: ObjectMapper,
) {

    private val md5Digest = MessageDigest.getInstance("MD5")!!

    val pemPublicKeyString by lazy {
        "-----BEGIN PUBLIC KEY-----${Base64.getMimeEncoder(76, byteArrayOf('\n'.toByte())).encodeToString(publicKey.encoded)}-----END PUBLIC KEY-----"
    }

    fun sign(data: ByteArray) = signature.run {
        initSign(privateKey)
        sign()!!
    }

    fun signToBase64(data: ByteArray) = encodeToBase64(sign(data))

    fun signBase64ToBase64(data: String) = signToBase64(data.toByteArray())

    fun encodeToBase64(bytes: ByteArray) = Base64.getEncoder().encodeToString(bytes)!!

    fun encodePassword(password: String) = md5Digest.run {
        update(password.toByteArray(StandardCharsets.UTF_8))
        md5Digest.digest()!!
    }

    fun encodeToJsonBytes(obj: Any) = objectMapper.writeValueAsBytes(obj)!!

    fun encodeTextureMessage(textureMessage: TextureMessage) = encodeToBase64(encodeToJsonBytes(textureMessage))

    private fun putInt(array: ByteArray, offset: Int, x: Int) {
        array[offset + 0] = (x shr 24 and 0xff).toByte()
        array[offset + 1] = (x shr 16 and 0xff).toByte()
        array[offset + 2] = (x shr 8 and 0xff).toByte()
        array[offset + 3] = (x shr 0 and 0xff).toByte()
    }

    private val sha256Digest = runCatching {
        MessageDigest.getInstance("SHA-256")!!
    }.getOrThrow()

    fun computeTextureStreamHash(textureStream: InputStream) = computeTextureImageHash(ImageIO.read(textureStream))

    fun computeTextureImageHash(img: BufferedImage): TextureHash {
        val width = img.width
        val height = img.height
        val buf = ByteArray(4096)
        putInt(buf, 0, width)
        putInt(buf, 4, height)
        var pos = 8
        for (x in 0 until width) {
            for (y in 0 until height) {
                putInt(buf, pos, img.getRGB(x, y))
                if (buf[pos + 0] == 0.toByte()) {
                    buf[pos + 3] = 0
                    buf[pos + 2] = buf[pos + 3]
                    buf[pos + 1] = buf[pos + 2]
                }
                pos += 4
                if (pos == buf.size) {
                    pos = 0
                    sha256Digest.update(buf, 0, buf.size)
                }
            }
        }
        if (pos > 0) {
            sha256Digest.update(buf, 0, pos)
        }
        val sha256 = sha256Digest.digest()
        return TextureHash(sha256)
    }

}
