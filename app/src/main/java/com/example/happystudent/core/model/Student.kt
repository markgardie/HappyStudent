package com.example.happystudent.core.model

data class Student(
    val id: Int = UNDEFINED_ID,
    val name: String,
    val leaving_probability: Float,
    val group: String,
    val update_date: String,
    val imageUrl: String
) {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
