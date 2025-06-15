package com.rosana_diana.accounttype

import org.jetbrains.exposed.sql.Table

object AccountTypeTable : Table("account_type") {
    val id = integer("id").autoIncrement()

    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 50).uniqueIndex()
}