package com.rosana_diana

import com.rosana_diana.account.AccountTable
import com.rosana_diana.client.ClientTable
import com.rosana_diana.person.PersonTable
import com.rosana_diana.accounttype.AccountTypeTable
import com.rosana_diana.employee.EmployeeTable
import com.rosana_diana.transaction.TransactionTable
import com.rosana_diana.transactiontype.TransactionTypeTable
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate

object DatabaseFactory {
    fun init(config: ApplicationConfig, embedded: Boolean) {
        val database: Database = if (embedded) {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver", user = "root", password = "")
        } else {
            val url = config.property("postgres.url").getString()
            val user = config.property("postgres.user").getString()
            val password = config.property("postgres.password").getString()

            Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)
        }

        println("Connected to database: ${database.url}")

        transaction {
            SchemaUtils.create(
                PersonTable,
                EmployeeTable,
                ClientTable,
                AccountTypeTable,
                AccountTable,
                TransactionTypeTable,
                TransactionTable,
            )

            if (TransactionTypeTable.selectAll().count() == 0L) {
                println("Inserindo tipos de transação padrão...")
                TransactionTypeTable.insert {
                    it[name] = "Depósito"
                }
                TransactionTypeTable.insert {
                    it[name] = "Levantamento"
                }
                TransactionTypeTable.insert {
                    it[name] = "Transferência"
                }
                TransactionTypeTable.insert {
                    it[name] = "Pagamento"
                }
            }

            if (AccountTypeTable.selectAll().count() == 0L) {
                println("Inserindo tipos de conta padrão...")
                AccountTypeTable.insert {
                    it[name] = "Conta Corrente"
                }
                AccountTypeTable.insert {
                    it[name] = "Poupança"
                }
                AccountTypeTable.insert {
                    it[name] = "Crédito"
                }
            }

            if (PersonTable.selectAll().count() == 0L) {
                val hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt())
                val adminPersonId = PersonTable.insert {
                    it[name] = "Admin User"
                    it[birthdate] = LocalDate.of(1990, 1, 1)
                    it[gender] = "Não Especificado"
                    it[email] = "admin@bankapp.com"
                    it[phone] = "912345676"
                    it[address] = "Rua do Administrador, 10, Cidade Admin"
                    it[password] = hashedPassword
                    it[nif] = 999888777
                } get PersonTable.id

                ClientTable.insert {
                    it[personId] = adminPersonId
                }

                EmployeeTable.insert {
                    it[personId] = adminPersonId
                    it[admissionDate] = LocalDate.now()
                    it[salary] = 5000.00
                    it[idPosition] = 1
                }
            }
        }
    }

    fun cleanUp() {
        println("Closing database connection.")
    }
}