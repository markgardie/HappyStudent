package com.example.happystudent.feature.students.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.happystudent.core.model.Student.Companion.UNDEFINED_ID
import com.example.happystudent.core.model.Student.Companion.ZERO_PROB
import com.example.happystudent.feature.students.StudentViewModel
import com.example.happystudent.feature.students.UpsertStudentScreen

const val STUDENT_ID_ARG = "student_id"
const val upsertStudentRoute = "upsert_student/{$STUDENT_ID_ARG}"
const val LEAVING_PROB_KEY = "leaving_prob_key"

fun NavController.navigateToUpsertStudent(studentId: Int) {
    this.navigate("upsert_student/$studentId")
}

fun NavController.navigateBackToUpsert(probability: Double) {
    this.popBackStack()
    this.currentBackStackEntry
        ?.savedStateHandle
        ?.set(LEAVING_PROB_KEY, probability)
}

fun NavGraphBuilder.upsertStudentScreen(
    viewModel: StudentViewModel,
    navigateToSurvey: () -> Unit,
    navigateBackToList: () -> Unit
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
        val evaluatedProb = backStackEntry.savedStateHandle.get<Double>(LEAVING_PROB_KEY)

        UpsertStudentScreen(
            viewModel = viewModel,
            studentId = studentId ?: UNDEFINED_ID,
            evaluatedProb = evaluatedProb ?: ZERO_PROB,
            navigateToSurvey = navigateToSurvey,
            navigateBackToList = navigateBackToList
        )
    }
}