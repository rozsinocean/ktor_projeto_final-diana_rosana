package com.rosana_diana

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.rosana_diana.person.Person
import com.rosana_diana.person.PersonRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.mindrot.jbcrypt.BCrypt

fun Application.configureRouting() {
    val personRepository = PersonRepository()

    routing {
        // Form Submission: Handle registration
        post("/registo_cliente") {
            val params = call.receiveParameters()

            // Retrieve and validate required fields
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
                // Hash password and store the person
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

                // Show success message
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

        // Page: Login Form
        get("/login") {
            call.respond(ThymeleafContent("login", mapOf()))
        }

        // Form Submission: Handle login
        post("/login") {
            val params = call.receiveParameters()

            // Retrieve fields
            val email = params["email"] ?: ""
            val password = params["password"] ?: ""
            val nif = params["nif"]?.toIntOrNull()

            // Validate required fields
            if (email.isBlank() || password.isBlank() || nif == null) {
                call.respond(
                    ThymeleafContent(
                        "login", mapOf("error" to "Todos os campos obrigatórios devem ser preenchidos.")
                    )
                )
                return@post
            }

            try {
                val person = personRepository.allPerson()
                    .find { it.email == email && it.nif == nif }

                if (person != null && BCrypt.checkpw(password, person.password)) {
                    // Generate JWT token
                    val jwtConfig = environment.config.config("jwt")
                    val jwtSecret = jwtConfig.property("secret").getString()
                    val jwtAudience = jwtConfig.property("audience").getString()
                    val jwtDomain = jwtConfig.property("domain").getString()

                    val token = JWT.create()
                        .withAudience(jwtAudience)
                        .withIssuer(jwtDomain)
                        .withClaim("email", person.email)
                        .sign(Algorithm.HMAC256(jwtSecret))

                    // Successful login, redirect or render success page
                    call.respond(
                        ThymeleafContent(
                            "login_success", mapOf(
                                "message" to "Login bem-sucedido para ${person.name}.",
                                "token" to token
                            )
                        )
                    )
                } else {
                    call.respond(
                        ThymeleafContent(
                            "login", mapOf("error" to "Email, NIF ou senha inválidos.")
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    ThymeleafContent(
                        "login", mapOf("error" to "Erro inesperado: ${e.localizedMessage}")
                    )
                )
            }
        }

        // Protected Route
        authenticate("auth-jwt") {
            get("/protected") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)

                if (email != null) {
                    call.respondText("Bem-vindo, $email! Esta é uma rota protegida.")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Não foi possível recuperar informações do usuário.")
                }
            }
        }
    }
}

// Data class for login credentials
data class UserCredentials(
    val email: String,
    val password: String
)