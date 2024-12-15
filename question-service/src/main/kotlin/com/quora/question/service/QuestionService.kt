package com.quora.question.service

import com.quora.question.exception.ResourceNotFoundException
import com.quora.question.model.Question
import com.quora.question.repository.QuestionRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val elasticsearchTemplate: ElasticsearchTemplate
) {

    @Transactional
    fun createQuestion(question: Question): Question {
        val savedQuestion = questionRepository.save(question)
        elasticsearchTemplate.save(savedQuestion)
        return savedQuestion
    }

    @Cacheable(value = ["questions"], key = "#id")
    fun getQuestion(id: String): Question {
        return questionRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Question not found with id: $id") }
    }

    fun getAllQuestions(pageable: Pageable): Page<Question>? {
        return questionRepository.findAll(pageable)
    }

    fun getQuestionsByUser(userId: String, pageable: Pageable): Page<Question>? {
        return questionRepository.findByUserId(userId, pageable)
    }

    @CachePut(value = ["questions"], key = "#id")
    fun updateQuestion(id: String, updatedQuestion: Question): Question {
        // Find existing question
        val existingQuestion = questionRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Question not found with id: $id") }
        
        // Create updated question maintaining the original id and creation time
        val questionToUpdate = updatedQuestion.copy(
            id = existingQuestion.id,
            createdAt = existingQuestion.createdAt,
            updatedAt = LocalDateTime.now()
        )
        
        // Save to MongoDB
        val savedQuestion = questionRepository.save(questionToUpdate)
        
        // Update in Elasticsearch
        elasticsearchTemplate.save(savedQuestion)
        
        return savedQuestion
    }

    @CacheEvict(value = ["questions"], key = "#id")
    fun deleteQuestion(id: String) {
        questionRepository.deleteById(id)
    }

} 