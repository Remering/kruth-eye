package com.github.remering.krutheye.service

import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import com.github.remering.krutheye.mapper.YggdrasilProfileMapper
import org.springframework.stereotype.Service

@Service
class ProfileService(
    val mapper: YggdrasilProfileMapper,
    val codecService: CodecService
) {

    fun authenticateByNamePassword(name: String, password: String): YggdrasilProfileEntity? {
        val encodedPassword = codecService.encodePassword(password)
        return mapper.authenticateByNamePassword(name, encodedPassword)
    }

    fun getByUUID(uuid: YggdrasilProfileUUID) = mapper.getByUUID(uuid)

    fun getByOwner(user: YggdrasilUserEntity) = mapper.getByOwnerId(user.id!!)

    fun getByNames(names: List<String>) = mapper.getByNames(names)

    fun getByName(name: String) = mapper.getByName(name)

    fun getName(uuid: YggdrasilProfileUUID) = mapper.getName(uuid)

}