package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "roles")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_id_role_seq")
    @SequenceGenerator(name = "roles_id_role_seq", sequenceName = "roles_id_role_seq", allocationSize = 1)
    @Column(name = "id_role", nullable = false)
    var id: Int? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "role_name", nullable = false, length = 100)
    var roleName: String? = null
}
