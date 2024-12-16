package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.ViewUser

interface ViewUserService {
    fun findAllViewUsers(): List<ViewUser>
    fun findViewUserById(id: Int): ViewUser?
    fun exportViewUsersToCsv(): String
}