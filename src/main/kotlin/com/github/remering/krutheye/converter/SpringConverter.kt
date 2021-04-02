package com.github.remering.krutheye.converter

import com.github.remering.krutheye.bean.TextureHash
import com.github.remering.krutheye.bean.TextureModel
import com.github.remering.krutheye.bean.YggdrasilUUID
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.core.convert.support.GenericConversionService
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class TextureModelConverter: Converter<String, TextureModel> {
    override fun convert(model: String) =
        when(model) {
            "slim" -> TextureModel.ALEX
            "default" -> TextureModel.STEVE
            "" -> TextureModel.STEVE
            else -> throw IllegalArgumentException("Can not map '${model}' to TextureModel")
        }
}

@Component
class TextureHashConverter: Converter<String, TextureHash>{
    override fun convert(source: String): TextureHash? {
        if (source.length != 64) return null
        return TextureHash(source)
    }

}
