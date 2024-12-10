package com.quora.question.service

import com.quora.question.model.Question
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val redisTemplate: RedisTemplate<String, Question>,
    private val elasticsearchTemplate: ElasticsearchTemplate
) {
    
    fun createQuestion(question: Question): Question {
        val savedQuestion = questionRepository.save(question)
        elasticsearchTemplate.save(savedQuestion)
        return savedQuestion
    }

    @Cacheable(value = ["questions"], key = "#id")
    fun getQuestion(id: String): Question {
        return questionRepository.findById(id)
            .orElseThrow { RuntimeException("Question not found") }
    }
} 