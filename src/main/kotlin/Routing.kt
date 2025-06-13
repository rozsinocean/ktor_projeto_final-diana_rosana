package com.rosana_diana

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.rosana_diana.person.Person
import com.rosana_diana.person.PersonRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.mindrot.jbcrypt.BCrypt
import java.util.Date

fun Application.configureRouting() {
    val personRepository = PersonRepository()

    routing {
        authenticate("auth-jwt") {
            post("/registo_cliente") {
                val params = call.receiveParameters()

                val name = params["name"] ?: ""
                val email = params["email"] ?: ""
                val password = params["password"] ?: ""
                val nif = params["nif"]?.toIntOrNull()
                val gender = params["gender"] ?: ""
                val phone = params["phone"]
                val address = params["address"]
                val birthdate = params["birthdate"]?.let { java.time.LocalDate.parse(it) }

                if (name.isBlank() || email.isBlank() || password.isBlank() || nif == null || gender.isBlank() || birthdate == null) {
                    call.respond(
                        ThymeleafContent(
                            "registo_cliente", mapOf("error" to "Todos os campos obrigatórios devem ser preenchidos.")
                        )
                    )
                    return@post
                }

                try {
                    val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
                    val newPerson = Person(
                        name = name,
                        email = email,
                        password = hashedPassword,
                        nif = nif,
                        gender = gender,
                        phone = phone,
                        address = address,
                        birthdate = birthdate.toString()
                    )
                    personRepository.createPerson(newPerson)

                    call.respond(
                        ThymeleafContent(
                            "registo_cliente", mapOf(
                                "message" to "Registro efetuado com sucesso para $name (${email})."
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e.message?.contains("unique constraint") == true) {
                        call.respond(
                            ThymeleafContent(
                                "registo_cliente", mapOf("error" to "O Email ou NIF fornecido já está registrado.")
                            )
                        )
                    } else {
                        call.respond(
                            ThymeleafContent(
                                "registo_cliente", mapOf("error" to "Erro inesperado: ${e.localizedMessage}")
                            )
                        )
                    }
                }
            }
        }

        post("/login") {
            val params = call.receiveParameters()
            val email = params["email"] ?: ""
            val nif = params["nif"] ?: ""
            val password = params["password"] ?: ""

            try {
                val person = personRepository.findByEmail(email)

                if (person == null || person.nif.toString() != nif || !BCrypt.checkpw(password, person.password)) {
                    application.log.warn("Senha inválida para o usuário: $email com NIF: $nif")
                    call.respond(HttpStatusCode.Unauthorized, "Email, senha ou NIF inválidos.")
                    return@post
                }

                application.log.info("Login bem-sucedido para o usuário: $email com NIF: $nif")

                val token = JWT.create()
                    .withAudience(environment.config.property("jwt.audience").getString())
                    .withIssuer(environment.config.property("jwt.domain").getString())
                    .withClaim("email", person.email)
                    .withClaim("nif", person.nif)
                    .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000)) // Token expira em 1 hora
                    .sign(Algorithm.HMAC256(environment.config.property("jwt.secret").getString()))

                call.response.cookies.append(
                    Cookie(
                        name = "jwt_token",
                        value = token,
                        httpOnly = true,
                        secure = false,
                        path = "/",
                        maxAge = 60 * 60
                    )
                )

                call.respondRedirect("http://127.0.0.1:8080/", permanent = false)

            } catch (e: Exception) {
                application.log.error("Erro durante o processo de login:", e)
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado durante o login.")
            }
        }


        post("/logout") {
            call.response.cookies.append(
                Cookie(
                    name = "jwt_token",
                    value = "",
                    maxAge = 0
                )
            )
            call.respondRedirect("/login")
        }
    }
}