package com.example.happystudent.feature.students.util

import com.example.happystudent.core.model.Student


fun List<Student>.formatList(): String {
    var shareText = ""

    this.forEach { student ->
        shareText += "${student.name}, ${student.group}: ${student.leaving_probability} \n"
    }
    return shareText
}