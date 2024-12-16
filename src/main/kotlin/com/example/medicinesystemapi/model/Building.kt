package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "buildings")
class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "buildings_id_building_seq")
    @SequenceGenerator(name = "buildings_id_building_seq", sequenceName = "buildings_id_building_seq", allocationSize = 1)
    @Column(name = "id_building", nullable = false)
    var id: Int? = null

    @Size(max = 150)
    @NotNull
    @Column(name = "building_name", nullable = false, length = 150)
    var buildingName: String? = null

    @Size(max = 150)
    @Column(name = "address", length = 150)
    var address: String? = null

    @Size(max = 50)
    @Column(name = "contact_number", length = 50)
    var contactNumber: String? = null
}
