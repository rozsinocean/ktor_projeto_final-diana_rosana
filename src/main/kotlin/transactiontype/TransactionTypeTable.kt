package com.rosana_diana.transactiontype

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction

object TransactionTypeTable : Table("transaction_type") {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 50).uniqueIndex() // Ex: "Deposit", "Withdrawal", "Transfer"
}

fun ResultRow.toTransactionType() = TransactionType(
    id = this[TransactionTypeTable.id],
    name = this[TransactionTypeTable.name]
)