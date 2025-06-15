package com.rosana_diana.client

import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object ClientTable : Table("client") {
    val id = integer("id").autoIncrement()

    override val primaryKey = PrimaryKey(id)
    val personId = reference("person_id", PersonTable.id, onDelete = ReferenceOption.CASCADE)
}

fun ResultRow.toClient() = Client(
    id = this[ClientTable.id],
    personId = this[ClientTable.personId]
)