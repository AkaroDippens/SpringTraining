package com.example.medicinesystemapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(name = "analyzes")
class Analyze {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analyzes_id_analyze_seq")
    @SequenceGenerator(name = "analyzes_id_analyze_seq", sequenceName = "analyzes_id_analyze_seq", allocationSize = 1)
    @Column(name = "id_analyze", nullable = false)
    var id: Int? = null

    @Column(name = "analyze_result", length = Integer.MAX_VALUE)
    var analyzeResult: String? = null

    @Column(name = "receive_time")
    var receiveTime: Instant? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_doctor", nullable = false)
    var idDoctor: Doctor? = null

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_appointment", nullable = false)
    var idAppointment: Appointment? = null
}