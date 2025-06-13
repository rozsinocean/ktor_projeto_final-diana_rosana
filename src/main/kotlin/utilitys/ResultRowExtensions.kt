package com.rosana_diana.person

import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPerson(): Person {
    return Person(
        id = this[PersonTable.id],
        name = this[PersonTable.name],
        birthdate = this[PersonTable.birthdate].toString(),
        gender = this[PersonTable.gender],
        email = this[PersonTable.email],
        phone = this[PersonTable.phone],
        address = this[PersonTable.address],
        password = this[PersonTable.password],
        nif = this[PersonTable.nif]
    )
}