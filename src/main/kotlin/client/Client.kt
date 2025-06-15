package com.rosana_diana.client

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: Int,
    val personId: Int
)