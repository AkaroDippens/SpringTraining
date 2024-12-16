package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate

@Entity
@Table(name = "doctors")
class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctors_id_doctor_seq")
    @SequenceGenerator(name = "doctors_id_doctor_seq", sequenceName = "doctors_id_doctor_seq", allocationSize = 1)
    @Column(name = "id_doctor", nullable = false)
    var id: Int? = null

    @Size(max = 150)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String? = null

    @Column(name = "experience")
    var experience: LocalDate? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_specialization", nullable = false)
    var idSpecialization: Specialization? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_building", nullable = false)
    var idBuilding: Building? = null
}
