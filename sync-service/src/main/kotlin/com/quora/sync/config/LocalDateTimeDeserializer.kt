package com.quora.sync.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        val node: JsonNode = p.codec.readTree(p)
        
        return when {
            // Handle MongoDB's extended JSON format
            node.isObject && node.has("\$date") -> {
                val dateNode = node.get("\$date")
                when {
                    dateNode.isNumber -> {
                        Instant.ofEpochMilli(dateNode.asLong())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    }
                    dateNode.isTextual -> {
                        Instant.parse(dateNode.asText())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    }
                    else -> throw IllegalArgumentException("Invalid date format in MongoDB document")
                }
            }
            // Handle direct timestamp
            node.isNumber -> {
                Instant.ofEpochMilli(node.asLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            }
            // Handle ISO-8601 string
            node.isTextual -> {
                LocalDateTime.parse(node.asText())
            }
            else -> throw IllegalArgumentException("Unable to deserialize LocalDateTime from: $node")
        }
    }
} 