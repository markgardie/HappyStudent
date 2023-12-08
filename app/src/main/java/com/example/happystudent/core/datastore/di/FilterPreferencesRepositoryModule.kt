package com.example.happystudent.core.datastore.di

import com.example.happystudent.core.datastore.DefaultFilterPreferencesRepository
import com.example.happystudent.core.datastore.FilterPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface FilterPreferencesRepositoryModule {

    @Singleton
    @Binds
    fun bindFilterPreferenceRepository(impl: DefaultFilterPreferencesRepository)
            : FilterPreferencesRepository

}