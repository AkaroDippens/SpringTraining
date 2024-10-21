package com.example.uchebpracticaspring.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "grades")
open class GradeModel @JvmOverloads constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @NotBlank(message = "Grade is mandatory")
    @Size(max = 255, message = "Grade should not exceed 255 characters")
    var gradeContent: String? = null,

    @OneToOne
    @JoinColumn(name = "student_id")
    var student: StudentModel? = null,

    var isDeleted: Boolean = false
)