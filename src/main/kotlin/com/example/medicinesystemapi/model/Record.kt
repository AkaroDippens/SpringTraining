package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(name = "records")
class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "records_id_record_seq")
    @SequenceGenerator(name = "records_id_record_seq", sequenceName = "records_id_record_seq", allocationSize = 1)
    @Column(name = "id_record", nullable = false)
    var id: Int? = null

    @NotNull
    @Column(name = "appointment_date", nullable = false)
    var appointmentDate: Instant? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    var idUser: User? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_building", nullable = false)
    var idBuilding: Building? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_doctor", nullable = false)
    var idDoctor: Doctor? = null
}