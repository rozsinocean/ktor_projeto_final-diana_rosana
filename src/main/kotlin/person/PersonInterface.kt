package com.rosana_diana.person

interface PersonInterface {
    fun allPerson(): List<Person> // Fix return type to List<Person>
    fun getPersonById(id: Int): Person?
    fun createPerson(person: Person): Person?
    fun updatePerson(person: Person): Person?
    fun deletePerson(id: Int): Boolean
}