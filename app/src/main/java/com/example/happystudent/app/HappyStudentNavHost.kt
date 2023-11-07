package com.example.happystudent.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.happystudent.feature.leaving_prob.SurveyViewModel
import com.example.happystudent.feature.leaving_prob.navigation.navigateToSurvey
import com.example.happystudent.feature.leaving_prob.navigation.surveyScreen
import com.example.happystudent.feature.students.StudentViewModel
import com.example.happystudent.feature.students.navigation.navigateToList
import com.example.happystudent.feature.students.navigation.navigateToUpsertStudent
import com.example.happystudent.feature.students.navigation.studentListRoute
import com.example.happystudent.feature.students.navigation.studentListScreen
import com.example.happystudent.feature.students.navigation.upsertStudentScreen

@Composable
fun HappyStudentNavHost() {

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
                navigateToUpsert = navController::navigateToUpsertStudent
            )

            upsertStudentScreen(
                viewModel = studentViewModel,
                navigateToList = navController::navigateToList,
                navigateToSurvey = navController::navigateToSurvey
            )

            surveyScreen(
                viewModel = surveyViewModel
            )

        }
    }
}