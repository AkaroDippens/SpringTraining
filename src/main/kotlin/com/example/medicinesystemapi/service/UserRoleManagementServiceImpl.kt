package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Doctor
import com.example.medicinesystemapi.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserRoleManagementServiceImpl(
    private val userRepository: UserRepository,
    private val doctorService: DoctorService,
    private val doctorRepository: DoctorRepository,
    private val buildingRepository: BuildingRepository,
    private val specializationRepository: SpecializationRepository,
    private val roleRepository: RoleRepository,
) : UserRoleManagementService {
    @Transactional
    override fun changeUserRole(
        userId: Int,
        newRoleId: Int,
    ): Boolean {
        val user = userRepository.findById(userId.toLong()).orElse(null) ?: return false
        val oldRole = user.idRole?.roleName

        // Находим новую роль по id
        val newRole = roleRepository.findById(newRoleId.toLong()).orElse(null) ?: return false

        // Обновляем роль пользователя
        user.idRole = newRole
        userRepository.save(user)

        // Обрабатываем создание/удаление доктора на основе изменения роли
        when {
            oldRole != "DOCTOR" && newRole.roleName == "DOCTOR" -> {
                // Создаем нового доктора
                val doctor =
                    Doctor().apply {
                        this.id = null
                        this.fullName = user.fullName
                        this.idSpecialization = specializationRepository.findById(1).orElse(null)
                        this.idBuilding = buildingRepository.findById(1).orElse(null)
                        this.experience = LocalDate.now()
                    }
                doctorRepository.save(doctor)
            }
            oldRole == "DOCTOR" && newRole.roleName != "DOCTOR" -> {
                // Удаляем доктора
                doctorRepository.findAll()
                    .find { it.fullName == user.fullName }
                    ?.let { doctor ->
                        doctorRepository.deleteById(doctor.id!!.toLong())
                    }
            }
        }

        return true
    }
}
