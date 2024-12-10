package com.quora.question.controller

import com.quora.question.model.Answer
import com.quora.question.service.AnswerService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/answers")
class AnswerController(private val answerService: AnswerService) {
    
    @PostMapping("/question/{questionId}")
    fun createAnswer(
        @PathVariable questionId: String,
        @RequestBody answer: Answer,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Answer> {
        return ResponseEntity.ok(answerService.createAnswer(questionId, answer))
    }
    
    @GetMapping("/question/{questionId}")
    fun getAnswersForQuestion(
        @PathVariable questionId: String,
        pageable: Pageable
    ): ResponseEntity<Page<Answer>> {
        return ResponseEntity.ok(answerService.getAnswersForQuestion(questionId, pageable))
    }
    
    @PutMapping("/{id}")
    fun updateAnswer(
        @PathVariable id: String,
        @RequestBody answer: Answer,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Answer> {
        return ResponseEntity.ok(answerService.updateAnswer(id, answer))
    }
    
    @DeleteMapping("/{id}")
    fun deleteAnswer(
        @PathVariable id: String,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Unit> {
        answerService.deleteAnswer(id)
        return ResponseEntity.ok().build()
    }
} 