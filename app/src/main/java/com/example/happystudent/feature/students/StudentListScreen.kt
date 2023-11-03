package com.example.happystudent.feature.students

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults.colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happystudent.core.model.Student


@Composable
fun StudentListScreen(
    viewModel: StudentViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is StudentUiState.Empty -> EmptyState()
        is StudentUiState.Loading -> LoadingState()
        is StudentUiState.Success -> StudentList(
            students = (uiState as StudentUiState.Success).students
        )
    }

}

@Composable
fun StudentList(
    students: List<Student>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        items(
            items = students,
            key = { student ->
                student.id
            }
        ) {
            StudentCard(it)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCard(
    student: Student
) {


    ListItem(
        headlineText = {
            Text(text = student.name)
        },
        supportingText = {

            Text(text = student.group)

        },
        leadingContent = {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Student photo"
            )
        },
        trailingContent = {
            Column {
                Text(text = "${student.leaving_probability * 100}%")
                Text(text = student.update_date)
            }
        }
    )


}

@Composable
fun LoadingState() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }

}

@Composable
fun EmptyState() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "No students")

    }

}