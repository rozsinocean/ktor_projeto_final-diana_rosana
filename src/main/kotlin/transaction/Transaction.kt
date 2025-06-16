package com.rosana_diana.transaction

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id_transaction: Int? = null,
    val id_source_account: Int?,
    val id_destination_account: Int?,
    val amount: Double,
    val transaction_datetime: String,
    val id_transaction_type: Int,
    val description: String? = null
)

