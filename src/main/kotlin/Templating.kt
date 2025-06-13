package com.rosana_diana

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.request.receiveParameters
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Application.configureTemplating() {
    val logger = LoggerFactory.getLogger("ThymeleafConfig")

    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
            checkExistence = true // Isso vai ajudar a debugar
        })
    }

    routing {
        static("/static") {
            resources("static")
        }

        get("/login") {
            logger.info("Tentar mostrar página de login")
            try {
                val resource = this::class.java.classLoader.getResource("templates/thymeleaf/login.html")
                if (resource == null) {
                    logger.error("Arquivo login.html não encontrado!")
                    call.respondText("Template não encontrado", status = HttpStatusCode.NotFound)
                    return@get
                }

                logger.info("Template login.html encontrado, tentando renderizar")
                call.respond(ThymeleafContent("login", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar login: ${e.message}", e)
                call.respondText("Erro: ${e.message}")
            }
        }

        get("/registo_cliente") {
            try {
                logger.info("Tentar renderizar o template Registo de Cliente Novo")
                call.respond(ThymeleafContent("registo_cliente", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar template: ${e.message}", e)
                call.respondText("Erro ao carregar o template: ${e.message}")
            }
        }

        authenticate("auth-jwt") {
            get("/") {
                logger.info("DEBUG: Entrou na rota / protegida.")
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                logger.info("DEBUG: Principal email: $email")

                if (email == null) {
                    logger.warn("DEBUG: Email nulo na rota / mesmo após autenticação. Redirecionando para login.")
                    call.respondRedirect("/login") // Garante que redireciona se o email for nulo aqui
                    return@get
                }

                try {
                    logger.info("DEBUG: Preparando para renderizar o template index.")
                    call.respond(ThymeleafContent("index", mapOf("userEmail" to email)))
                    logger.info("DEBUG: Chamada a respond(ThymeleafContent) executada.")
                } catch (e: Exception) {
                    logger.error("DEBUG: Erro ao renderizar template index: ${e.message}", e)
                    call.respond(HttpStatusCode.InternalServerError, "Erro ao carregar a página principal: ${e.message}")
                }
            }

            get("/suporte") {
                try {
                    logger.info("Tentar renderizar o template suporte")
                    call.respond(ThymeleafContent("suporte", mapOf()))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/transferencias") {
                try {
                    logger.info("Tentar renderizar o template transferecias")
                    call.respond(ThymeleafContent("transferencias", mapOf()))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/levantamentos") {
                try {
                    logger.info("Tentar renderizar o template levantamentos")
                    call.respond(ThymeleafContent("levantamentos", mapOf()))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/pin_multibanco") {
                try {
                    logger.info("Tentar renderizar o template pin_multibanco")
                    call.respond(ThymeleafContent("pin_multibanco", mapOf()))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }
        }
    }
}



