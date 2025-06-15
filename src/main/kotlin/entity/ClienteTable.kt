package com.rosana_diana.entity

import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ClienteTable : Table() {
    val id = integer("id").autoIncrement()

    override val primaryKey = PrimaryKey(id)
    val personId = reference("person_id", PersonTable.id, onDelete = ReferenceOption.CASCADE)
}