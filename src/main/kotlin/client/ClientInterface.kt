package com.rosana_diana.client

interface ClientInterface {
    fun allClients(): List<Client>
    fun getClientById(id: Int): Client?
    fun createClient(person: Client): Client
    fun deleteClient(id: Int): Boolean
    fun findByPersonId(personId: Int): Client?
}