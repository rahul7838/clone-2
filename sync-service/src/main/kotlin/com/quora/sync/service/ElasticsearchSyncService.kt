package com.quora.sync.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import com.quora.sync.model.AnswerDocument
import com.quora.sync.model.QuestionDocument
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId

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
                    when (collection) {
                        "questions" -> {
                            val question = convertMongoDocumentToQuestion(data)
                            elasticsearchOperations.save(question)
                            logger.info("Indexed question: ${question.id}")
                        }
                        "answers" -> {
                            val answer = convertMongoDocumentToAnswer(data)
                            elasticsearchOperations.save(answer)
                            logger.info("Indexed answer: ${answer.id}")
                        }
                    }
                }
                "DELETE" -> {
                    val id = extractId(data.get("_id"))
                    when (collection) {
                        "questions" -> elasticsearchOperations.delete(id, QuestionDocument::class.java)
                        "answers" -> elasticsearchOperations.delete(id, AnswerDocument::class.java)
                    }
                    logger.info("Deleted document from $collection: $id")
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing change event: ${e.message}", e)
        }
    }

    private fun convertMongoDocumentToQuestion(node: JsonNode): QuestionDocument {
        return QuestionDocument(
            id = extractId(node.get("_id")),
            title = node.get("title")?.asText(),
            content = node.get("content")?.asText(),
            userId = node.get("userId")?.asText(),
            tags = node.get("tags")?.map { it.asText() } ?: emptyList(),
            createdAt = extractDate(node.get("createdAt")),
            updatedAt = extractDate(node.get("updatedAt")),
            answerCount = node.get("answerCount")?.asInt() ?: 0,
            viewCount = node.get("viewCount")?.asInt() ?: 0,
            votes = node.get("votes")?.asInt() ?: 0
        )
    }

    private fun convertMongoDocumentToAnswer(node: JsonNode): AnswerDocument {
        return AnswerDocument(
            id = extractId(node.get("_id")),
            questionId = node.get("questionId")?.asText(),
            content = node.get("content")?.asText(),
            userId = node.get("userId")?.asText(),
            createdAt = extractDate(node.get("createdAt")),
            updatedAt = extractDate(node.get("updatedAt")),
            votes = node.get("votes")?.asInt() ?: 0,
            isAccepted = node.get("isAccepted")?.asBoolean() ?: false
        )
    }

    private fun extractId(idNode: JsonNode?): String? {
        return when {
            idNode == null -> null
            idNode.has("\$oid") -> idNode.get("\$oid").asText()
            else -> idNode.asText()
        }
    }

    private fun extractDate(dateNode: JsonNode?): LocalDateTime {
        return when {
            dateNode == null -> LocalDateTime.now()
            dateNode.has("\$date") -> {
                val timestamp = dateNode.get("\$date").asLong()
                Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            }
            else -> LocalDateTime.now()
        }
    }
} 