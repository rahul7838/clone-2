package com.quora.question.service

import com.quora.question.exception.ResourceNotFoundException
import com.quora.question.model.Answer
import com.quora.question.repository.AnswerRepository
import com.quora.question.repository.QuestionRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AnswerService(
    private val answerRepository: AnswerRepository,
    private val questionRepository: QuestionRepository,
) {
    
    @Transactional
    @Cacheable(value = ["answers"], key = "#{id}")
    fun createAnswer(questionId: String, answer: Answer): Answer {
        // Verify question exists
        val question = questionRepository.findById(questionId)
            .orElseThrow { ResourceNotFoundException("Question not found with id: $questionId") }
        
        // Save the answer
        val savedAnswer = answerRepository.save(
            answer.copy(questionId = questionId)
        )
        
        // Update question's answer count
        questionRepository.save(
            question.copy(
                answerCount = question.answerCount + 1,
                updatedAt = LocalDateTime.now()
            )
        )
        
        // Cache the answer
//        redisTemplate.opsForValue().set("answer:${savedAnswer.id}", savedAnswer)
        
        return savedAnswer
    }
    
    @Cacheable(value = ["answers"], key = "#id")
    fun getAnswer(id: String): Answer {
        return answerRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Answer not found with id: $id") }
    }
    
    @Cacheable(value = ["question-answers"], key = "#questionId")
    fun getAnswersForQuestion(questionId: String, pageable: Pageable): Page<Answer> {
        return answerRepository.findByQuestionId(questionId, pageable)
    }
    
    fun getAnswersByUser(userId: String, pageable: Pageable): Page<Answer> {
        return answerRepository.findByUserId(userId, pageable)
    }
    
    @Transactional
    @CacheEvict(value = ["answers", "question-answers"], key = "#id")
    fun updateAnswer(id: String, updatedAnswer: Answer): Answer {
        val existingAnswer = answerRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Answer not found with id: $id") }
        
        val answerToUpdate = updatedAnswer.copy(
            id = existingAnswer.id,
            questionId = existingAnswer.questionId,
            createdAt = existingAnswer.createdAt,
            updatedAt = LocalDateTime.now()
        )
        
        return answerRepository.save(answerToUpdate)
    }
    
    @Transactional
    @CacheEvict(value = ["answers", "question-answers"], key = "#id")
    fun deleteAnswer(id: String) {
        val answer = answerRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Answer not found with id: $id") }
        
        // Update question's answer count
        val question = questionRepository.findById(answer.questionId)
            .orElseThrow { ResourceNotFoundException("Question not found with id: ${answer.questionId}") }
        
        questionRepository.save(
            question.copy(
                answerCount = (question.answerCount - 1).coerceAtLeast(0),
                updatedAt = LocalDateTime.now()
            )
        )
        
        // Delete the answer
        answerRepository.deleteById(id)
        
        // Remove from cache
//        redisTemplate.delete("answer:$id")
    }
    
    @Transactional
    fun acceptAnswer(answerId: String, userId: String): Answer {
        val answer = answerRepository.findById(answerId)
            .orElseThrow { ResourceNotFoundException("Answer not found with id: $answerId") }
        
        // Verify the question exists and user is the question owner
        val question = questionRepository.findById(answer.questionId)
            .orElseThrow { ResourceNotFoundException("Question not found with id: ${answer.questionId}") }
        
        if (question.userId != userId) {
            throw IllegalStateException("Only the question owner can accept an answer")
        }
        
        // Update the answer as accepted
        val acceptedAnswer = answer.copy(isAccepted = true)
        return answerRepository.save(acceptedAnswer)
    }
    
    @Transactional
    fun voteAnswer(answerId: String, upvote: Boolean): Answer {
        val answer = answerRepository.findById(answerId)
            .orElseThrow { ResourceNotFoundException("Answer not found with id: $answerId") }
        
        val updatedAnswer = answer.copy(
            votes = answer.votes + if (upvote) 1 else -1
        )
        
        return answerRepository.save(updatedAnswer)
    }
}
/*
This implementation includes:
Core CRUD Operations:
Create answer (with question answer count update)
Read answer(s)
Update answer
Delete answer (with question answer count update)
Additional Features:
Answer acceptance
Voting system
User-specific answer retrieval
Question-specific answer retrieval
Caching Strategy:
Redis caching for individual answers
Cache eviction on updates/deletes
Caching for question-answer relationships
Transaction Management:
@Transactional for operations affecting multiple entities
Atomic updates for answer counts
Error Handling:
Resource not found exceptions
Permission validation
Data consistency checks
Performance Optimizations:
Pagination support
Efficient caching
Optimistic locking for concurrent updates

*/