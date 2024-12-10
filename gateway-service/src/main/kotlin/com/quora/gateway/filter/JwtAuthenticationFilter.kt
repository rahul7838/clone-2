package com.quora.gateway.filter

import com.quora.gateway.config.JwtConfig
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(private val jwtConfig: JwtConfig) : 
    AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config>(Config::class.java) {

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request

            if (isAuthRequired(request.path.value())) {
                val authHeader = request.headers["Authorization"]?.firstOrNull()
                
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return@GatewayFilter onError(exchange, "No JWT token found", HttpStatus.UNAUTHORIZED)
                }

                val token = authHeader.substring(7)
                if (!jwtConfig.validateToken(token)) {
                    return@GatewayFilter onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED)
                }

                // Extract user information and add to headers
                val claims = jwtConfig.getClaimsFromToken(token)
                val modifiedExchange = exchange.mutate()
                    .request(request.mutate()
                        .header("X-User-Id", claims.subject)
                        .header("X-User-Email", claims["email"] as String?)
                        .header("X-User-Name", claims["name"] as String?)
                        .build())
                    .build()

                return@GatewayFilter chain.filter(modifiedExchange)
            }

            chain.filter(exchange)
        }
    }

    private fun isAuthRequired(path: String): Boolean {
        val openPaths = listOf("/api/v1/auth/register", "/api/v1/auth/login")
        return !openPaths.any { path.startsWith(it) }
    }

    private fun onError(exchange: ServerWebExchange, message: String, status: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = status
        return response.setComplete()
    }

    class Config
} 