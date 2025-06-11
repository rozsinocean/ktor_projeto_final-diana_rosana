package com.rosana_diana

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureSockets()
    configureHTTP()
    configureTemplating()
    configureRouting()
}
