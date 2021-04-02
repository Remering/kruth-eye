package com.github.remering.krutheye.mapper

import com.github.remering.krutheye.bean.YggdrasilUserUUID
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface YggdrasilUserMapper {

    fun getById(@Param("id") id: Int): YggdrasilUserEntity?

    fun getByUUID(@Param("uuid") uuid: YggdrasilUserUUID): YggdrasilUserEntity?

    fun getByUsername(@Param("username") username: String): YggdrasilUserEntity?

    fun getByUsernamePassword(@Param("username") username: String, @Param("password") password: ByteArray): YggdrasilUserEntity?

    fun add(@Param("user") user: YggdrasilUserEntity, @Param("password") password: ByteArray): Int

    fun removeByUsername(@Param("username") username: String): Int

}