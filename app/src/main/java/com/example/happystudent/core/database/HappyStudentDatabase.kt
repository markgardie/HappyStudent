package com.example.happystudent.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.happystudent.core.database.dao.StudentDao
import com.example.happystudent.core.database.model.StudentEntity

@Database(
    entities = [
        StudentEntity::class
               ],
    version = 1,
    exportSchema = false
)
abstract class HappyStudentDatabase: RoomDatabase() {

    abstract fun studentDao(): StudentDao
}