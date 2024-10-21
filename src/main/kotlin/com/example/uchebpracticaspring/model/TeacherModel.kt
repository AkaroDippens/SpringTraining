package com.example.uchebpracticaspring.model

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "teachers")
open class TeacherModel @JvmOverloads constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @NotBlank(message = "Name is mandatory")
    var name: String? = null,

    @NotBlank(message = "Last name is mandatory")
    var lastName: String? = null,

    var isDeleted: Boolean = false,

    @NotBlank(message = "Subject is mandatory")
    var subject: String? = null,

    @Min(value = 18, message = "Age should not be less than 18")
    var age: Int? = null,

    @Min(value = 0, message = "Stage should not be less than 0")
    var stage: Int? = null,

    @ManyToOne
    @JoinColumn(name = "university_id")
    var university: UniversityModel? = null,

    @ManyToMany
    @JoinTable(
        name = "teacher_subject",
        joinColumns = [JoinColumn(name = "teacher_id")],
        inverseJoinColumns = [JoinColumn(name = "subject_id")]
    )
    var subjects: MutableList<SubjectModel> = mutableListOf(),
)