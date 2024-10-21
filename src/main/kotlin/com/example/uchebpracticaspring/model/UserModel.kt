package com.example.uchebpracticaspring.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
data class UserModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @NotBlank(message = "Username is required")
    var username: String = "",
    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    var password: String = "",
    var isActive: Boolean = true,

    @ElementCollection(targetClass = RoleEnum::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")])
    @Enumerated(EnumType.STRING)
    var roles: Set<RoleEnum> = setOf()
)