package com.rosana_diana

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.Date


fun Application.configureSecurity() {
    val jwtConfig = environment.config.config("jwt")
    val jwtSecret = jwtConfig.property("secret").getString() // Securely read from config
    val jwtAudience = jwtConfig.property("audience").getString()
    val jwtDomain = jwtConfig.property("domain").getString()
    val jwtRealm = jwtConfig.property("realm").getString()

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .acceptLeeway(5) // Allow 5 seconds of clock skew
                    .build()
            )

            validate { credential ->
                // Fine-grained validation
                val username = credential.payload.getClaim("username").asString()
                val expiration = credential.expiresAt?.after(Date()) ?: false

                if (username != null && expiration) {
                    JWTPrincipal(credential.payload)
                } else {
                    // Optional logging for debugging reasons (remove in production)
                    application.log.warn("JWT validation failed: Missing username or expired token.")
                    null
                }
            }
        }
    }
}