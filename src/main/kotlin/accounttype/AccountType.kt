package com.rosana_diana.accounttype

import kotlinx.serialization.Serializable

@Serializable
data class AccountType(
    val id: Int? = null,
    val name: String
)