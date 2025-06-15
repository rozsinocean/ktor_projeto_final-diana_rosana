package com.rosana_diana.employee

import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class EmployeeRepository : EmployeeInterface {
    override fun allEmployees(): List<Employee> = transaction {
        EmployeeTable.selectAll().map {
            it.toEmployee()
        }
    }

    override fun getEmployeeById(id: Int): Employee? = transaction {
        EmployeeTable.select { EmployeeTable.id eq id }.map {
            it.toEmployee()
        }.singleOrNull()
    }

    override fun createEmployee(person: Employee): Employee = transaction {
        val personExists = PersonTable.select { PersonTable.id eq person.personId }.singleOrNull() != null
        if (!personExists) {
            throw IllegalArgumentException("Person with ID ${person.personId} does not exist.")
        }

        val existingEmployee = EmployeeTable.select { EmployeeTable.personId eq person.personId }.singleOrNull()
        if (existingEmployee != null) {
            throw IllegalArgumentException("A Employee already exists for person ID ${person.personId}.")
        }

        val insertedId = EmployeeTable.insert {
            it[EmployeeTable.personId] = person.personId
        } get EmployeeTable.id

        getEmployeeById(insertedId)!!
    }

    override fun deleteEmployee(id: Int): Boolean = transaction {
        EmployeeTable.deleteWhere { EmployeeTable.id eq id } > 0
    }

    override fun findByPersonId(personId: Int): Employee? = transaction {
        EmployeeTable.select { EmployeeTable.personId eq personId }
            .map { it.toEmployee() }
            .singleOrNull()
    }
}