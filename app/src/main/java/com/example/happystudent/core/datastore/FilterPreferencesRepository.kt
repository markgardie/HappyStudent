package com.example.happystudent.core.datastore

import kotlinx.coroutines.flow.Flow

interface FilterPreferencesRepository {

    val filterPreferencesFlow: Flow<FilterPreferences>

    suspend fun updateFilter(filter: String)

    suspend fun updateFilterType(filter: FilterPreferences.FilterType)
}