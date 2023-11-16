package com.example.happystudent.feature.survey.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.happystudent.feature.survey.SurveyScreen
import com.example.happystudent.feature.survey.SurveyViewModel


const val surveyRoute = "survey"

fun NavController.navigateToSurvey() {
    this.navigate(surveyRoute)
}

fun NavGraphBuilder.surveyScreen (
    viewModel: SurveyViewModel,
    navigateBackToUpsert: (Double) -> Unit
) {

    composable(
        route = surveyRoute
    ) {

        SurveyScreen(
            viewModel = viewModel,
            navigateBackToUpsert = navigateBackToUpsert
        )
    }
}