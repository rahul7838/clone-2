package com.quora.question.repository

import com.quora.question.model.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : MongoRepository<Question, String> {
    fun findByUserId(userId: String, pageable: Pageable): Page<Question>
    
    fun findByTags(tag: String, pageable: Pageable): Page<Question>
    
    @Query("{ 'tags': { \$in: ?0 } }")
    fun findByTagsIn(tags: List<String>, pageable: Pageable): Page<Question>
    
    @Query("{ \$text: { \$search: ?0 } }")
    fun searchQuestions(searchText: String, pageable: Pageable): Page<Question>
} 