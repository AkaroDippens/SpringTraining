package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "medical_records")
class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medical_records_id_medical_record_seq")
    @SequenceGenerator(name = "medical_records_id_medical_record_seq", sequenceName = "medical_records_id_medical_record_seq", allocationSize = 1)
    @Column(name = "id_medical_record", nullable = false)
    var id: Int? = null

    @Column(name = "user_information", length = Integer.MAX_VALUE)
    var userInformation: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_user", nullable = false)
    var idUser: User? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_appointment")
    var idAppointment: Appointment? = null
}