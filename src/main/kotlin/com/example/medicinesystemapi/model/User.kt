package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import com.example.medicinesystemapi.model.Role
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate


@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_user_seq")
    @SequenceGenerator(name = "users_id_user_seq", sequenceName = "users_id_user_seq", allocationSize = 1)
    @Column(name = "id_user", nullable = false)
    var id: Int? = null

    @Size(max = 150)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String? = null

    @Size(max = 20)
    @Column(name = "contact_number", length = 20)
    var contactNumber: String? = null

    @Size(max = 30)
    @NotNull
    @Column(name = "mhi_policy", nullable = false, length = 30)
    var mhiPolicy: String? = null

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null

    @NotNull
    @Column(name = "password", nullable = false)
    var password: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_role", nullable = false)
    var idRole: Role? = null
}