package com.example.happystudent.feature.students.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.happystudent.feature.students.StudentListScreen
import com.example.happystudent.feature.students.StudentViewModel

const val studentListRoute = "student_list"

fun NavController.navigateToList() {
    this.navigate(studentListRoute)
}

fun NavGraphBuilder.studentListScreen(
    viewModel: StudentViewModel,
    navigateToUpsert: (Int) -> Unit
) {
    composable(
        route = studentListRoute
    ) {

        StudentListScreen(
            viewModel = viewModel,
            navigateToUpsert = navigateToUpsert
        )
    }


}