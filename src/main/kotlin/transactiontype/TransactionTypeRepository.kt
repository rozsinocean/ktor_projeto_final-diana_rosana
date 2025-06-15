package com.rosana_diana.transactiontype

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionTypeRepository {
    fun getByName(name: String): TransactionType? = transaction {
        TransactionTypeTable.select { TransactionTypeTable.name eq name }
            .map { it.toTransactionType() }
            .singleOrNull()
    }

    fun createType(name: String): TransactionType = transaction {
        val insertedId = TransactionTypeTable.insert {
            it[TransactionTypeTable.name] = name
        } get TransactionTypeTable.id

        getById(insertedId)!!
    }

    fun getById(id: Int): TransactionType? = transaction {
        TransactionTypeTable.select { TransactionTypeTable.id eq id }
            .map { it.toTransactionType() }
            .singleOrNull()
    }

    fun getAllTypes(): List<TransactionType> = transaction {
        TransactionTypeTable.selectAll().map { it.toTransactionType() }
    }
}