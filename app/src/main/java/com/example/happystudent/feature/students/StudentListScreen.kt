package com.example.happystudent.feature.students

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happystudent.core.model.Student
import com.example.happystudent.feature.students.navigation.DEFAULT_STUDENT_ID
import kotlinx.coroutines.delay


@Composable
fun StudentListScreen(
    viewModel: StudentViewModel,
    navigateToUpsert: (Int) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is StudentUiState.Empty -> EmptyState()
        is StudentUiState.Loading -> LoadingState()
        is StudentUiState.Success -> StudentList(
            students = (uiState as StudentUiState.Success).students,
            deleteStudent = viewModel::deleteStudent
        )
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            modifier = Modifier.padding(16.dp),
            onClick = { navigateToUpsert(DEFAULT_STUDENT_ID) }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new student")
        }
    }

}

@Composable
fun StudentList(
    students: List<Student>,
    deleteStudent: (Int) -> Unit
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
            StudentDismissItem(
                student = it,
                deleteStudent = deleteStudent
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDismissItem(
    student: Student,
    deleteStudent: (Int) -> Unit
) {

    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(student)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToEnd) {
                show = false
                true
            } else false
        }, positionalThreshold = { 150.dp.toPx()  }
    )

    AnimatedVisibility(
        show,exit = fadeOut(spring())
    ) {
        SwipeToDismiss(
            state = dismissState,
            background = {
                DismissBackground()
            },
            dismissContent = {
                StudentCard(student = student)
            },
            directions = setOf(
                DismissDirection.StartToEnd
            )
        )
    }

    LaunchedEffect(show) {
        if (!show) {
            delay(800)
            deleteStudent(currentItem.id)
        }
    }

}

@Composable
fun DismissBackground() {

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            Icons.Default.Delete,
            contentDescription = "delete"
        )

    }
}

@Composable
fun StudentCard(
    student: Student
) {


    ListItem(
        headlineContent = {
            Text(text = student.name)
        },
        supportingContent = {

            Text(text = student.group)

        },
        leadingContent = {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Фото студента"
            )
        },
        trailingContent = {
            Column {
                Text(text = "${student.leaving_probability}%")
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