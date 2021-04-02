package com.github.remering.krutheye.converter

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
import com.github.remering.krutheye.bean.YggdrasilUUID
import org.springframework.boot.jackson.JsonComponent

@JsonComponent
class YggdrasilDeserializer: StdScalarDeserializer<YggdrasilUUID>(YggdrasilUUID::class.java) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext) = YggdrasilUUID(parser.valueAsString)
}