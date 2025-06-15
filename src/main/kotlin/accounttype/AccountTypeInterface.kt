package com.rosana_diana.accounttype

interface AccountTypeInterface {
    fun allAccountTypes(): List<AccountType>
    fun getAccountTypeById(id: Int): AccountType?
    fun getAccountTypeByName(name: String): AccountType?
}