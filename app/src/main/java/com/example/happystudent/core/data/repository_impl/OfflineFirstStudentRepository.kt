package com.example.happystudent.core.data.repository_impl

import android.net.Uri
import android.os.Environment
import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.database.asEntity
import com.example.happystudent.core.database.asExternalModel
import com.example.happystudent.core.database.dao.StudentDao
import com.example.happystudent.core.database.model.StudentEntity
import com.example.happystudent.core.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class OfflineFirstStudentRepository @Inject constructor(
    private val studentDao: StudentDao
) : StudentRepository {


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

    override suspend fun exportStudents(students: List<Student>) {
        val studentsJson = Json.encodeToString(students)

        val downloadFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val timestamp = SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault())
            .format(Calendar.getInstance().time)


        val file = File("${downloadFolder?.path}/$timestamp.json")

        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(studentsJson.toByteArray())
            }
        }
    }

    override suspend fun importStudents(jsonUri: Uri) {

        withContext(Dispatchers.IO) {
            FileInputStream(jsonUri.toString()).use { inputStream ->
                val studentsJson = inputStream.readBytes().toString()
                val students = Json.decodeFromString<List<Student>>(studentsJson)
                students.forEach { student ->
                    studentDao.upsertStudent(student.asEntity())
                }
            }

        }


    }

}