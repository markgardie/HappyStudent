package com.example.happystudent.feature.leaving_prob.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.happystudent.feature.leaving_prob.SurveyScreen
import com.example.happystudent.feature.leaving_prob.SurveyViewModel

const val surveyRoute = "survey"

fun NavController.navigateToSurvey() {
    this.navigate(surveyRoute)
}

fun NavGraphBuilder.surveyScreen (
    viewModel: SurveyViewModel
) {

    composable(
        route = surveyRoute
    ) {

        SurveyScreen(viewModel = viewModel)
    }
}