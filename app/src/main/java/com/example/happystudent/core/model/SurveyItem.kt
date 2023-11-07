package com.example.happystudent.core.model

data class SurveyItem(
    val id: Int = UNDEFINED_ID,
    val question: String,
    val answers: List<String>,
    val weight: Double
) {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
