package com.example.happystudent.core.database

import com.example.happystudent.core.database.model.StudentEntity
import com.example.happystudent.core.model.Student


fun StudentEntity.asExternalModel() = Student(
    id = id,
    name = name,
    leaving_probability = leaving_probability,
    group = group,
    update_date = update_date,
    imageUri = imageUri,
    priority = priority
)

fun Student.asEntity() = StudentEntity(
    id = id,
    name = name,
    leaving_probability = leaving_probability,
    group = group,
    update_date = update_date,
    imageUri = imageUri,
    priority = priority
)

