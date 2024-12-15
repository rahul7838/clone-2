package com.quora.question.repository

import com.quora.question.model.Question
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

//@Repository
//class QuestionCacheRepository(
//    private val redisTemplate: RedisTemplate<String, Question>
//) {
//    companion object {
//        private const val CACHE_PREFIX = "question:"
//        private const val CACHE_TTL_HOURS = 24L
//    }
//
//    fun saveToCache(question: Question) {
//        question.id?.let { id ->
//            val key = generateKey(id)
//            redisTemplate.opsForValue().set(key, question, CACHE_TTL_HOURS, TimeUnit.HOURS)
//        }
//    }
//
//    fun getFromCache(id: String): Question? {
//        val key = generateKey(id)
//        return redisTemplate.opsForValue().get(key)
//    }
//
//    fun removeFromCache(id: String) {
//        val key = generateKey(id)
//        redisTemplate.delete(key)
//    }
//
//    private fun generateKey(id: String) = "$CACHE_PREFIX$id"
//}