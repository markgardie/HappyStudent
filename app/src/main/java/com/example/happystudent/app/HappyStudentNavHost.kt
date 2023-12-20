package com.example.happystudent.app

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.happystudent.feature.students.StudentViewModel
import com.example.happystudent.feature.students.navigation.navigateBackToList
import com.example.happystudent.feature.students.navigation.navigateBackToUpsert
import com.example.happystudent.feature.students.navigation.navigateToList
import com.example.happystudent.feature.students.navigation.navigateToUpsertStudent
import com.example.happystudent.feature.students.navigation.studentListRoute
import com.example.happystudent.feature.students.navigation.studentListScreen
import com.example.happystudent.feature.students.navigation.upsertStudentScreen
import com.example.happystudent.feature.survey.SurveyViewModel
import com.example.happystudent.feature.survey.navigation.navigateToSurvey
import com.example.happystudent.feature.survey.navigation.surveyScreen

@Composable
fun HappyStudentNavHost(
    context: Context
) {

    val navController = rememberNavController()
    val studentViewModel: StudentViewModel = hiltViewModel()
    val surveyViewModel: SurveyViewModel = hiltViewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        NavHost(
            navController = navController,
            startDestination = studentListRoute
        ) {

            studentListScreen(
                viewModel = studentViewModel,
                navigateToUpsert = navController::navigateToUpsertStudent,
                context = context
            )

            upsertStudentScreen(
                viewModel = studentViewModel,
                navigateToList = navController::navigateToList,
                navigateToSurvey = navController::navigateToSurvey,
                navigateBackToList = navController::navigateBackToList,
                context = context
            )

            surveyScreen(
                viewModel = surveyViewModel,
                navigateBackToUpsert = navController::navigateBackToUpsert
            )

        }
    }
}