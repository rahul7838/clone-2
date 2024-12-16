package com.quora.sync.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.SourceRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory

@Component
class ChangeEventHandler(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(ChangeEventHandler::class.java)

    fun handleChangeEvent(record: SourceRecord) {
        try {
            val value = record.value() as Struct
            val operation = value.getString("op")
            val source = value.getStruct("after")
            val collectionName = record.topic().split(".").last()

            val changeEvent = mapOf(
                "operation" to getOperationType(operation),
                "collection" to collectionName,
                "data" to objectMapper.readTree(source.toString())
            )

            val topic = "quora.changes.$collectionName"
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(changeEvent))
            logger.info("Published change event to topic: $topic")
        } catch (e: Exception) {
            logger.error("Error handling change event: ${e.message}", e)
        }
    }

    private fun getOperationType(op: String): String = when(op) {
        "c", "r" -> "CREATE"
        "u" -> "UPDATE"
        "d" -> "DELETE"
        else -> "UNKNOWN"
    }
} 