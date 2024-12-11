package com.quora.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message), HttpStatus.NOT_FOUND)
    }
    
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message), HttpStatus.UNAUTHORIZED)
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("An unexpected error occurred"),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}

data class ErrorResponse(
    val message: String?,
    val timestamp: Long = System.currentTimeMillis()
)

class ResourceNotFoundException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message) 