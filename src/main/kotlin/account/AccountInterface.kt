package com.rosana_diana.account

interface AccountInterface {
    fun allAccounts(): List<Account>
    fun getAccountById(id: Int): Account?
    fun createAccount(account: Account): Account
    fun updateAccount(id: Int, account: Account): Account?
    fun deleteAccount(id: Int): Boolean
    fun findByIban(iban: String): Account?
    fun getAccountsByPrimaryHolder(primaryHolderId: Int): List<Account>
    fun getAccountsBySecondaryHolder(secondaryHolderId: Int): List<Account>
}