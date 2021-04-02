package com.github.remering.krutheye.converter

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.github.remering.krutheye.bean.YggdrasilUUID
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jackson.JsonComponent
import org.springframework.core.convert.support.GenericConversionService
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

@JsonComponent
class YggdrasilUUIDJacksonSerializer: StdScalarSerializer<YggdrasilUUID>(YggdrasilUUID::class.java) {
    override fun serialize(value: YggdrasilUUID, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.toString())
    }
}

@JsonComponent
class KotlinVersionSerializer: StdScalarSerializer<KotlinVersion>(KotlinVersion::class.java){
    override fun serialize(value: KotlinVersion, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.toString())
    }

}

class YggdrasilUUIDRedisSerializer: RedisSerializer<YggdrasilUUID> {

    override fun getTargetType() = YggdrasilUUID::class.java

    private val threadLocalByteBuffer by lazy {
        ThreadLocal.withInitial {
            ByteBuffer.allocate(8 * 2).order(ByteOrder.BIG_ENDIAN)
        }
    }

    override fun serialize(yggdrasilUUID: YggdrasilUUID?): ByteArray? {
        val uuid = yggdrasilUUID?.uuid?:return null
        val byteBuffer = threadLocalByteBuffer.get()
        byteBuffer.clear()
        byteBuffer.putLong(uuid.mostSignificantBits)
        byteBuffer.putLong(uuid.leastSignificantBits)
        return byteBuffer.array()
    }

    override fun deserialize(bytes: ByteArray?): YggdrasilUUID? {
        requireNotNull(bytes)
        val byteBuffer = threadLocalByteBuffer.get()
        byteBuffer.clear()
        byteBuffer.put(bytes)
        byteBuffer.flip()
        val msb = byteBuffer.long
        val lsb = byteBuffer.long
        return YggdrasilUUID(UUID(msb, lsb))
    }

}

