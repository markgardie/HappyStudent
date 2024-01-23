package com.example.happystudent.core.data.repository_impl

import com.example.happystudent.core.data.repository.SurveyItemRepository
import com.example.happystudent.core.model.SurveyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import android.content.Context
import com.example.happystudent.R
import dagger.hilt.android.qualifiers.ApplicationContext

class TestSurveyItemRepository @Inject constructor(
    @ApplicationContext context: Context
): SurveyItemRepository {

    private val surveyItems = listOf(

        SurveyItem(
        id = 5,
        question = context.getString(R.string.interest),
        answers = listOf(
            context.getString(R.string.interest_ans1),
            context.getString(R.string.interets_ans2),
            context.getString(R.string.interest_ans3),
            context.getString(R.string.interest_ans4)
        ),
        weight = 0.25
         ),

        SurveyItem(
            id = 7,
            question = context.getString(R.string.duration),
            answers = listOf(
                context.getString(R.string.duration_ans1),
                context.getString(R.string.duration_ans2),
                context.getString(R.string.duration_ans3),
                context.getString(R.string.duration_ans4),
                context.getString(R.string.duration_ans5)
            ),
            weight = 0.20
        ),

        SurveyItem(
            id = 8,
            question = context.getString(R.string.teacher),
            answers = listOf(
                context.getString(R.string.duration_ans1),
                context.getString(R.string.duration_ans2),
                context.getString(R.string.duration_ans3),
                context.getString(R.string.duration_ans4),
                context.getString(R.string.duration_ans5)
            ),
            weight = 0.18
        ),

        SurveyItem(
            id = 4,
            question = context.getString(R.string.mood),
            answers = listOf(
                context.getString(R.string.mood_ans1),
                context.getString(R.string.mood_ans2),
                context.getString(R.string.mood_ans3),
                context.getString(R.string.mood_ans4)
            ),
            weight = 0.14
        ),

        SurveyItem(
            id = 3,
            question = context.getString(R.string.behavior),
            answers = listOf(
                context.getString(R.string.behavior_ans1),
                context.getString(R.string.behavior_ans2),
                context.getString(R.string.behavior_ans3)
            ),
            weight = 0.11
        ),

        SurveyItem(
            id = 2,
            question = context.getString(R.string.classwork),
            answers = listOf(
                context.getString(R.string.classwork_ans1),
                context.getString(R.string.classwork_ans2),
                context.getString(R.string.classwork_ans3),
                context.getString(R.string.classwork_ans4)
            ),
            weight = 0.07
        ),

        SurveyItem(
            id = 1,
            question = context.getString(R.string.homework),
            answers = listOf(
                context.getString(R.string.homework_ans1),
                context.getString(R.string.homework_ans2),
                context.getString(R.string.homework_ans3),
                context.getString(R.string.homework_ans4)
            ),
            weight = 0.04
        ),

        SurveyItem(
            id = 6,
            question = context.getString(R.string.relationship),
            answers = listOf(
                context.getString(R.string.relationship_ans1),
                context.getString(R.string.relationship_ans2),
                context.getString(R.string.relationship_ans3),
                context.getString(R.string.relationship_ans4),
                context.getString(R.string.relationship_ans5)
            ),
            weight = 0.01
        )
    )

    override fun getSurveyItemsStream(): Flow<List<SurveyItem>> =
        flow {
            emit(surveyItems)
        }

    override suspend fun getSurveyItems(): List<SurveyItem> = surveyItems
}