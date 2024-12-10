package com.quora.question.controller

import com.quora.question.model.Question
import com.quora.question.service.QuestionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/questions")
class QuestionController(private val questionService: QuestionService) {
    
    @PostMapping
    fun createQuestion(
        @RequestHeader("Authorization") token: String,
        @RequestBody question: Question
    ): ResponseEntity<Question> {
        return ResponseEntity.ok(questionService.createQuestion(question))
    }
    
    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): ResponseEntity<Question> {
        return ResponseEntity.ok(questionService.getQuestion(id))
    }
    
    @GetMapping
    fun getAllQuestions(pageable: Pageable): ResponseEntity<Page<Question>> {
        return ResponseEntity.ok(questionService.getAllQuestions(pageable))
    }
    
    @GetMapping("/user/{userId}")
    fun getQuestionsByUser(
        @PathVariable userId: String,
        pageable: Pageable
    ): ResponseEntity<Page<Question>> {
        return ResponseEntity.ok(questionService.getQuestionsByUser(userId, pageable))
    }
    
    @PutMapping("/{id}")
    fun updateQuestion(
        @PathVariable id: String,
        @RequestBody question: Question,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Question> {
        return ResponseEntity.ok(questionService.updateQuestion(id, question))
    }
    
    @DeleteMapping("/{id}")
    fun deleteQuestion(
        @PathVariable id: String,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Unit> {
        questionService.deleteQuestion(id)
        return ResponseEntity.ok().build()
    }
} 