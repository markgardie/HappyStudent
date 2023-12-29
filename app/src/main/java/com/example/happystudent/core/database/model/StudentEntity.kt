package com.example.happystudent.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val leaving_probability: Double,
    val group: String,
    val update_date: String,
    val imageUri: String,
    val priority: String
)
