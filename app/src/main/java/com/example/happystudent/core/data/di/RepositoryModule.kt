package com.example.happystudent.core.data.di

import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.data.repository.SurveyItemRepository
import com.example.happystudent.core.data.repository_impl.OfflineFirstStudentRepository
import com.example.happystudent.core.data.repository_impl.TestSurveyItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineFirstRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

   @OfflineFirstRepository
   @Binds
   abstract fun bindOfflineStudentRepository(impl: OfflineFirstStudentRepository): StudentRepository

   @TestRepository
   @Binds
   abstract fun bindTestSurveyItemRepository(impl: TestSurveyItemRepository): SurveyItemRepository

}