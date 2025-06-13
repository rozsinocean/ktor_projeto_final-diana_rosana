package com.rosana_diana

import io.ktor.server.application.*
import io.ktor.server.config.*

fun Application.configureDatabases() {
    // Load embedded flag from environment variable or configuration
    val embedded = environment.config.propertyOrNull("database.embedded")?.getString()?.toBoolean() ?: false

    // Initialize the database with the configuration
    DatabaseFactory.init(environment.config, embedded)

    // Optionally, add a shutdown hook to clean up resources
    environment.monitor.subscribe(ApplicationStopping) {
        DatabaseFactory.cleanUp()
    }
}