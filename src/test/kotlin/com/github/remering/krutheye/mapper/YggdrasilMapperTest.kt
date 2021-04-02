package com.github.remering.krutheye.mapper

import com.github.remering.krutheye.entity.YggdrasilProfileEntity
import com.github.remering.krutheye.entity.YggdrasilUserEntity
import com.github.remering.krutheye.service.CodecService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.InetAddress
import kotlin.test.assertNotNull

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class YggdrasilMapperTest {


    @Autowired
    lateinit var userMapper: YggdrasilUserMapper

    @Autowired
    lateinit var profileMapper: YggdrasilProfileMapper

    @Autowired
    lateinit var codecService: CodecService

//    @BeforeAll
//    fun beforeAll() {
//        mapper.removeByUsername("test1@example.com")
//        mapper.removeByUsername("test2@example.com")
//        mapper.removeByUsername("test3@example.com")
//    }

    @Test
    fun `add example users`() {
        for (i in 1..3) {
            val username = "test$i@example.com"
            val password = "$i".repeat(6)
            val encodedPassword = codecService.encodePassword(password)
            val user = YggdrasilUserEntity(
                username = username,
                registerIp = InetAddress.getLocalHost(),
            )
            userMapper.add(user, encodedPassword)
        }
    }

    @Test
    fun `get example users`() {
        for (i in 1..3) {
            val username = "test$i@example.com"
            assertNotNull(userMapper.getByUsername(username))
        }
    }

    @Test
    fun `add example profiles`() {
        var profile = YggdrasilProfileEntity(
            name = "character1",
            user = userMapper.getByUsername("test2@example.com")!!
        )
        profileMapper.add(profile)
        profile = YggdrasilProfileEntity(
            name = "character2",
            user = userMapper.getByUsername("test3@example.com")!!
        )
        profileMapper.add(profile)
        profile = YggdrasilProfileEntity(
            name = "character3",
            user = userMapper.getByUsername("test3@example.com")!!
        )
        profileMapper.add(profile)
    }


}