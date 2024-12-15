package com.quora.question.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@Configuration
class MongoConfig(private val mongoTemplate: MongoTemplate) {
    
    @PostConstruct
    fun initIndexes() {
        // Text indexes for search
       /*  
       val questionTextIndex = TextIndexDefinition.builder()
            .onField("title", 2.0f)
            .onField("content")
            .onField("tags")
            .build()
        mongoTemplate.indexOps("questions").ensureIndex(questionTextIndex)
        */
        
        // Regular indexes - corrected compound index syntax
        mongoTemplate.indexOps("questions")
            .ensureIndex(
                Index()
                    .on("userId", Sort.Direction.ASC)
                    .on("tags", Sort.Direction.ASC)
                    .on("createdAt", Sort.Direction.DESC)
            )
    
    }
} 
