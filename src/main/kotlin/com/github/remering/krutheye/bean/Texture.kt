package com.github.remering.krutheye.bean

import java.math.BigInteger

enum class TextureModel(val model: String){
    STEVE("default"), ALEX("slim");

    override fun toString() = model

}

enum class TextureType {
    SKIN, CAPE
}

class TextureHash private constructor (private val hashString: String, val hashBytes: ByteArray) {
    constructor(hashBytes: ByteArray): this(BigInteger(hashBytes).toString(16), hashBytes)
    constructor(hashString: String): this(hashString, hashString.toBigInteger(16).toByteArray())
    override fun toString() = hashString
}
