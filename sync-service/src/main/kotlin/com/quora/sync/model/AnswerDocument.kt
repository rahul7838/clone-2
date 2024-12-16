package com.quora.sync.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.quora.sync.config.LocalDateTimeDeserializer
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "answers")
data class AnswerDocument(
    @Id
    val id: String? = null,
    
    @Field(type = FieldType.Keyword)
    val questionId: String? = null,
    
    @Field(type = FieldType.Text, analyzer = "standard")
    val content: String? = null,
    
    @Field(type = FieldType.Keyword)
    val userId: String? = null,
    
    @Field(type = FieldType.Date)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Field(type = FieldType.Date)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Field(type = FieldType.Integer)
    val votes: Int = 0,
    
    @Field(type = FieldType.Boolean)
    val isAccepted: Boolean = false
) 