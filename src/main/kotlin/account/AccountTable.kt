package com.rosana_diana.account

import com.rosana_diana.client.ClientTable
import com.rosana_diana.accounttype.AccountTypeTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date

object AccountTable : Table("account") {
    val id_account = integer("id_account").autoIncrement()
    override val primaryKey = PrimaryKey(id_account)

    val iban = varchar("iban", 34).uniqueIndex()
    val balance = double("balance")
    val id_primaryholder = reference("id_primaryholder", ClientTable.id, onDelete = ReferenceOption.NO_ACTION)
    val id_secondaryholder = reference("id_secondaryholder", ClientTable.id, onDelete = ReferenceOption.NO_ACTION).nullable()
    val opening_date = date("opening_date")
    val id_accountType = reference("id_account_type", AccountTypeTable.id, onDelete = ReferenceOption.NO_ACTION)
}

fun ResultRow.toAccount() = Account(
    id_account = this[AccountTable.id_account],
    iban = this[AccountTable.iban],
    balance = this[AccountTable.balance],
    id_primaryholder = this[AccountTable.id_primaryholder],
    id_secondaryholder = this[AccountTable.id_secondaryholder],
    opening_date = this[AccountTable.opening_date].toString(),
    id_accountType = this[AccountTable.id_accountType]
)