package com.example.happystudent.core.model

data class Student(
    val id: Int = UNDEFINED_ID,
    val name: String,
    val leaving_probability: Double = 0.0,
    val group: String = "",
    val update_date: String,
    var imageUri: String = "",
    val priority: String = ""
) {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
