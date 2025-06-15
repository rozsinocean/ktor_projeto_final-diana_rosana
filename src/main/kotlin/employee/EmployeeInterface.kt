package com.rosana_diana.employee

interface EmployeeInterface {
    fun allEmployees(): List<Employee>
    fun getEmployeeById(id: Int): Employee?
    fun createEmployee(person: Employee): Employee
    fun deleteEmployee(id: Int): Boolean
    fun findByPersonId(personId: Int): Employee?
}