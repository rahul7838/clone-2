package com.quora.question.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//@Configuration
//class JacksonConfig {
//    @Bean
//    fun objectMapper(): ObjectMapper {
//        return ObjectMapper().apply {
//            registerModule(JavaTimeModule())
//            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//            enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
//        }
//    }
//}