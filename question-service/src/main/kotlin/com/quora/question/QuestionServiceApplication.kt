package com.quora.question

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
class QuestionServiceApplication

fun main(args: Array<String>) {
    runApplication<QuestionServiceApplication>(*args)
} 