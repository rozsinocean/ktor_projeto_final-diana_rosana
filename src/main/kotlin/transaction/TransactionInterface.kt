
package com.rosana_diana.transaction

interface TransactionInterface {
    fun allTransactions(): List<Transaction>
    fun getTransactionById(id: Int): Transaction?
    fun createTransaction(transaction: Transaction): Transaction
    fun updateTransaction(id: Int, transaction: Transaction): Transaction?
    fun deleteTransaction(id: Int): Boolean
    fun getTransactionsBySourceAccount(accountId: Int): List<Transaction>
    fun getTransactionsByDestinationAccount(accountId: Int): List<Transaction>
    fun getTransactionsBetweenDates(startDate: String, endDate: String): List<Transaction> // Exemplo com String
    fun getTransactionsByType(typeId: Int): List<Transaction>
}