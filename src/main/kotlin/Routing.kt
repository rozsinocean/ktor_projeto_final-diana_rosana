package com.rosana_diana

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.rosana_diana.account.Account
import com.rosana_diana.account.AccountRepository
import com.rosana_diana.accounttype.AccountTypeRepository
import com.rosana_diana.client.Client
import com.rosana_diana.client.ClientRepository
import com.rosana_diana.employee.Employee
import com.rosana_diana.employee.EmployeeRepository
import com.rosana_diana.person.Person
import com.rosana_diana.person.PersonRepository
import com.rosana_diana.person.PersonTable.email
import com.rosana_diana.support.Support
import com.rosana_diana.support.SupportRepository
import com.rosana_diana.transaction.TransactionRepository
import com.rosana_diana.transaction.TransactionService
import com.rosana_diana.transactiontype.TransactionTypeRepository
import com.rosana_diana.utilitys.IbanGenerator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate
import java.util.Date

 fun Application.configureRouting() {
    val personRepository = PersonRepository()
    val clientRepository = ClientRepository()
    val employeeRepository = EmployeeRepository()
    val accountRepository = AccountRepository()
    val transactionRepository = TransactionRepository()
    val supportRepository = SupportRepository()
    val transactionTypeRepository = TransactionTypeRepository()
    val transactionService = TransactionService(accountRepository, transactionRepository, transactionTypeRepository) // <--- ATUALIZAR CONSTRUTOR

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
                val birthdate = params["birthdate"]?.let { LocalDate.parse(it) }

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

                    val createdPerson = personRepository.createPerson(newPerson)

                    if (createdPerson.id != null) {
                        val newClient = Client(id = 0, personId = createdPerson.id)
                        val createdClient = clientRepository.createClient(newClient)

                        val newAccount = Account(
                            iban = IbanGenerator.generateIban(),
                            balance = 0.0,
                            id_primaryholder = createdClient.id,
                            id_secondaryholder = null,
                            id_accountType = 1,
                            id_account = 0,
                            opening_date = LocalDate.now().toString()
                        )

                        accountRepository.createAccount(newAccount)
                    }

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

            post("/registo_funcionario") {
                val params = call.receiveParameters()

                val name = params["name"] ?: ""
                val email = params["email"] ?: ""
                val password = params["password"] ?: ""
                val nif = params["nif"]?.toIntOrNull()
                val gender = params["gender"] ?: ""
                val phone = params["phone"]
                val address = params["address"]
                val birthdate = params["birthdate"]?.let { LocalDate.parse(it) }

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

                    val createdPerson = personRepository.createPerson(newPerson)

                    val admissionDate = LocalDate.now()
                    if (createdPerson.id != null) {
                        val newEmployee = Employee(
                            id = 0, personId = createdPerson.id,
                            admissionDate = admissionDate.toString(),
                            salary = 0.0,
                            idPosition = "1"
                        )

                        employeeRepository.createEmployee(newEmployee)
                    }

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

            post("/depositos") {
                val params = call.receiveParameters()
                val accountIban = params["iban"]
                val amount = params["amount"]?.toDoubleOrNull()

                if (accountIban.isNullOrBlank() || amount == null) {
                    call.respond(HttpStatusCode.BadRequest, "Dados de depósito inválidos. Forneça accountIban e amount.")
                    return@post
                }

                try {
                    transactionService.makeDeposit(accountIban, amount)

                    call.respond(
                        ThymeleafContent(
                            "depositos", mapOf(
                                "message" to "Deposito efetuado com sucesso para para o iban (${accountIban})."
                            )
                        )
                    )
                } catch (e: TransactionService.TransactionServiceException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Erro no depósito.")
                } catch (e: Exception) {
                    application.log.error("Erro inesperado ao realizar depósito:", e)
                    call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ao processar o depósito.")
                }
            }

            post("/levantamentos") {
                val params = call.receiveParameters()
                val accountIban = params["iban"]
                val amount = params["amount"]?.toDoubleOrNull()

                if (accountIban.isNullOrBlank() || amount == null) {
                    call.respond(HttpStatusCode.BadRequest, "Dados de levantamento inválidos. Forneça accountIban e amount.")
                    return@post
                }

                try {
                    transactionService.makeWithdrawal(accountIban, amount)

                    call.respond(
                        ThymeleafContent(
                            "levantamentos", mapOf(
                                "message" to "Levantamento efetuado com sucesso do iban (${accountIban})."
                            )
                        )
                    )
                } catch (e: TransactionService.TransactionServiceException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Erro no levantamento.")
                } catch (e: Exception) {
                    application.log.error("Erro inesperado ao realizar levantamento:", e)
                    call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ao processar o levantamento.")
                }
            }

            post("/transferencias") {
                val params = call.receiveParameters()
                val sourceIban = params["sourceIban"]
                val destinationIban = params["destinationIban"]
                val amount = params["amount"]?.toDoubleOrNull()

                if (sourceIban.isNullOrBlank() || destinationIban.isNullOrBlank() || amount == null) {
                    call.respond(HttpStatusCode.BadRequest, "Dados de transferência inválidos. Forneça sourceIban, destinationIban e amount.")
                    return@post
                }

                try {
                    transactionService.makeTransfer(sourceIban, destinationIban, amount)

                    call.respond(
                        ThymeleafContent(
                            "transferencias", mapOf(
                                "message" to "Transferencia efetuado com sucesso do iban de origem (${sourceIban}) para iban (${destinationIban})."
                            )
                        )
                    )
                } catch (e: TransactionService.TransactionServiceException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Erro na transferência.")
                } catch (e: Exception) {
                    application.log.error("Erro inesperado ao realizar transferência:", e)
                    call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ao processar a transferência.")
                }
            }

            post("/pesquisar_cliente") {
                val params = call.receiveParameters()
                val email = params["email"]
                val nif = params["nif"]

                if (email == null || nif == null) {
                    call.respond(HttpStatusCode.BadRequest, "Dados inválidos.")
                    return@post
                }

                try {
                    val person = personRepository.findByEmail(email)
                    if (person == null || person.nif.toString() != nif) {
                        call.respond(HttpStatusCode.BadRequest, "Dados inválidos.")
                        return@post
                    }

                    val client = clientRepository.findByPersonId(person.id!!)
                    if (client == null) {
                        call.respond(HttpStatusCode.BadRequest, "Pessoa,não é um cliente.")
                        return@post
                    }

                    val data = mutableMapOf<String, Any>("person" to person)

                    call.respond(
                        ThymeleafContent(
                            "pesquisar_cliente", data
                        )
                    )
                } catch (e: TransactionService.TransactionServiceException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Erro na transferência.")
                } catch (e: Exception) {
                    application.log.error("Erro inesperado ao realizar transferência:", e)
                    call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ao processar a transferência.")
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
                    .withClaim("type", "client")
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

                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                application.log.error("Erro durante o processo de login:", e)
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado durante o login.")
            }
        }

        post("/login_funcionario") {
            val params = call.receiveParameters()
            val id = params["id"]?.toInt()
            val password = params["password"] ?: ""

            try {
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Dados nulos!")
                    return@post
                }

                val employee = employeeRepository.getEmployeeById(id)
                val person = employee?.let { personRepository.findById(it.personId) }

                if (employee == null || employee.id != id || !BCrypt.checkpw(password, person?.password)) {
                    application.log.warn("Senha inválida para o funcionário: $id")
                    call.respond(HttpStatusCode.Unauthorized, "Email, senha ou NIF inválidos.")
                    return@post
                }

                application.log.info("Login bem-sucedido para o funcionário: $id")

                val token = JWT.create()
                    .withAudience(environment.config.property("jwt.audience").getString())
                    .withIssuer(environment.config.property("jwt.domain").getString())
                    .withClaim("email", person?.email)
                    .withClaim("nif", person?.nif)
                    .withClaim("type", "employee")
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

                call.respond(HttpStatusCode.OK)
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
                    maxAge = 0,
                    path = "/",
                    httpOnly = true,
                    secure = true
                )
            )

            call.respondRedirect("/login")
        }

        post("/suporte") {
            val params = call.receiveParameters()
            val sourceEmail = params["email"]
            val sourceDescription = params["description"]

            if (sourceEmail.isNullOrBlank() || sourceDescription.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Dados em branco ou inválidos.")
                return@post
            }

            try {
                val supportData = Support(
                    id = 0,
                    email = sourceEmail,
                    description = sourceDescription
                )

                supportRepository.createSupport(supportData)

                call.respond(
                    ThymeleafContent(
                        "suporte", mapOf(
                            "message" to "Mensagem feito com sucesso."
                        )
                    )
                )

                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                application.log.error("Erro inesperado:", e)
                call.respond(HttpStatusCode.InternalServerError, "Ocorreu um erro inesperado ao fazer o envio para o suporte.")
            }
        }
    }
}