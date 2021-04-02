package com.github.remering.krutheye.mapper

import com.github.remering.krutheye.bean.*
import com.github.remering.krutheye.entity.YggdrasilTextureEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.io.InputStream

@Mapper
interface YggdrasilTextureMapper {

    fun getById(@Param("id") id: Int): YggdrasilTextureEntity?

    fun getByHash(@Param("hash") hash: TextureHash): YggdrasilTextureEntity?

    fun getByProfileUUID(@Param("profileUUID") profileUUID: YggdrasilProfileUUID): List<YggdrasilTextureEntity>

    fun getPngById(@Param("id") id: Int): InputStream?

    fun getPngByTextureId(@Param("textureId") textureId: Int): InputStream?

    fun getPngByHash(@Param("textureHash") textureHash: TextureHash): InputStream?

    fun unlinkTexture(@Param("profileUUID") profileUUID: YggdrasilProfileUUID, @Param("textureType") textureType: TextureType): Int

    fun add(@Param("entity") entity: YggdrasilTextureEntity): Int

    fun addPng(@Param("textureId") textureId: Int, @Param("image") image: ByteArray): Int

    fun bindTexture(@Param("profileUUID") profileUUID: YggdrasilProfileUUID, @Param("textureId") textureId: Int): Int
}