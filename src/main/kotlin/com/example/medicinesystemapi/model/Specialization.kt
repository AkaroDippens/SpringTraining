package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "specializations")
class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "specializations_id_specialization_seq")
    @SequenceGenerator(name = "specializations_id_specialization_seq", sequenceName = "specializations_id_specialization_seq", allocationSize = 1)
    @Column(name = "id_specialization", nullable = false)
    var id: Int? = null

    @Size(max = 250)
    @NotNull
    @Column(name = "specialization_name", nullable = false, length = 250)
    var specializationName: String? = null
}