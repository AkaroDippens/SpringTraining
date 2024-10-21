package com.example.uchebpracticaspring.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "students")
open class StudentModel @JvmOverloads constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @NotBlank(message = "Имя обязательно!")
    open var name: String? = null,

    @NotBlank(message = "Фамилия обязательно!")
    var lastName: String? = null,

    @NotBlank(message = "Отчество обязательно!")
    var firstName: String? = null,

    var middleName: String? = null,

    var isDeleted: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "university_id")
    var university: UniversityModel? = null,

    @OneToOne(mappedBy = "student", cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinColumn(name = "grade_id")
    var grade: GradeModel? = null
)