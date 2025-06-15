package com.rosana_diana.transactiontype

import kotlinx.serialization.Serializable

@Serializable
data class TransactionType(
    val id: Int? = null,
    val name: String
)