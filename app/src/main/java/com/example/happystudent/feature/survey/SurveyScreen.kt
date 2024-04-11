package com.example.happystudent.feature.survey

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happystudent.R
import com.example.happystudent.core.model.SurveyItem
import com.example.happystudent.core.theme.padding
import kotlinx.coroutines.launch

@Composable
fun SurveyScreen(
    viewModel: SurveyViewModel,
    navigateBackToUpsert: (Double) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is SurveyUiState.Empty -> EmptyState()
        is SurveyUiState.Success -> Survey(
            surveyItems = (uiState as SurveyUiState.Success).surveyItems,
            calculateProbability = viewModel::calculateProbability,
            navigateBackToUpsert = navigateBackToUpsert
        )
    }

}

@Composable
fun Survey(
    surveyItems: List<SurveyItem>,
    calculateProbability: suspend (List<Int>) -> Double,
    navigateBackToUpsert: (Double) -> Unit
) {

    val nextStringRes = stringResource(R.string.next)
    val completeStringRes = stringResource(R.string.complete)

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
        mutableStateOf(nextStringRes)
    }

    val (selectedAnswer, onAnswerSelect) = remember {
        mutableIntStateOf(-1)
    }

    val answersIds by remember {
        mutableStateOf(mutableListOf<Int>())
    }

    val scope = rememberCoroutineScope()

    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.padding.small),
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
                    nextText = nextStringRes
                    answersIds.removeLast()
                    onAnswerSelect(-1)
                }) {
                    Text(
                        text = stringResource(R.string.previous),
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
                    nextText = completeStringRes
                }
                else {
                    scope.launch {
                        navigateBackToUpsert(calculateProbability(answersIds))
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
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(vertical = MaterialTheme.padding.large)
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
                        .padding(horizontal = MaterialTheme.padding.large),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (answerIndex == selectedAnswer),
                        onClick = null // null recommended for accessibility with screenreaders
                    )

                    Text(
                        text = surveyItem.answers[answerIndex],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = MaterialTheme.padding.medium)
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