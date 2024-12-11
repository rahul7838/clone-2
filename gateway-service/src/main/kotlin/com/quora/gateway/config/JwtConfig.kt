package com.quora.gateway.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.http.HttpHeaders

@Component
class JwtConfig {
    
    @Value("\${jwt.secret}")
    private lateinit var secret: String
/*
    This function validates the JWT token
    It attempts to parse and verify the token using the secret key
    Returns:
    true if the token is valid (can be parsed and signature is correct)
    false if any exception occurs (expired token, invalid signature, malformed token, etc.)
    It's typically used for authentication to verify if an incoming request has a valid JWT
*/
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.toByteArray()))
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

/*
    Claims typically contain user information like user ID, roles, or other custom data
    This function extracts and returns the claims from the JWT token
    It uses the secret key to parse the token and return the claims
*/
    fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
    }
} 

/*
For Production:
You should:
Generate a strong secret key (at least 256 bits / 32 characters long)
Set it as an environment variable named JWT_SECRET
Never commit the actual secret to version control
Here's how to generate a secure JWT secret:
Option 1: Using OpenSSL (in terminal):

openssl rand -base64 32

Option 2: Using Java to generate a secure key:

import java.security.SecureRandom;
import java.util.Base64;

SecureRandom random = new SecureRandom();
byte[] bytes = new byte[32];
random.nextBytes(bytes);
String key = Base64.getEncoder().encodeToString(bytes);
System.out.println(key);

Setting the environment variable:

On Linux/Mac:
export JWT_SECRET=your_generated_key


For production deployment, you should:
1. Set this as an environment variable in your deployment platform (Docker, Kubernetes, etc.)
2. Use a secure secrets management service like HashiCorp Vault or AWS Secrets Manager
3. Never share or expose this key
4. Rotate the key periodically following security best practices
5. Remember to update the environment variable in your deployment platform
*/