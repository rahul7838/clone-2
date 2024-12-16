package com.quora.sync.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sync/health")
class HealthController {
    
    @GetMapping
    fun healthCheck() = mapOf(
        "status" to "UP",
        "timestamp" to System.currentTimeMillis()
    )
} 