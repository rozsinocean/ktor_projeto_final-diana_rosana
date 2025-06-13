package com.rosana_diana.person

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.text.insert

class PersonRepository : PersonInterface {

    override fun allPerson(): List<Person> = transaction {
        PersonTable.selectAll().map {
            it.toPerson() // Use the toPerson extension function
        }
    }

    override fun getPersonById(id: Int): Person? = transaction {
        PersonTable.select { PersonTable.id eq id }.map {
            it.toPerson() // Use the toPerson extension function
        }.singleOrNull()
    }

    override fun createPerson(person: Person): Person {
        val insertedId = transaction {
            PersonTable.insert {
                it[name] = person.name
                it[email] = person.email
                it[phone] = person.phone
                it[address] = person.address
                it[nif] = person.nif
                it[gender] = person.gender
                it[password] = person.password
                it[birthdate] = person.birthdate.toDate()
            } get PersonTable.id
        }

        return getPersonById(insertedId!!)!!
    }

    override fun updatePerson(person: Person): Person? = transaction {
        val updatedRows = PersonTable.update({ PersonTable.id eq person.id!! }) {
            it[name] = person.name
            it[email] = person.email
            it[phone] = person.phone
            it[address] = person.address
            it[nif] = person.nif
            it[gender] = person.gender
            it[password] = person.password
            it[birthdate] = person.birthdate.toDate()
        }

        if (updatedRows == 0) null else getPersonById(person.id!!)
    }

    override fun deletePerson(id: Int): Boolean = transaction {
        PersonTable.deleteWhere { PersonTable.id eq id } > 0
    }

    override fun findByEmail(email: String): Person? = transaction {
        PersonTable.select {
            PersonTable.email eq email
        }.map { it.toPerson() }.singleOrNull() // Returns null if no person is found
    }
}
