package com.rosana_diana.employee

import com.rosana_diana.client.Client
import com.rosana_diana.client.ClientTable
import com.rosana_diana.person.PersonTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

@Serializable
data class Employee (
    val id: Int,
    val admissionDate: String,
    val salary: Double,
    val idPosition: String,
    val personId: Int
)
