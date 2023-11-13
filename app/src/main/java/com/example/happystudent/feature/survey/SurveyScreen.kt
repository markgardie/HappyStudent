package com.example.happystudent.feature.survey

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happystudent.core.model.SurveyItem
import kotlinx.coroutines.launch

@Composable
fun SurveyScreen(
    viewModel: SurveyViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is SurveyUiState.Empty -> EmptyState()
        is SurveyUiState.Success -> Survey(
            surveyItems = (uiState as SurveyUiState.Success).surveyItems,
            calculateProbability = viewModel::calculateProbability
        )
    }

}

@Composable
fun Survey(
    surveyItems: List<SurveyItem>,
    calculateProbability: suspend (List<Int>) -> Double
) {


    var itemIndex by remember {
        mutableIntStateOf(0)
    }

    var showBackButton by remember {
        mutableStateOf(false)
    }

    var surveyProgress by remember {
        mutableFloatStateOf(0.0f)
    }

    var nextText by remember {
        mutableStateOf("Далі")
    }

    val (selectedAnswer, onAnswerSelect) = remember {
        mutableIntStateOf(-1)
    }

    val answersIds by remember {
        mutableStateOf(mutableListOf<Int>())
    }

    val scope = rememberCoroutineScope()

    var probability by remember {
        mutableDoubleStateOf(0.0)
    }


    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            progress = surveyProgress,
            color = MaterialTheme.colorScheme.tertiary
        )
    }

    SurveyItemComponent(
        surveyItem = surveyItems[itemIndex],
        selectedAnswer = selectedAnswer,
        onAnswerSelect = onAnswerSelect
    )

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {

        Row {

            if (showBackButton) {
                TextButton(onClick = {
                    itemIndex--
                    showBackButton = itemIndex != 0
                    surveyProgress = itemIndex / surveyItems.size.toFloat()
                    nextText = "Далі"
                    answersIds.removeLast()
                    onAnswerSelect(-1)
                }) {
                    Text(
                        text = "Назад",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }


            TextButton(onClick = {
                if (itemIndex < surveyItems.lastIndex - 1) {
                    itemIndex++
                }
                else if (itemIndex == surveyItems.lastIndex - 1) {
                    itemIndex++
                    nextText = "Завершити"
                }
                else {
                    scope.launch {
                        probability = calculateProbability(answersIds)
                        Log.d("Survey Screen", "$probability")
                    }
                }

                answersIds.add(selectedAnswer)
                onAnswerSelect(-1)
                showBackButton = itemIndex != 0
                surveyProgress = itemIndex / surveyItems.size.toFloat()

            }) {
                Text(
                    text = nextText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }


        }

    }
}


@Composable
fun SurveyItemComponent(
    surveyItem: SurveyItem,
    selectedAnswer: Int,
    onAnswerSelect: (Int) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = surveyItem.question,
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(vertical = 36.dp)
        ) {

            surveyItem.answers.forEachIndexed { answerIndex, _ ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (answerIndex == selectedAnswer),
                            onClick = { onAnswerSelect(answerIndex) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (answerIndex == selectedAnswer),
                        onClick = null // null recommended for accessibility with screenreaders
                    )

                    Text(
                        text = surveyItem.answers[answerIndex],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                }
            }


        }

    }


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