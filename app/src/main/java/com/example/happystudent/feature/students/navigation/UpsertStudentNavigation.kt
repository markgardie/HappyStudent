package com.example.happystudent.feature.students.navigation

import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.happystudent.feature.students.StudentViewModel
import com.example.happystudent.feature.students.UpsertStudentScreen

const val STUDENT_ID_ARG = "student_id"
const val upsertStudentRoute = "upsert_student/{$STUDENT_ID_ARG}"

const val DEFAULT_STUDENT_ID = 0

fun NavController.navigateToUpsertStudent(studentId: Int) {
    this.navigate("upsert_student/$studentId")
}

fun NavGraphBuilder.upsertStudentScreen(
    viewModel: StudentViewModel,
    navigateToList: () -> Unit
) {

    composable(
        route = upsertStudentRoute,
        arguments = listOf(
            navArgument(STUDENT_ID_ARG) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val studentId = backStackEntry.arguments?.getInt(STUDENT_ID_ARG)

        UpsertStudentScreen(
            viewModel = viewModel,
            navigateToList = navigateToList,
            studentId = studentId ?: DEFAULT_STUDENT_ID
        )
    }
}