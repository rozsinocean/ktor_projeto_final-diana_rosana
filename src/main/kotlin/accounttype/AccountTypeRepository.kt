package com.rosana_diana.accounttype

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun ResultRow.toAccountType() = AccountType(
    id = this[AccountTypeTable.id],
    name = this[AccountTypeTable.name]
)

class AccountTypeRepository : AccountTypeInterface {
    override fun allAccountTypes(): List<AccountType> = transaction {
        AccountTypeTable.selectAll().map { it.toAccountType() }
    }

    override fun getAccountTypeById(id: Int): AccountType? = transaction {
        AccountTypeTable.select { AccountTypeTable.id eq id }
            .map { it.toAccountType() }
            .singleOrNull()
    }

    override fun getAccountTypeByName(name: String): AccountType? = transaction {
        AccountTypeTable.select { AccountTypeTable.name eq name }
            .map { it.toAccountType() }
            .singleOrNull()
    }
}