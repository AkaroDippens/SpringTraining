package com.example.medicinesystemapi.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Immutable
import java.time.LocalDate

@Immutable
@Entity
@Table(name = "view_users")
class ViewUser(
    @jakarta.persistence.Id
    @Column(name = "id_user")
    var id: Int? = null,

    @Size(max = 150)
    @Column(name = "full_name", length = 150)
    var fullName: String? = null,

    @Size(max = 20)
    @Column(name = "contact_number", length = 20)
    var contactNumber: String? = null,

    @Size(max = 30)
    @Column(name = "mhi_policy", length = 30)
    var mhiPolicy: String? = null,

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null,

    @Size(max = 100)
    @Column(name = "role_name", length = 100)
    var roleName: String? = null
)