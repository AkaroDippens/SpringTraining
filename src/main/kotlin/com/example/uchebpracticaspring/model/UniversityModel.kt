package com.example.uchebpracticaspring.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "universities")
open class UniversityModel @JvmOverloads constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @NotBlank(message = "Name is mandatory")
    var name: String? = null,

    var isDeleted: Boolean = false,

    @OneToMany(mappedBy = "university", cascade = [CascadeType.ALL])
    var students: MutableList<StudentModel> = mutableListOf(),

    @OneToMany(mappedBy = "university", cascade = [CascadeType.ALL])
    var teachers: MutableList<TeacherModel> = mutableListOf()
)