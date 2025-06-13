package com.rosana_diana.person

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Convert String to LocalDate
fun String.toDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}