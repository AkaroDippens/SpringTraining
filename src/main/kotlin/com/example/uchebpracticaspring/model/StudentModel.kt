package com.example.uchebpracticaspring.model

open class StudentModel(
    var id: Int,
    var name: String?,
    var lastName: String?,
    var firstName: String?,
    var middleName: String?,
    var isDeleted: Boolean = false,
    var averageGrade: String?,
    var course: String?,
    var university: String?,
)
