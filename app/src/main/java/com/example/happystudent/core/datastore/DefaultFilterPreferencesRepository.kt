package com.example.happystudent.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.happystudent.core.datastore.FilterPreferences.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject

class DefaultFilterPreferencesRepository @Inject constructor(
    private val filterPreferencesStore: DataStore<FilterPreferences>
): FilterPreferencesRepository {

    private val TAG: String = "FilterPreferencesRepo"

    override val filterPreferencesFlow: Flow<FilterPreferences> = filterPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(FilterPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun updateFilter(filter: String) {
        filterPreferencesStore.updateData { preferences ->
            preferences
                .toBuilder()
                .setFilter(filter)
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