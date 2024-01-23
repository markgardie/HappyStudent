package com.example.happystudent.core.datastore

import kotlinx.coroutines.flow.Flow

interface FilterPreferencesRepository {

    val filterPreferencesFlow: Flow<FilterPreferences>

    suspend fun updateGroup(group: String)

    suspend fun updatePriority(priority: FilterPreferences.Priority)

    suspend fun updateFilterType(filter: FilterPreferences.FilterType)
}