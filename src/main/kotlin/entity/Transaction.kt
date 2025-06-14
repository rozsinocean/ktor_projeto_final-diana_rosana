package com.rosana_diana.entity

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.time

object Transaction: Table() {
    val id_transaction = integer("id_transaction")
    val id_accountSource = integer("id_accountSource")
    val id_accountDestination = integer("id_accountDestination")
    val date = date("date")
    val time = time("time")
    val amount = double("amount")
}