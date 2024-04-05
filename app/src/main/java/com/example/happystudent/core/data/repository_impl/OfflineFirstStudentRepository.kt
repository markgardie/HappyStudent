package com.example.happystudent.core.data.repository_impl

import android.content.Context
import android.net.Uri
import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.database.asEntity
import com.example.happystudent.core.database.asExternalModel
import com.example.happystudent.core.database.dao.StudentDao
import com.example.happystudent.core.database.model.StudentEntity
import com.example.happystudent.core.model.Student
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject


const val SUCCESS_EXPORT_IMPORT = 1
const val FAILED_EXPORT_IMPORT = 0

class OfflineFirstStudentRepository @Inject constructor(
    private val studentDao: StudentDao,
    @ApplicationContext private val context: Context
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

    override suspend fun exportStudents(students: List<Student>, uri: Uri): Int {
        val studentsJson = Json.encodeToString(students)

        return try {
            context.contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
                FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                    outputStream.write(studentsJson.toByteArray())
                }
            }

            SUCCESS_EXPORT_IMPORT
        } catch(e: Exception) {
            FAILED_EXPORT_IMPORT
        }

    }

    override suspend fun importStudents(uri: Uri): Int {

        val stringBuilder = StringBuilder()

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
            val studentsJson = stringBuilder.toString()
            Json.decodeFromString<List<Student>>(studentsJson).forEach {
                studentDao.upsertStudent(it.asEntity())
            }
            SUCCESS_EXPORT_IMPORT
        } catch (e: Exception) {
            FAILED_EXPORT_IMPORT
        }

    }

}