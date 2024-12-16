package com.quora.search.controller

import com.quora.search.service.SearchService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/search")
class SearchController(private val searchService: SearchService) {
    
    @GetMapping("/questions")
    fun searchQuestions(
        @RequestParam query: String,
        pageable: Pageable
    ): ResponseEntity<Page<SearchResult>> {
        return ResponseEntity.ok(searchService.searchQuestions(query, pageable))
    }
    
    @GetMapping("/questions/tag/{tag}")
    fun searchQuestionsByTag(
        @PathVariable tag: String,
        pageable: Pageable
    ): ResponseEntity<Page<SearchResult>> {
        return ResponseEntity.ok(searchService.searchQuestionsByTag(tag, pageable))
    }
    
    @GetMapping("/answers")
    fun searchAnswers(
        @RequestParam query: String,
        pageable: Pageable
    ): ResponseEntity<Page<SearchResult>> {
        return ResponseEntity.ok(searchService.searchAnswers(query, pageable))
    }
}