package com.rosana_diana.transaction

import com.rosana_diana.account.AccountRepository
import com.rosana_diana.transactiontype.TransactionTypeRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TransactionService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionTypeRepository: TransactionTypeRepository
) {

    class TransactionServiceException(message: String) : Exception(message)

    fun makeDeposit(accountIban: String, amount: Double) {
        if (amount <= 0) {
            throw TransactionServiceException("O valor do depósito deve ser positivo.")
        }

        transaction {
            val account = accountRepository.findByIban(accountIban)
                ?: throw TransactionServiceException("Conta não encontrada com IBAN: $accountIban")

            val updatedAccount = account.copy(balance = account.balance + amount)
            val accountUpdated = accountRepository.updateAccount(updatedAccount.id_account, updatedAccount)
                ?: throw TransactionServiceException("Falha ao atualizar o saldo da conta durante o depósito. Operação desfeita.")

            val depositTypeId = transactionTypeRepository.getById(1)?.id
                ?: throw TransactionServiceException("Tipo de transação 'DEPOSITO' não encontrado. Verifique se foi criado no banco de dados.")

            val currentDateTime = LocalDateTime.now().toString()
            val depositTransaction = Transaction(
                id_source_account = null,
                id_destination_account = account.id_account,
                amount = amount,
                transaction_datetime = currentDateTime,
                id_transaction_type = depositTypeId,
                description = "Depósito na conta ${account.iban}"
            )
            transactionRepository.createTransaction(depositTransaction)
        }
    }

    fun makeWithdrawal(accountIban: String, amount: Double) {
        if (amount <= 0) {
            throw TransactionServiceException("O valor do levantamento deve ser positivo.")
        }

        transaction {
            val account = accountRepository.findByIban(accountIban)
                ?: throw TransactionServiceException("Conta não encontrada com IBAN: $accountIban")

            if (account.balance < amount) {
                throw TransactionServiceException("Fundos insuficientes na conta (IBAN: $accountIban). Saldo atual: ${account.balance}")
            }

            val updatedAccount = account.copy(balance = account.balance - amount)
            val accountUpdated = accountRepository.updateAccount(updatedAccount.id_account, updatedAccount)
                ?: throw TransactionServiceException("Falha ao atualizar o saldo da conta durante o levantamento. Operação desfeita.")

            val withdrawalTypeId = transactionTypeRepository.getById(2)?.id
                ?: throw TransactionServiceException("Tipo de transação 'LEVANTAMENTO' não encontrado. Verifique se foi criado no banco de dados.")

            val currentDateTime = LocalDateTime.now().toString()
            val withdrawalTransaction = Transaction(
                id_source_account = account.id_account,
                id_destination_account = null,
                amount = amount,
                transaction_datetime = currentDateTime,
                id_transaction_type = withdrawalTypeId,
                description = "Levantamento da conta ${account.iban}"
            )
            transactionRepository.createTransaction(withdrawalTransaction)
        }
    }

    fun makeTransfer(sourceIban: String, destinationIban: String, amount: Double) {
        if (amount <= 0) {
            throw TransactionServiceException("O valor da transferência deve ser positivo.")
        }
        if (sourceIban == destinationIban) {
            throw TransactionServiceException("Não é possível transferir para a mesma conta de origem e destino.")
        }

        transaction {
            val sourceAccount = accountRepository.findByIban(sourceIban)
                ?: throw TransactionServiceException("Conta de origem não encontrada com IBAN: $sourceIban")

            val destinationAccount = accountRepository.findByIban(destinationIban)
                ?: throw TransactionServiceException("Conta de destino não encontrada com IBAN: $destinationIban")

            if (sourceAccount.balance < amount) {
                throw TransactionServiceException("Fundos insuficientes na conta de origem (IBAN: $sourceIban). Saldo atual: ${sourceAccount.balance}")
            }

            val updatedSourceAccount = sourceAccount.copy(balance = sourceAccount.balance - amount)
            val updatedDestinationAccount = destinationAccount.copy(balance = destinationAccount.balance + amount)

            val sourceUpdated = accountRepository.updateAccount(updatedSourceAccount.id_account, updatedSourceAccount)
            val destUpdated = accountRepository.updateAccount(updatedDestinationAccount.id_account, updatedDestinationAccount)

            if (sourceUpdated == null || destUpdated == null) {
                throw TransactionServiceException("Falha ao atualizar o saldo das contas durante a transferência. Operação desfeita.")
            }

            val transferTypeId = transactionTypeRepository.getById(3)?.id
                ?: throw TransactionServiceException("Tipo de transação 'TRANSFERENCIA' não encontrado. Verifique se foi criado no banco de dados.")

            val currentDateTime = LocalDateTime.now().toString()

            val sourceTransaction = Transaction(
                id_source_account = sourceAccount.id_account,
                id_destination_account = destinationAccount.id_account,
                amount = amount,
                transaction_datetime = currentDateTime,
                id_transaction_type = transferTypeId,
                description = "Transferência para conta ${destinationAccount.iban}"
            )
            transactionRepository.createTransaction(sourceTransaction)
        }
    }
}