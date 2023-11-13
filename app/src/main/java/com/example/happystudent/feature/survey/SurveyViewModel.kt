package com.example.happystudent.feature.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happystudent.core.data.di.TestRepository
import com.example.happystudent.core.data.repository.SurveyItemRepository
import com.example.happystudent.core.model.SurveyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    @TestRepository
    private val repository: SurveyItemRepository
) : ViewModel() {

    val uiState: StateFlow<SurveyUiState> = repository.getSurveyItemsStream()
        .map {
            if (it.isEmpty()) SurveyUiState.Empty
            else SurveyUiState.Success(it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SurveyUiState.Empty
        )

    suspend fun calculateProbability(answersIds: List<Int>): Double {


        var points = 0.0
        var maxPoints = 0.0

        val surveyItems = repository.getSurveyItems()

        surveyItems.forEach { item ->
            answersIds.forEach { id ->
                maxPoints += (item.answers.size - 1) * item.weight
                points += id * item.weight
            }
        }

        return points / maxPoints
    }

}

sealed interface SurveyUiState {

    data class Success(val surveyItems: List<SurveyItem>) : SurveyUiState

    object Empty : SurveyUiState

}