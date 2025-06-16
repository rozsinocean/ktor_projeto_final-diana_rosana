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
            checkExistence = true
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
            try {
                logger.info("Template login.html encontrado, tentando renderizar")
                call.respond(ThymeleafContent("login", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar login: ${e.message}", e)
                call.respondText("Erro: ${e.message}")
            }
        }

        get("/login_funcionario") {
            try {
                logger.info("Template login.html encontrado, tentando renderizar")
                call.respond(ThymeleafContent("login_funcionario", mapOf()))
            } catch (e: Exception) {
                logger.error("Erro ao renderizar login: ${e.message}", e)
                call.respondText("Erro: ${e.message}")
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

        authenticate("auth-jwt") {
            get("/") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                val userType = principal?.getClaim("type", String::class)

                if (email == null || userType == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    val person = personRepository.findByEmail(email)

                    if (person == null) {
                        call.respondRedirect("/login")
                        return@get
                    }

                    if (userType == "client") {
                        val client = clientRepository.findByPersonId(person.id!!)

                        val accounts = client?.id?.let { clientId ->
                            accountRepository.getAccountsByPrimaryHolder(clientId)
                        } ?: emptyList()

                        val userTransactions = mutableSetOf<Transaction>()
                        val userAccountIds = accounts.map { it.id_account }

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
                    } else if (userType == "employee") {
                        val numberOfAccounts = accountRepository.allAccounts().size
                        val numberOfClients = clientRepository.allClients().size

                        val data = mutableMapOf<String, Any>(
                            "person" to person,
                            "numberOfAccounts" to numberOfAccounts,
                            "numberOfClients" to numberOfClients,
                        )
                        call.respond(ThymeleafContent("dashboard_funcionario", data.toMap()))
                    }

                } catch (e: Exception) {
                    application.log.error("Erro ao carregar a página principal:", e)
                    call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ao carregar seus dados: ${e.message}")
                }
            }

            get("/transferencias") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                val type = principal?.getClaim("type", String::class)

                if (email == null || type == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    if (type == "client") {
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
                    } else {
                        call.respond(ThymeleafContent("transferencias_funcionario", mapOf()))
                    }
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/levantamentos") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                val type = principal?.getClaim("type", String::class)

                if (email == null || type == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    if (type == "client") {
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
                    } else {
                        call.respond(ThymeleafContent("levantamentos_funcionario", mapOf()))
                    }
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/depositos") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                val type = principal?.getClaim("type", String::class)

                if (email == null || type == null) {
                    call.respondRedirect("/login")
                    return@get
                }

                try {
                    if (type == "client") {
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
                    } else {
                        call.respond(ThymeleafContent("depositos_funcionario", mapOf()))
                    }
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            //client specific
            get("/omeuperfil") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                val type = principal?.getClaim("type", String::class)

                if (email == null || type != "client") {
                    call.respondRedirect("/")
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

                    val allAccountTypes = accountTypeRepository.allAccountTypes()
                    val accountTypesMap = allAccountTypes.associate { it.id to it.name }

                    val data = mutableMapOf(
                        "person" to person,
                        "accounts" to accounts,
                        "accountTypesMap" to accountTypesMap
                    )

                    call.respond(ThymeleafContent("omeuperfil", data.toMap()))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            // employee specific
            get("/registo_cliente") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                val type = principal?.getClaim("type", String::class)

                if (email == null || type != "employee") {
                    call.respondRedirect("/")
                    return@get
                }

                try {
                    logger.info("Tentar renderizar o template Registo de Cliente Novo")
                    call.respond(ThymeleafContent("registo_cliente", mapOf()))
                } catch (e: Exception) {
                    logger.error("Erro ao renderizar template: ${e.message}", e)
                    call.respondText("Erro ao carregar o template: ${e.message}")
                }
            }

            get("/pesquisar_Cliente") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                val type = principal?.getClaim("type", String::class)

                if (email == null || type != "employee") {
                    call.respondRedirect("/")
                    return@get
                }

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