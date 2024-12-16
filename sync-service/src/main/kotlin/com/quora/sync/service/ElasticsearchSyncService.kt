package com.quora.sync.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates

@Service
class ElasticsearchSyncService(
    private val elasticsearchOperations: ElasticsearchOperations,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(ElasticsearchSyncService::class.java)

    @KafkaListener(topics = ["quora.changes.questions", "quora.changes.answers"])
    fun handleChangeEvent(message: String) {
        try {
            val event = objectMapper.readTree(message)
            val operation = event.get("operation").asText()
            val collection = event.get("collection").asText()
            val data = event.get("data")

            when (operation) {
                "CREATE", "UPDATE" -> {
                    elasticsearchOperations.save(
                        collection,
                        data.get("_id").asText(),
                        data.toString()
                    )
                    logger.info("Indexed document in $collection: ${data.get("_id").asText()}")
                }
                "DELETE" -> {
                    elasticsearchOperations.delete(
                        data.get("_id").asText(),
                        IndexCoordinates.of(collection)
                    )
                    logger.info("Deleted document from $collection: ${data.get("_id").asText()}")
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing change event: ${e.message}", e)
        }
    }
} 