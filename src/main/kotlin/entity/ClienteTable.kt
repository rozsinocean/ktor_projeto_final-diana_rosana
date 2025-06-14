package com.rosana_diana.entity

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object ClienteTable : Table() {
    val id = integer("id").autoIncrement()

    override val primaryKey = PrimaryKey(id)
}