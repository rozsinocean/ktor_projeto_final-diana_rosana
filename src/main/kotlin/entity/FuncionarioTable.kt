package com.rosana_diana.entity

import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object FuncionarioTable :Table() {
    val id = integer("id").autoIncrement()
    val admissionDate = date("admissionDate")
    val salary = double("salary")
    val idPosition = integer("Position")

    override val primaryKey = PrimaryKey(id)
    val personId = reference("person_id", PersonTable.id, onDelete = ReferenceOption.CASCADE)
}