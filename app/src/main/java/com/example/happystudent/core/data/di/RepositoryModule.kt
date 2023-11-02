package com.example.happystudent.core.data.di

import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.data.repository_impl.OfflineFirstStudentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

   @Binds
   fun bindStudentRepository(impl: OfflineFirstStudentRepository): StudentRepository

}