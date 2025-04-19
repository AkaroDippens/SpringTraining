package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.MedicalRecord
import com.example.medicinesystemapi.model.User
import com.example.medicinesystemapi.service.MedicalRecordService
import com.example.medicinesystemapi.service.UserService
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@Controller
class ViewController
    @Autowired
    constructor(
        private val userService: UserService,
        private val medicalRecordService: MedicalRecordService,
        private val authController: AuthController,
    ) {
        @GetMapping("/login")
        fun login(): String {
            return "login"
        }

        @GetMapping("/register")
        fun register(): String {
            return "register"
        }

        @GetMapping("/")
        fun homeProfilePage(): String {
            return "profile"
        }

        @GetMapping("/profile")
        fun profilePage(): String {
            return "profile"
        }

        @GetMapping("/records")
        fun recordPage(): String {
            return "appointment"
        }

        @GetMapping("/doctors")
        fun doctorPage(): String {
            return "doctor"
        }

        @GetMapping("/users")
        fun userPage(): String {
            return "users"
        }

        @GetMapping("/specializations")
        fun specializationPage(): String {
            return "specializations-table"
        }

        @GetMapping("/medicines")
        fun medicinePage(): String {
            return "medicines-table"
        }

        @GetMapping("/buildings")
        fun buildingPage(): String {
            return "buildings-table"
        }

        @GetMapping("/doctor/appointments")
        fun doctorAppointmentsPage(): String {
            return "doctor-appointments"
        }

        @GetMapping("/db-admin/logs")
        fun logsPage(): String {
            return "logs"
        }

        @GetMapping("/db-admin/statistics")
        fun statisticsPage(): String {
            return "statistics"
        }

        @GetMapping("/logout")
        fun logoutPage(): String {
            return "redirect:/login"
        }

        @GetMapping("/api/profile")
        fun getProfile(
            @RequestHeader(value = "Authorization") token: String?,
        ): ResponseEntity<ProfileResponse> {
            val cleanToken = token?.replace("Bearer ", "") ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            val mhiPolicy = validateToken(cleanToken)

            val user =
                userService.findUserByMhiPolicy(mhiPolicy)
                    ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

            val medicalRecord =
                medicalRecordService.findMedicalRecordByUserId(user.id?.toLong())
                    ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

            return ResponseEntity.ok(ProfileResponse(user, medicalRecord))
        }

        // Функция валидации токена
        private fun validateToken(token: String): String {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(
                        "cb7f3bb50c74c595bf95eaab227b9101ecb38f99adb19f31360c374a958a18a466fa192c0b409771b00c1bb4e0b6fb57ee1541ff25ceca686b1d4161f0c4d92a".toByteArray(),
                    )
                    .build()
                    .parseClaimsJws(token)
                    .body

            return claims.subject
        }
    }

data class ProfileResponse(
    val user: User,
    val medicalRecord: MedicalRecord,
)
