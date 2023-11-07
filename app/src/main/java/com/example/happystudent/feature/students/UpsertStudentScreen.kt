package com.example.happystudent.feature.students

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happystudent.core.model.Student

@Composable
fun UpsertStudentScreen(
    viewModel: StudentViewModel,
    studentId: Int,
    navigateToList: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val student = findStudent(uiState, studentId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        var nameText by remember {
            mutableStateOf(student?.name ?: "")
        }

        var groupText by remember {
            mutableStateOf(student?.group ?: "")
        }

        TextField(
            modifier = Modifier
                .padding(vertical = 16.dp),
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text(text = "Name") }
        )

        TextField(
            value = groupText,
            onValueChange = { groupText = it },
            label = { Text(text = "Group") }
        )

        Button(
            modifier = Modifier
                .padding(vertical = 32.dp),
            onClick = {
                viewModel.upsertStudent(
                    Student(
                        id = studentId,
                        name = nameText,
                        group = groupText,
                        leaving_probability = 0.0,
                        update_date = "today",
                        imageUrl = ""
                    )
                )
                navigateToList()

            }) {
            Text(text = "Зберегти")
        }


    }

}

fun findStudent(uiState: StudentUiState, studentId: Int): Student? {

    val students = when (uiState) {
        is StudentUiState.Success -> uiState.students
        else -> emptyList()
    }

    for (student in students) {
        if (student.id == studentId) {
            return student
        }
    }

    return null
}