package com.reticentmonolith

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.litote.kmongo.id.jackson.IdJacksonModule

object JsonMapper {
    val defaultMapper: ObjectMapper = jacksonObjectMapper()

    init {
        defaultMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        defaultMapper.registerModule(JavaTimeModule())
        defaultMapper.registerModule(IdJacksonModule())
    }
}