// src/main/kotlin/com/rosana_diana/transaction/TransactionRepository.kt
package com.rosana_diana.transaction

import com.rosana_diana.account.AccountTable
import com.rosana_diana.transactiontype.TransactionTypeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TransactionRepository : TransactionInterface {
    override fun allTransactions(): List<Transaction> = transaction {
        TransactionTable.selectAll().map { it.toTransaction() }
    }

    override fun getTransactionById(id: Int): Transaction? = transaction {
        TransactionTable.select { TransactionTable.id_transaction eq id }
            .map { it.toTransaction() }
            .singleOrNull()
    }

    override fun createTransaction(transaction: Transaction): Transaction = transaction {
        // Validações de contas (certifique-se que as contas existem)
        if (transaction.id_source_account != null) {
            val sourceAccountExists = AccountTable.select { AccountTable.id_account eq transaction.id_source_account }.singleOrNull() != null
            if (!sourceAccountExists) {
                throw IllegalArgumentException("Source account with ID ${transaction.id_source_account} does not exist.")
            }
        }
        if (transaction.id_destination_account != null) {
            val destAccountExists = AccountTable.select { AccountTable.id_account eq transaction.id_destination_account }.singleOrNull() != null
            if (!destAccountExists) {
                throw IllegalArgumentException("Destination account with ID ${transaction.id_destination_account} does not exist.")
            }
        }

        // Validação de tipo de transação
        val transactionTypeExists = TransactionTypeTable.select { TransactionTypeTable.id eq transaction.id_transaction_type }.singleOrNull() != null
        if (!transactionTypeExists) {
            throw IllegalArgumentException("Transaction type with ID ${transaction.id_transaction_type} does not exist.")
        }

        val insertedId = TransactionTable.insert {
            it[id_source_account] = transaction.id_source_account
            it[id_destination_account] = transaction.id_destination_account
            it[amount] = transaction.amount
            it[transaction_datetime] = LocalDateTime.parse(transaction.transaction_datetime) // String para LocalDateTime
            it[id_transaction_type] = transaction.id_transaction_type
            it[description] = transaction.description
        } get TransactionTable.id_transaction

        getTransactionById(insertedId)!!
    }

    override fun updateTransaction(id: Int, transaction: Transaction): Transaction? = transaction {
        val updatedRows = TransactionTable.update({ TransactionTable.id_transaction eq id }) {
            it[id_source_account] = transaction.id_source_account
            it[id_destination_account] = transaction.id_destination_account
            it[amount] = transaction.amount
            it[transaction_datetime] = LocalDateTime.parse(transaction.transaction_datetime)
            it[id_transaction_type] = transaction.id_transaction_type
            it[description] = transaction.description
        }

        if (updatedRows > 0) getTransactionById(id) else null
    }

    override fun deleteTransaction(id: Int): Boolean = transaction {
        TransactionTable.deleteWhere { TransactionTable.id_transaction eq id } > 0
    }

    override fun getTransactionsBySourceAccount(accountId: Int): List<Transaction> = transaction {
        TransactionTable.select { TransactionTable.id_source_account eq accountId }
            .map { it.toTransaction() }
    }

    override fun getTransactionsByDestinationAccount(accountId: Int): List<Transaction> = transaction {
        TransactionTable.select { TransactionTable.id_destination_account eq accountId }
            .map { it.toTransaction() }
    }

    override fun getTransactionsBetweenDates(startDate: String, endDate: String): List<Transaction> = transaction {
        val startDateTime = LocalDateTime.parse(startDate) // Assumindo formato compatível com ISO
        val endDateTime = LocalDateTime.parse(endDate)

        TransactionTable.select {
            TransactionTable.transaction_datetime.between(startDateTime, endDateTime)
        }.map { it.toTransaction() }
    }

    override fun getTransactionsByType(typeId: Int): List<Transaction> = transaction {
        TransactionTable.select { TransactionTable.id_transaction_type eq typeId }
            .map { it.toTransaction() }
    }
}