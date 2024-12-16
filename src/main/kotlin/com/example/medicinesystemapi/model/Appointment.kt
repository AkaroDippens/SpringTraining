package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "appointments")
class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appointments_id_appointment_seq")
    @SequenceGenerator(name = "appointments_id_appointment_seq", sequenceName = "appointments_id_appointment_seq", allocationSize = 1)
    @Column(name = "id_appointment", nullable = false)
    var id: Int? = null

    @Size(max = 250)
    @Column(name = "reason", length = 250)
    var reason: String? = null

    @Size(max = 250)
    @Column(name = "diagnosis", length = 250)
    var diagnosis: String? = null

    @Size(max = 250)
    @Column(name = "recommendations", length = 250)
    var recommendations: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_record", nullable = false)
    var idRecord: Record? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_recipe")
    var idRecipe: Recipe? = null
}