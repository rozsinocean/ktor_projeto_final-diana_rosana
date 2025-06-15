package com.rosana_diana

import com.rosana_diana.account.AccountRepository
import com.rosana_diana.accounttype.AccountTypeRepository
import com.rosana_diana.client.ClientRepository
import com.rosana_diana.person.PersonRepository
import com.rosana_diana.transaction.Transaction
import com.rosana_diana.transaction.TransactionRepository
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

    val personRepository = PersonRepository()
    val clientRepository = ClientRepository()
    val accountRepository = AccountRepository()
    val transactionRepository = TransactionRepository()
    val accountTypeRepository = AccountTypeRepository()

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
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)

                if (email == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    val person = personRepository.findByEmail(email)

                    if (person == null) {
                        call.respondRedirect("/login")
                        return@get
                    }

                    val client = clientRepository.findByPersonId(person.id!!)

                    val accounts = client?.id?.let { clientId ->
                        accountRepository.getAccountsByPrimaryHolder(clientId)
                    } ?: emptyList()

                    val userTransactions = mutableSetOf<Transaction>()
                    val userAccountIds = accounts.mapNotNull { it.id_account }

                    for (accountId in userAccountIds) {
                        userTransactions.addAll(transactionRepository.getTransactionsBySourceAccount(accountId))
                        userTransactions.addAll(transactionRepository.getTransactionsByDestinationAccount(accountId))
                    }

                    val allUserTransactions = userTransactions.toList()

                    val allAccountTypes = accountTypeRepository.allAccountTypes()
                    val accountTypeNamesById = allAccountTypes.associateBy { it.id }.mapValues { it.value.name }


                    val data = mutableMapOf<String, Any>(
                        "userEmail" to email,
                        "person" to person,
                        "accounts" to accounts,
                        "transactions" to allUserTransactions,
                        "accountTypes" to accountTypeNamesById,
                    )

                    if (client != null) {
                        data["client"] = client
                    }

                    call.respond(ThymeleafContent("index", data.toMap()))

                } catch (e: Exception) {
                    application.log.error("Erro ao carregar a página principal:", e)
                    call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ao carregar seus dados: ${e.message}")
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
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)

                if (email == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    val person = personRepository.findByEmail(email)

                    if (person == null) {
                        call.respondRedirect("/login")
                        return@get
                    }

                    val client = clientRepository.findByPersonId(person.id!!)

                    val accounts = client?.id?.let { clientId ->
                        accountRepository.getAccountsByPrimaryHolder(clientId)
                    } ?: emptyList()

                    call.respond(ThymeleafContent("transferencias", mapOf("accounts" to accounts)))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/levantamentos") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)

                if (email == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    val person = personRepository.findByEmail(email)

                    if (person == null) {
                        call.respondRedirect("/login")
                        return@get
                    }

                    val client = clientRepository.findByPersonId(person.id!!)

                    val accounts = client?.id?.let { clientId ->
                        accountRepository.getAccountsByPrimaryHolder(clientId)
                    } ?: emptyList()

                    call.respond(ThymeleafContent("levantamentos", mapOf("accounts" to accounts)))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/depositos") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)

                if (email == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    val person = personRepository.findByEmail(email)

                    if (person == null) {
                        call.respondRedirect("/login")
                        return@get
                    }

                    val client = clientRepository.findByPersonId(person.id!!)

                    val accounts = client?.id?.let { clientId ->
                        accountRepository.getAccountsByPrimaryHolder(clientId)
                    } ?: emptyList()

                    call.respond(ThymeleafContent("depositos", mapOf("accounts" to accounts)))
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

            get("/pesquisar_Cliente") {
                try {
                    logger.info("Tentar renderizar o template pesquisar cliente")
                    call.respond(ThymeleafContent("pesquisar_CLiente", mapOf()))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }
        }
    }
}