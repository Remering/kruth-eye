package com.github.remering.krutheye.entity

import com.github.remering.krutheye.bean.TextureHash
import com.github.remering.krutheye.bean.TextureModel
import com.github.remering.krutheye.bean.TextureType
import org.apache.ibatis.annotations.Param

data class YggdrasilTextureEntity(
    @Param("id") var id: Int? = null,
    @Param("hash") var hash: TextureHash,
    @Param("model") var model: TextureModel,
    @Param("type") var type: TextureType,
)