package com.example.happystudent.feature.students.model

import com.example.happystudent.core.model.Student

data class ExpandableStudentGroup(
    val name: String,
    val students: List<Student>
)