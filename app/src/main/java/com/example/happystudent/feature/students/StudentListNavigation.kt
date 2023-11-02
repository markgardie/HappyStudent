package com.example.happystudent.feature.students

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val studentListRoute = "student_list"

fun NavGraphBuilder.studentListScreen(
    viewModel: StudentViewModel
) {
    composable(
        route = studentListRoute
    ) {

        StudentListRoute(viewModel)
    }


}