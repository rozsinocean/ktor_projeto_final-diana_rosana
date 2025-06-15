package com.rosana_diana.transactiontype

import org.jetbrains.exposed.sql.Table

object TransactionTypeTable : Table("transaction_type") {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 50).uniqueIndex() // Ex: "Deposit", "Withdrawal", "Transfer"
}