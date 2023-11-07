package com.example.happystudent.feature.leaving_prob

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SurveyScreen(
    viewModel: SurveyViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when(uiState) {
        is SurveyUiState.Empty -> EmptyState()
        is SurveyUiState.Success -> Survey()
    }

}

@Composable
fun Survey() {

}

@Composable
fun EmptyState() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Немає питань")

    }
}