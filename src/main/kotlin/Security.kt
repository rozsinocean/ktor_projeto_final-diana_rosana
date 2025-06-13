package com.rosana_diana

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.http.auth.*
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
    val jwtSecret = jwtConfig.property("secret").getString()
    val jwtAudience = jwtConfig.property("audience").getString()
    val jwtDomain = jwtConfig.property("domain").getString()
    val jwtRealm = jwtConfig.property("realm").getString()

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            authSchemes("Bearer")
            authHeader { call ->
                val header = call.request.parseAuthorizationHeader()
                if (header is HttpAuthHeader.Single && header.authScheme == "Bearer") {
                    return@authHeader HttpAuthHeader.Single("Bearer", header.blob)
                }

                val tokenFromCookie = call.request.cookies["jwt_token"]
                if (tokenFromCookie != null) {
                    return@authHeader HttpAuthHeader.Single("Bearer", tokenFromCookie)
                }
                null
            }

            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .acceptLeeway(5)
                    .build()
            )

            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                val expiration = credential.expiresAt?.after(Date()) ?: false

                if (email != null && expiration) {
                    application.log.info("Token validated successfully for email: $email")
                    JWTPrincipal(credential.payload)
                } else {
                    application.log.warn("JWT validation failed: email=$email, expiration=$expiration")
                    null
                }
            }
            challenge { _, _ ->
                call.respondRedirect("/login")
            }
        }
    }
}