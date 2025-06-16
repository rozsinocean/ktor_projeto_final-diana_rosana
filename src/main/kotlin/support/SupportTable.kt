package com.rosana_diana.support

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ResultRow

object SupportTable : Table("supports") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255)
    val description = varchar("description", 255)

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toSupport() = Support(
    id = this[SupportTable.id],
    email = this[SupportTable.email],
    description = this[SupportTable.description]
)