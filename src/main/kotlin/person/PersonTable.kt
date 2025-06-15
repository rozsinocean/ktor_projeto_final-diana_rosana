package com.rosana_diana.person

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.ResultRow

object PersonTable : Table("person") {
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

fun ResultRow.toPerson(): Person {
    return Person(
        id = this[PersonTable.id],
        name = this[PersonTable.name],
        birthdate = this[PersonTable.birthdate].toString(),
        gender = this[PersonTable.gender],
        email = this[PersonTable.email],
        phone = this[PersonTable.phone],
        address = this[PersonTable.address],
        password = this[PersonTable.password],
        nif = this[PersonTable.nif]
    )
}