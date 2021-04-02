package com.github.remering.krutheye.converter

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.test.assertEquals


class UUIDEndianTest {



    // https://developpaper.com/uuid-storage-optimization-in-mybatis-mysql-environment/
    fun uuid2Bytes(uuid: UUID): ByteArray {
        val bytes = ByteArray(16)
        val mostBit = uuid.mostSignificantBits
        bytes[0] = (mostBit shr 56 and 0xFF).toByte()
        bytes[1] = (mostBit shr 48 and 0xFF).toByte()
        bytes[2] = (mostBit shr 40 and 0xFF).toByte()
        bytes[3] = (mostBit shr 32 and 0xFF).toByte()
        bytes[4] = (mostBit shr 24 and 0xFF).toByte()
        bytes[5] = (mostBit shr 16 and 0xFF).toByte()
        bytes[6] = (mostBit shr 8 and 0xFF).toByte()
        bytes[7] = (mostBit and 0xFF).toByte()
        //
        //
        val leastBit = uuid.leastSignificantBits
        bytes[8] = (leastBit shr 56 and 0xFF).toByte()
        bytes[9] = (leastBit shr 48 and 0xFF).toByte()
        bytes[10] = (leastBit shr 40 and 0xFF).toByte()
        bytes[11] = (leastBit shr 32 and 0xFF).toByte()
        bytes[12] = (leastBit shr 24 and 0xFF).toByte()
        bytes[13] = (leastBit shr 16 and 0xFF).toByte()
        bytes[14] = (leastBit shr 8 and 0xFF).toByte()
        bytes[15] = (leastBit and 0xFF).toByte()
        return bytes
    }

    fun bytes2UUIDInBuffer(bytes: ByteArray): Pair<Long, Long> {
        val byteBuffer = ByteBuffer.allocate(2 * 8)
        byteBuffer.order(ByteOrder.BIG_ENDIAN)
        byteBuffer.put(bytes)
        byteBuffer.flip()
        val msb = byteBuffer.long
        val lsb = byteBuffer.long
        return msb to lsb
    }

    fun bytes2UUID(bytes: ByteArray): Pair<Long, Long> {
        val msb = (bytes[0].toLong() and 0xFF shl 56
                or (bytes[1].toLong() and 0xFF shl 48)
                or (bytes[2].toLong() and 0xFF shl 40)
                or (bytes[3].toLong() and 0xFF shl 32)
                or (bytes[4].toLong() and 0xFF shl 24)
                or (bytes[5].toLong() and 0xFF shl 16)
                or (bytes[6].toLong() and 0xFF shl 8)
                or (bytes[7].toLong() and 0xFF))

        val lsb = (bytes[8].toLong() and 0xFF shl 56
                or (bytes[9].toLong() and 0xFF shl 48)
                or (bytes[10].toLong() and 0xFF shl 40)
                or (bytes[11].toLong() and 0xFF shl 32)
                or (bytes[12].toLong() and 0xFF shl 24)
                or (bytes[13].toLong() and 0xFF shl 16)
                or (bytes[14].toLong() and 0xFF shl 8)
                or (bytes[15].toLong() and 0xFF))
        return msb to lsb
    }

    fun uuid2BytesInBuffer(uuid: UUID): ByteArray {
        val byteBuffer = ByteBuffer.allocate(2 * 8)
        byteBuffer.order(ByteOrder.BIG_ENDIAN)
        byteBuffer.putLong(uuid.mostSignificantBits)
        byteBuffer.putLong(uuid.leastSignificantBits)
        return byteBuffer.array()
    }

    @ExperimentalUnsignedTypes
    fun ByteArray.toHexString() = joinToString { it.toUByte().toString(16) }


    @ExperimentalUnsignedTypes
    @Test
    fun `test write into byte buffer`() {
        val uuid = UUID.randomUUID()
        println("uuid: $uuid")
        val uuid2Bytes = uuid2Bytes(uuid)
        val uuid2BytesInBuffer = uuid2BytesInBuffer(uuid)
        println("uuid2Bytes = ${uuid2Bytes.toHexString()}")
        println("uuid2BytesInBuffer = ${uuid2BytesInBuffer.toHexString()}")
        assertArrayEquals(uuid2Bytes, uuid2BytesInBuffer)

    }

    private fun Long.toHexString() = java.lang.Long.toHexString(this)!!

    @Test
    fun `test read from byte buffer`() {
        val uuid = UUID.randomUUID()
        val uuidBytes = uuid2Bytes(uuid)
        println("uuid: $uuid")
        val (msb1, lsb1) = bytes2UUID(uuidBytes)
        println("bytes2UUID : [msb: ${msb1.toHexString()}, lsb: ${lsb1.toHexString()}]")
        val (msb2, lsb2) = bytes2UUIDInBuffer(uuidBytes)
        println("bytes2UUIDInBuffer: [msb: ${msb2.toHexString()}, lsb: ${lsb2.toHexString()}]")
        assertEquals(msb1, msb2)
        assertEquals(lsb1, lsb2)
    }
}