package com.rosana_diana.utilitys

import kotlin.random.Random

object IbanGenerator {
    fun generateIban(): String {
        val countryCode = "PT"
        val checkDigits = Random.nextInt(100).toString().padStart(2, '0')

        val bbanLength = 30
        val chars = ('0'..'9') + ('A'..'Z')

        val randomBBAN = (1..bbanLength)
            .map { chars.random() }
            .joinToString("")

        return "$countryCode$checkDigits$randomBBAN"
    }
}