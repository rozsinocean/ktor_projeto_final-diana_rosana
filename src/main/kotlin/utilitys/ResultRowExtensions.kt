package com.rosana_diana.person

import com.rosana_diana.person.Person
import com.rosana_diana.person.PersonTable
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPerson(): Person {
    return Person(
        id = this[PersonTable.id],
        name = this[PersonTable.name],
        birthdate = this[PersonTable.birthdate].toString(), // Convert LocalDate to String
        gender = this[PersonTable.gender],
        email = this[PersonTable.email],
        phone = this[PersonTable.phone], // Nullable fields
        address = this[PersonTable.address], // Nullable fields
        password = this[PersonTable.password],
        nif = this[PersonTable.nif]
    )
}