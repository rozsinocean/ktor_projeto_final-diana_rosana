package com.rosana_diana

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
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
        // Index
        get("/") {
            try {
                logger.info("Tentar renderizar o template index")
                call.respond(ThymeleafContent("index", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar template: ${e.message}", e)
                call.respondText("Erro ao carregar o template: ${e.message}")
            }
        }

        // Login
        get("/") {
            logger.info("Acessando rota raiz (/)")
            call.respondRedirect("/login")
        }

        get("/login") {
            logger.info("Tentar mostrar página de login")
            try {
                // Verificar se o template existe
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

        // Suporte
        get("/suporte") {
            try {
                logger.info("Tentar renderizar o template suporte")
                call.respond(ThymeleafContent("suporte", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar template: ${e.message}", e)
                call.respondText("Erro ao carregar o template: ${e.message}")
            }
        }

        // Transferências
        get("/transferencias") {
            try {
                logger.info("Tentar renderizar o template transferecias")
                call.respond(ThymeleafContent("transferencias", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar template: ${e.message}", e)
                call.respondText("Erro ao carregar o template: ${e.message}")
            }
        }

        // Fazer Levantamentos
        get("/levantamentos") {
            try {
                logger.info("Tentar renderizar o template levantamentos")
                call.respond(ThymeleafContent("levantamentos", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar template: ${e.message}", e)
                call.respondText("Erro ao carregar o template: ${e.message}")
            }
        }

        // Pin de multibanco
        get("/pin_multibanco") {
            try {
                logger.info("Tentar renderizar o template pin_multibanco")
                call.respond(ThymeleafContent("pin_multibanco", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar template: ${e.message}", e)
                call.respondText("Erro ao carregar o template: ${e.message}")
            }
        }

        // Registo de cliente
        get("/registo_cliente") {
            try {
                logger.info("Tentar renderizar o template Registo de Cliente Novo")
                call.respond(ThymeleafContent("registo_cliente", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar template: ${e.message}", e)
                call.respondText("Erro ao carregar o template: ${e.message}")
            }
        }



    }

}



