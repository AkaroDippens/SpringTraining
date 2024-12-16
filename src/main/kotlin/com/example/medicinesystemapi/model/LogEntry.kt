package com.example.medicinesystemapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "logs")
class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "timestamp")
    var timestamp: LocalDateTime = LocalDateTime.now()

    @Column(name = "method")
    var method: String? = null

    @Column(name = "url")
    var url: String? = null

    @Column(name = "status")
    var status: Int? = null

    @Column(name = "user_agent")
    var userAgent: String? = null

    @Column(name = "ip_address")
    var ipAddress: String? = null
}