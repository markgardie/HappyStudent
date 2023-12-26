package com.example.happystudent.core.database.di

import com.example.happystudent.core.database.HappyStudentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

   @Provides
   fun provideStudentDao(db: HappyStudentDatabase) =
       db.studentDao()
}