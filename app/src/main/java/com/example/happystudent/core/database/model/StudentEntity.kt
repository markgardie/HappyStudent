package com.example.happystudent.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.happystudent.core.model.Student

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val leaving_probability: Float,
    val group: String,
    val update_date: String,
    val imageUrl: String
)