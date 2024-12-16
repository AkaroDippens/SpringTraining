package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "medicines")
class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicines_id_medicine_seq")
    @SequenceGenerator(name = "medicines_id_medicine_seq", sequenceName = "medicines_id_medicine_seq", allocationSize = 1)
    @Column(name = "id_medicine", nullable = false)
    var id: Int? = null

    @Size(max = 150)
    @NotNull
    @Column(name = "medicine_name", nullable = false, length = 150)
    var medicineName: String? = null

    @Size(max = 200)
    @NotNull
    @Column(name = "manufacturer", nullable = false, length = 200)
    var manufacturer: String? = null
}
