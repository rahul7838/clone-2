package com.quora.question.repository

import com.quora.question.model.Answer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : MongoRepository<Answer, String> {
    fun findByQuestionId(questionId: String, pageable: Pageable): Page<Answer>
    
    fun findByUserId(userId: String, pageable: Pageable): Page<Answer>
    
    @Query("{ \$text: { \$search: ?0 } }")
    fun searchAnswers(searchText: String, pageable: Pageable): Page<Answer>
    
    fun countByQuestionId(questionId: String): Long
} 