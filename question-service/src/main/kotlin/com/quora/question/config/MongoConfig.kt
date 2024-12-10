package com.quora.question.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.TextIndexDefinition
import javax.annotation.PostConstruct

@Configuration
class MongoConfig(private val mongoTemplate: MongoTemplate) {
    
    @PostConstruct
    fun initIndexes() {
        // Text indexes for search
        val questionTextIndex = TextIndexDefinition.builder()
            .onField("title", 2.0f)
            .onField("content")
            .onField("tags")
            .build()
        
        mongoTemplate.indexOps("questions").ensureIndex(questionTextIndex)
        
        // Regular indexes
        mongoTemplate.indexOps("questions")
            .ensureIndex(Index().on("userId", Index.Direction.ASC))
            .ensureIndex(Index().on("tags", Index.Direction.ASC))
            .ensureIndex(Index().on("createdAt", Index.Direction.DESC))
    }
} 