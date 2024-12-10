package com.quora.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GatewayApplication {
    @Bean
    fun routeLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("auth-service") { r ->
                r.path("/api/v1/auth/**")
                    .uri("lb://auth-service")
            }
            .route("question-service") { r ->
                r.path("/api/v1/questions/**")
                    .uri("lb://question-service")
            }
            .route("answer-service") { r ->
                r.path("/api/v1/answers/**")
                    .uri("lb://answer-service")
            }
            .route("search-service") { r ->
                r.path("/api/v1/search/**")
                    .uri("lb://search-service")
            }
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
} 