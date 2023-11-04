package com.example.happystudent.core.data.repository

import com.example.happystudent.core.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentRepository {

    fun getStudentStream(): Flow<List<Student>>

    suspend fun upsertStudent(student: Student)

    suspend fun deleteStudent(studentId: Int)
}