package com.github.remering.krutheye.mapper

import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.bean.YggdrasilUUID
import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import org.apache.ibatis.annotations.Param

interface YggdrasilProfileMapper {

    fun getById(@Param("id") id: Int): YggdrasilProfileEntity?
    fun getByUUID(@Param("uuid") uuid: YggdrasilProfileUUID): YggdrasilProfileEntity?
    fun getByName(@Param("name") name: String): YggdrasilProfileEntity?
    fun getByNames(@Param("names") names: List<String>): List<YggdrasilProfileUUID>
    fun getByOwnerId(@Param("ownerID") id: Int): List<YggdrasilProfileEntity>
    fun authenticateByNamePassword(@Param("name") name: String, @Param("password") password: ByteArray): YggdrasilProfileEntity?
    fun getName(@Param("uuid") uuid: YggdrasilProfileUUID): String
    fun add(@Param("entity") entity: YggdrasilProfileEntity): Int
}