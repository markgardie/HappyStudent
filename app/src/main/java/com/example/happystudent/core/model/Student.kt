package com.example.happystudent.core.model

import com.example.happystudent.core.datastore.FilterPreferences

data class Student(
    val id: Int = UNDEFINED_ID,
    val name: String,
    val leaving_probability: Double = ZERO_PROB,
    val group: String = "",
    val update_date: String,
    var imageUri: String = "",
    val priority: FilterPreferences.Priority = FilterPreferences.Priority.UNDEFINED
) {

    companion object {

        const val UNDEFINED_ID = 0

        const val CRITICAL_PROB = 70.0
        const val IMPORTANT_PROB = 40.0
        const val ZERO_PROB = 0.0
    }
}
