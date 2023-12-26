package com.example.happystudent.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.happystudent.core.datastore.FilterPreferences
import com.example.happystudent.core.datastore.FilterPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val FILTER_PREFERENCES_NAME = "filter_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideFilterPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<FilterPreferences> = DataStoreFactory
        .create(
            serializer = FilterPreferencesSerializer,
            produceFile = { context.dataStoreFile(FILTER_PREFERENCES_NAME) },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
}