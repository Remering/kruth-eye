package com.github.remering.krutheye.service

import com.github.remering.krutheye.bean.TextureModel
import com.github.remering.krutheye.bean.TextureType
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import java.net.URL

@SpringBootTest
class YggdrasilTextureServiceTest {

    @Autowired
    lateinit var textureService: TextureService

    @Autowired
    lateinit var profileService: ProfileService

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun `add example textures`() {
        var profile = profileService.getByName("character1")
        assumeTrue(profile != null)

        var imageResource = resourceLoader.getResource("classpath:textures/eromanga/masamune_lzumi.png")
        assumeTrue(imageResource.exists())
        textureService.addAndBindTexture(
            profileUUID = profile!!.uuid,
            imageInputStream = imageResource.inputStream,
            model = TextureModel.STEVE,
            type = TextureType.SKIN,
        )

        imageResource = resourceLoader.getResource("https://textures.minecraft.net/texture/5786fe99be377dfb6858859f926c4dbc995751e91cee373468c5fbf4865e7151")
        assumeTrue(imageResource.exists())
        textureService.addAndBindTexture(
            profileUUID = profile.uuid,
            imageInputStream = imageResource.inputStream,
            model = TextureModel.STEVE,
            type = TextureType.CAPE,
        )

        profile = profileService.getByName("character2")
        assumeTrue(profile != null)
        imageResource = resourceLoader.getResource("classpath:textures/eromanga/elf_yamada.png")
        assumeTrue(imageResource.exists())
        textureService.addAndBindTexture(
            profileUUID = profile!!.uuid,
            imageInputStream = imageResource.inputStream,
            model = TextureModel.ALEX,
            type = TextureType.SKIN
        )

        profile = profileService.getByName("character3")
        assumeTrue(profile != null)
        imageResource = resourceLoader.getResource("https://textures.minecraft.net/texture/5786fe99be377dfb6858859f926c4dbc995751e91cee373468c5fbf4865e7151")
        assumeTrue(imageResource.exists())
        textureService.addAndBindTexture(
            profileUUID = profile!!.uuid,
            imageInputStream = imageResource.inputStream,
            model = TextureModel.STEVE,
            type = TextureType.CAPE
        )
    }
}