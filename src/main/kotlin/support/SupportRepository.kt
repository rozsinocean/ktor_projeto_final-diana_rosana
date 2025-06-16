package com.rosana_diana.support

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SupportRepository : SupportInterface {
    override fun allSupports(): List<Support> = transaction {
        SupportTable.selectAll().map { it.toSupport() }
    }

    override fun getSupportById(id: Int): Support? = transaction {
        SupportTable.select { SupportTable.id eq id }
            .map { it.toSupport() }
            .singleOrNull() // Retorna null se não encontrar ou mais de um (o que não deve acontecer para PK)
    }

    override fun createSupport(support: Support): Support = transaction {
        val insertedId = SupportTable.insert {
            it[email] = support.email
            it[description] = support.description
        } get SupportTable.id

        getSupportById(insertedId)!!
    }

    override fun deleteSupport(id: Int): Boolean = transaction {
        SupportTable.deleteWhere { SupportTable.id eq id } > 0
    }
}