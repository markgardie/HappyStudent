package com.example.happystudent.core.data.repository_impl

import android.content.ContentResolver
import android.net.Uri
import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.database.asEntity
import com.example.happystudent.core.database.asExternalModel
import com.example.happystudent.core.database.dao.StudentDao
import com.example.happystudent.core.database.model.StudentEntity
import com.example.happystudent.core.model.Student
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

class OfflineFirstStudentRepository @Inject constructor (
    private val studentDao: StudentDao,
    private val contentResolver: ContentResolver
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

    override suspend fun exportStudents(students: List<Student>, jsonUri: Uri) {
        val studentsJson = Json.encodeToString(students)
        try {

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override suspend fun importStudents(jsonUri: Uri) {
        TODO("Not yet implemented")
    }
}