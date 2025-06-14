package com.rosana_diana.entity

import kotlinx.serialization.Serializable

@Serializable
data class Conta (
        val id_account: Int? = null,
        val iban: String,
        val balance: Double,
        val id_primaryholder: Int,
        val id_secondaryholder: Int? = null,
        val opening_date: String,
        val id_accountType: Int,
)

