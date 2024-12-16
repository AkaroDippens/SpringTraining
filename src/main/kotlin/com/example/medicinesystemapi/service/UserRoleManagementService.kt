package com.example.medicinesystemapi.service

interface UserRoleManagementService {
    fun changeUserRole(userId: Int, newRoleId: Int): Boolean
}