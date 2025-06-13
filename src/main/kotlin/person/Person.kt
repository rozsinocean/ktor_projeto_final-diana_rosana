package com.rosana_diana.person

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Int? = null,
    val name: String,
    val birthdate: String, // String format for easier serialization
    val gender: String,
    val email: String,
    val phone: String?, // Nullable to match table
    val address: String?, // Nullable to match table
    val password: String,
    val nif: Int
)