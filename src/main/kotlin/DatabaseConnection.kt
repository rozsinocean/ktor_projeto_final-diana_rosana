package com.rosana_diana

import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.server.application.*
import io.ktor.server.config.*

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
            SchemaUtils.create(PersonTable)
        }
    }

    fun cleanUp() {
        println("Closing database connection.")
    }
}