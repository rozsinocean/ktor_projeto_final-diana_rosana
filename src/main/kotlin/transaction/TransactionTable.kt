package com.rosana_diana.transaction

import com.rosana_diana.account.AccountTable // Importe AccountTable
import com.rosana_diana.transactiontype.TransactionTypeTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime // Para usar LocalDateTime com Exposed

object TransactionTable : Table("transactions") { // Use "transaction_" para evitar conflito com palavra reservada
    val id_transaction = integer("id_transaction").autoIncrement()
    override val primaryKey = PrimaryKey(id_transaction)

    val id_source_account = reference("id_source_account", AccountTable.id_account, onDelete = ReferenceOption.NO_ACTION).nullable()
    val id_destination_account = reference("id_destination_account", AccountTable.id_account, onDelete = ReferenceOption.NO_ACTION).nullable()

    val amount = double("amount")
    val transaction_datetime = datetime("transaction_datetime") // Usando datetime para LocalDateTime
    val id_transaction_type = reference("id_transaction_type", TransactionTypeTable.id, onDelete = ReferenceOption.NO_ACTION)
    val description = varchar("description", 255).nullable() // Descrição da transação
}

fun ResultRow.toTransaction() = Transaction(
    id_transaction = this[TransactionTable.id_transaction],
    id_source_account = this[TransactionTable.id_source_account],
    id_destination_account = this[TransactionTable.id_destination_account],
    amount = this[TransactionTable.amount],
    transaction_datetime = this[TransactionTable.transaction_datetime].toString(), // LocalDateTime para String
    id_transaction_type = this[TransactionTable.id_transaction_type],
    description = this[TransactionTable.description]
)