package com.quora.question.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.elasticsearch.annotations.Document as ESDocument

@Document(collection = "questions")
@ESDocument(indexName = "questions")
// @ESDocument(indexName = "questions")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Question(
    @Id
    val id: String? = null,
    val title: String,
    val content: String,
    val userId: String,
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val answerCount: Int = 0,
    val viewCount: Int = 0,
    val votes: Int = 0
) 