package com.rosana_diana.account // Ajuste o pacote

import kotlinx.serialization.Serializable

@Serializable
data class Account(
        val id_account: Int,
        val iban: String,
        val balance: Double,
        val id_primaryholder: Int,
        val id_secondaryholder: Int? = null,
        val opening_date: String,
        val id_accountType: Int,
)