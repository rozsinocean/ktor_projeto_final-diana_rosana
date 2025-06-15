package com.rosana_diana.employee

import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object EmployeeTable :Table("employee") {
    val id = integer("id").autoIncrement()
    val admissionDate = date("admissionDate")
    val salary = double("salary")
    val idPosition = integer("Position")

    override val primaryKey = PrimaryKey(id)
    val personId = reference("person_id", PersonTable.id, onDelete = ReferenceOption.CASCADE)
}

fun ResultRow.toEmployee() = Employee(
    id = this[EmployeeTable.id],
    admissionDate = this[EmployeeTable.admissionDate].toString(),
    salary = this[EmployeeTable.salary],
    idPosition = this[EmployeeTable.idPosition].toString(),
    personId = this[EmployeeTable.personId]
)