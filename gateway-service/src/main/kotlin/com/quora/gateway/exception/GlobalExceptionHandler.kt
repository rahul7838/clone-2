package com.quora.gateway.exception

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import com.fasterxml.jackson.databind.ObjectMapper

@Component
@Order(-2)
class GlobalExceptionHandler(private val objectMapper: ObjectMapper) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val response = exchange.response
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = ErrorResponse(
            message = ex.message ?: "An unexpected error occurred",
            timestamp = System.currentTimeMillis()
        )

        response.statusCode = when (ex) {
            is UnauthorizedException -> HttpStatus.UNAUTHORIZED
            is ResourceNotFoundException -> HttpStatus.NOT_FOUND
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        val buffer: DataBuffer = response.bufferFactory().wrap(
            objectMapper.writeValueAsBytes(errorResponse)
        )

        return response.writeWith(Mono.just(buffer))
    }
}

data class ErrorResponse(
    val message: String?,
    val timestamp: Long
)

class ResourceNotFoundException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message) 