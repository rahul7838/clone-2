package com.quora.search.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "search_results")
data class SearchResult(
    @Id
    @Field(type = FieldType.Keyword)
    val id: String,

    @Field(type = FieldType.Keyword)
    val type: String, // "QUESTION" or "ANSWER"

    @Field(type = FieldType.Text, analyzer = "standard")
    val title: String?,

    @Field(type = FieldType.Text, analyzer = "standard")
    val content: String,

    @Field(type = FieldType.Keyword)
    val userId: String,

    @Field(type = FieldType.Keyword)
    val tags: List<String> = emptyList(),

    @Field(type = FieldType.Float)
    val score: Float = 0f,

    @Field(type = FieldType.Date)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Field(type = FieldType.Integer)
    val votes: Int = 0
) 