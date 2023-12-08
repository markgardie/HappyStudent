package com.example.happystudent.core.database.model

import com.example.happystudent.core.model.Student

data class StudentGroup(
    val name: String,
    val students: List<Student>
)