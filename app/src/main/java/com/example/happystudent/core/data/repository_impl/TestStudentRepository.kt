package com.example.happystudent.core.data.repository_impl

import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TestStudentRepository @Inject constructor(): StudentRepository {

    private var students = mutableListOf(
        Student(
            id = 1,
            name = "Sasha",
            leaving_probability = 0.5,
            group = "sat-10-00",
            update_date = "03.11.2023",
            imageUrl = ""
        ),

        Student(
            id = 2,
            name = "Masha",
            leaving_probability = 0.7,
            group = "sat-10-00",
            update_date = "03.11.2023",
            imageUrl = ""
        ),

        Student(
            id = 3,
            name = "Misha",
            leaving_probability = 0.1,
            group = "sun-12-00",
            update_date = "28.10.2023",
            imageUrl = ""
        ),

        Student(
            id = 4,
            name = "Oleh",
            leaving_probability = 0.2,
            group = "sun-12-00",
            update_date = "28.10.2023",
            imageUrl = ""
        )
    )

    override fun getStudentStream(): Flow<List<Student>> =
        flow {
            emit(students)
        }

    override suspend fun upsertStudent(student: Student) {
        TODO()
    }

    override suspend fun deleteStudent(studentId: Int) {
        TODO()
    }
}