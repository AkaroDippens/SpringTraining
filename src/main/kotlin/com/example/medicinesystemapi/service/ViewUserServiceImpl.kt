package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.ViewUser
import com.example.medicinesystemapi.repository.ViewUserRepository
import com.opencsv.CSVWriter
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class ViewUserServiceImpl(
    private val viewUserRepository: ViewUserRepository,
) : ViewUserService {
    override fun findAllViewUsers(): List<ViewUser> {
        return viewUserRepository.findAll()
    }

    override fun findViewUserById(id: Int): ViewUser? {
        return viewUserRepository.findById(id).orElse(null)
    }

    override fun exportViewUsersToCsv(): String {
        val users = viewUserRepository.findAll()
        val stringWriter = StringWriter()
        val csvWriter = CSVWriter(stringWriter)

        // Записываем заголовки столбцов
        val header = arrayOf("ID", "Full Name", "Contact Number", "MHI Policy", "Birth Date", "Role Name")
        csvWriter.writeNext(header)

        // Записываем данные
        users.forEach { user ->
            val row =
                arrayOf(
                    user.id.toString(),
                    user.fullName,
                    user.contactNumber,
                    user.mhiPolicy,
                    user.birthDate?.toString(),
                    user.roleName,
                )
            csvWriter.writeNext(row)
        }

        csvWriter.close()
        return stringWriter.toString()
    }
}
