package com.example.happystudent.core.data.repository_impl

import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.database.asEntity
import com.example.happystudent.core.database.asExternalModel
import com.example.happystudent.core.database.dao.StudentDao
import com.example.happystudent.core.database.model.StudentEntity
import com.example.happystudent.core.model.Student
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstStudentRepository @Inject constructor (
    private val studentDao: StudentDao
): StudentRepository {

    override fun getStudentStream() =
        studentDao.getStudentStream().map {
            it.map(StudentEntity::asExternalModel)
        }

    override suspend fun upsertStudent(student: Student) {
        studentDao.upsertStudent(student.asEntity())
    }

    override suspend fun deleteStudent(studentId: Int) {
        studentDao.deleteStudent(studentId)
    }
}