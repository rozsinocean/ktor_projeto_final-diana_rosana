package com.rosana_diana.person

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Int? = null,
    val name: String,
    val birthdate: String,
    val gender: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val password: String,
    val nif: Int
)