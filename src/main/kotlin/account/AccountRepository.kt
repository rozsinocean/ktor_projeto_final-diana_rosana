// src/main/kotlin/com/rosana_diana/account/AccountRepository.kt
package com.rosana_diana.account

import com.rosana_diana.client.ClientTable
import com.rosana_diana.accounttype.AccountTypeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class AccountRepository : AccountInterface {
    override fun allAccounts(): List<Account> = transaction {
        AccountTable.selectAll().map { it.toAccount() }
    }

    override fun getAccountById(id: Int): Account? = transaction {
        AccountTable.select { AccountTable.id_account eq id }
            .map { it.toAccount() }
            .singleOrNull()
    }

    override fun createAccount(account: Account): Account = transaction {
        val primaryHolderExists = ClientTable.select { ClientTable.id eq account.id_primaryholder }.singleOrNull() != null
        if (!primaryHolderExists) {
            throw IllegalArgumentException("Primary holder with ID ${account.id_primaryholder} does not exist.")
        }

        if (account.id_secondaryholder != null) {
            val secondaryHolderExists = ClientTable.select { ClientTable.id eq account.id_secondaryholder }.singleOrNull() != null
            if (!secondaryHolderExists) {
                throw IllegalArgumentException("Secondary holder with ID ${account.id_secondaryholder} does not exist.")
            }
        }

        val accountTypeExists = AccountTypeTable.select { AccountTypeTable.id eq account.id_accountType }.singleOrNull() != null
        if (!accountTypeExists) {
            throw IllegalArgumentException("Account type with ID ${account.id_accountType} does not exist.")
        }

        val ibanExists = AccountTable.select { AccountTable.iban eq account.iban }.singleOrNull() != null
        if (ibanExists) {
            throw IllegalArgumentException("IBAN ${account.iban} already exists.")
        }

        val insertedId = AccountTable.insert {
            it[iban] = account.iban
            it[balance] = account.balance
            it[id_primaryholder] = account.id_primaryholder
            it[id_secondaryholder] = account.id_secondaryholder
            it[opening_date] = LocalDate.parse(account.opening_date)
            it[id_accountType] = account.id_accountType
        } get AccountTable.id_account

        getAccountById(insertedId)!!
    }

    override fun updateAccount(id: Int, account: Account): Account? = transaction {
        val updatedRows = AccountTable.update({ AccountTable.id_account eq id }) {
            it[iban] = account.iban
            it[balance] = account.balance
            it[id_primaryholder] = account.id_primaryholder
            it[id_secondaryholder] = account.id_secondaryholder
            it[opening_date] = LocalDate.parse(account.opening_date)
            it[id_accountType] = account.id_accountType
        }

        if (updatedRows > 0) getAccountById(id) else null
    }

    override fun deleteAccount(id: Int): Boolean = transaction {
        AccountTable.deleteWhere { AccountTable.id_account eq id } > 0
    }

    override fun findByIban(iban: String): Account? = transaction {
        AccountTable.select { AccountTable.iban eq iban }
            .map { it.toAccount() }
            .singleOrNull()
    }

    override fun getAccountsByPrimaryHolder(primaryHolderId: Int): List<Account> = transaction {
        AccountTable.select { AccountTable.id_primaryholder eq primaryHolderId }
            .map { it.toAccount() }
    }

    override fun getAccountsBySecondaryHolder(secondaryHolderId: Int): List<Account> = transaction {
        AccountTable.select { AccountTable.id_secondaryholder eq secondaryHolderId }
            .map { it.toAccount() }
    }
}