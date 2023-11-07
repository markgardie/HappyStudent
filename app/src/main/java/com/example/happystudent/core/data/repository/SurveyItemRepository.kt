package com.example.happystudent.core.data.repository

import com.example.happystudent.core.model.SurveyItem
import kotlinx.coroutines.flow.Flow

interface SurveyItemRepository {

    fun getSurveyItemsStream(): Flow<List<SurveyItem>>
}