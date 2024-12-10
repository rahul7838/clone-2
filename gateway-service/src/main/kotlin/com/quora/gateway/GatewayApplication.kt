package com.quora.gateway

import com.quora.gateway.filter.JwtAuthenticationFilter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GatewayApplication(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    @Bean
    fun routeLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("auth-service") { r ->
                r.path("/api/v1/auth/**")
                    .filters { f -> f.filter(jwtAuthenticationFilter.apply(JwtAuthenticationFilter.Config())) }
                    .uri("lb://auth-service")
            }
            .route("question-service") { r ->
                r.path("/api/v1/questions/**")
                    .filters { f -> f.filter(jwtAuthenticationFilter.apply(JwtAuthenticationFilter.Config())) }
                    .uri("lb://question-service")
            }
            .route("answer-service") { r ->
                r.path("/api/v1/answers/**")
                    .filters { f -> f.filter(jwtAuthenticationFilter.apply(JwtAuthenticationFilter.Config())) }
                    .uri("lb://answer-service")
            }
            .route("search-service") { r ->
                r.path("/api/v1/search/**")
                    .filters { f -> f.filter(jwtAuthenticationFilter.apply(JwtAuthenticationFilter.Config())) }
                    .uri("lb://search-service")
            }
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
} 