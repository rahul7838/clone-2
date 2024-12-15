package com.quora.question.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "answers")
data class Answer(
    @Id
    val id: String? = null,
    val questionId: String,
    val content: String,
    val userId: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val votes: Int = 0,
    val isAccepted: Boolean = false
) 