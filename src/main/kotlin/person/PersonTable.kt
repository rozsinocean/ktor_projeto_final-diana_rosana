package com.rosana_diana.person

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object PersonTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val birthdate = date("birthdate")
    val gender = varchar("gender", 50)
    val email = varchar("email", 255).uniqueIndex()
    val phone = varchar("phone", 20).nullable() // Nullable field
    val address = varchar("address", 255).nullable() // Nullable field
    val password = varchar("password", 255)
    val nif = integer("nif").uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}