package com.example.uchebpracticaspring.model

open class TeacherModel(
    var id: Int,
    var name: String?,
    var lastName: String?,
    var isDeleted: Boolean = false,
    var subject: String?,
    var age: Int?,
    var stage: Int?
)