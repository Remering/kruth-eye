package com.github.remering.krutheye.service

import com.github.remering.krutheye.bean.YggdrasilProfileUUID
import com.github.remering.krutheye.bean.YggdrasilUserUUID
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import com.github.remering.krutheye.mapper.YggdrasilUserMapper
import org.springframework.stereotype.Service

@Service
class UserService(
    private val mapper: YggdrasilUserMapper,
    private val codecService: CodecService
) {

    fun authenticateByEmail(email: String, password: String): YggdrasilUserEntity? {
        val passwordEncoded = codecService.encodePassword(password)
        return mapper.getByUsernamePassword(email, passwordEncoded)
    }

    fun getByUUID(uuid: YggdrasilUserUUID) = mapper.getByUUID(uuid)

}