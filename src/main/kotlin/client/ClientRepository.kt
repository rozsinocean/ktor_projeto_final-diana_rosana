package com.rosana_diana.client

import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ClientRepository : ClientInterface {
    override fun allClients(): List<Client> = transaction {
        ClientTable.selectAll().map {
            it.toClient()
        }
    }

    override fun getClientById(id: Int): Client? = transaction {
        ClientTable.select { ClientTable.id eq id }.map {
            it.toClient()
        }.singleOrNull()
    }

    override fun createClient(person: Client): Client = transaction {
        val personExists = PersonTable.select { PersonTable.id eq person.personId }.singleOrNull() != null
        if (!personExists) {
            throw IllegalArgumentException("Person with ID ${person.personId} does not exist.")
        }

        val existingClient = ClientTable.select { ClientTable.personId eq person.personId }.singleOrNull()
        if (existingClient != null) {
            throw IllegalArgumentException("A client already exists for person ID ${person.personId}.")
        }

        val insertedId = ClientTable.insert {
            it[ClientTable.personId] = person.personId
        } get ClientTable.id

        getClientById(insertedId)!!
    }

    override fun deleteClient(id: Int): Boolean = transaction {
        ClientTable.deleteWhere { ClientTable.id eq id } > 0
    }

    override fun findByPersonId(personId: Int): Client? = transaction {
        ClientTable.select { ClientTable.personId eq personId }
            .map { it.toClient() }
            .singleOrNull()
    }
}