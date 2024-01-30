package com.example.happystudent.core.datastore

import androidx.datastore.core.DataStore
import com.example.happystudent.core.datastore.FilterPreferences.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject

class DefaultFilterPreferencesRepository @Inject constructor(
    private val filterPreferencesStore: DataStore<FilterPreferences>
): FilterPreferencesRepository {


    override val filterPreferencesFlow: Flow<FilterPreferences> = filterPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(FilterPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }


    override suspend fun updateGroup(group: String) {
        filterPreferencesStore.updateData { preferences ->
            preferences
                .toBuilder()
                .setGroup(group)
                .build()
        }
    }

    override suspend fun updatePriority(priority: FilterPreferences.Priority) {
        filterPreferencesStore.updateData { preferences ->
            preferences
                .toBuilder()
                .setPriority(priority)
                .build()
        }
    }

    override suspend fun updateFilterType(filter: FilterType) {
        filterPreferencesStore.updateData { preferences ->
            preferences
                .toBuilder()
                .setFilterType(filter)
                .build()
        }
    }

}