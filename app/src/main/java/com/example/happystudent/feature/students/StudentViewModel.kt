package com.example.happystudent.feature.students

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happystudent.core.data.di.OfflineFirstRepository
import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.datastore.DefaultFilterPreferencesRepository
import com.example.happystudent.core.datastore.FilterPreferences.FilterType
import com.example.happystudent.core.datastore.FilterPreferences.Priority
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.model.Student.Companion.CRITICAL_PROB
import com.example.happystudent.core.model.Student.Companion.IMPORTANT_PROB
import com.example.happystudent.core.model.Student.Companion.ZERO_PROB
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    @OfflineFirstRepository private val repository: StudentRepository,
    private val filterPreferencesRepository: DefaultFilterPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<StudentUiState> = combine(
        repository.getStudentStream(),
        filterPreferencesRepository.filterPreferencesFlow
    ) { students, preferences ->
        if (students.isEmpty()) StudentUiState.Empty
        else StudentUiState.Success(
            students = students,
            preferenceGroup = preferences.group,
            preferencePriority = preferences.priority,
            filterType = preferences.filterType
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            StudentUiState.Loading
        )


    fun upsertStudent(student: Student) {

        val priority =
            if (student.leaving_probability <= ZERO_PROB) Priority.ZERO
            else if (student.leaving_probability > CRITICAL_PROB) Priority.FIRST
            else if (student.leaving_probability > IMPORTANT_PROB) Priority.SECOND
            else Priority.THIRD

        viewModelScope.launch {
            repository.upsertStudent(student.copy(priority = priority))
        }
    }

    fun deleteStudent(studentId: Int) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
        }
    }

    fun batchInsert(
        insertDate: String,
        group: String,
        studentListString: String
    ) {

        studentListString
            .split(",\\s+|\\s+,\\s+|\\s+,|,|\\n".toRegex())
            .forEach {
                upsertStudent(
                    Student(
                        name = it,
                        update_date = insertDate,
                        group = group
                    )
                )
            }


    }

    fun updateFilterPreferences(
        group: String,
        priority: Priority,
        filterType: FilterType
    ) {

        viewModelScope.launch {
            filterPreferencesRepository.updateGroup(group)
            filterPreferencesRepository.updatePriority(priority)
            filterPreferencesRepository.updateFilterType(filterType)
        }

    }

    fun filterStudents(
        students: List<Student>,
        filterType: FilterType,
        preferenceGroup: String,
        preferencePriority: Priority
    ) =

        when (filterType) {
            FilterType.BY_PRIORITY -> students.filter { it.priority == preferencePriority }
            FilterType.BY_GROUP -> students.filter { it.group == preferenceGroup }
            else -> students
        }


    fun getGroups(
        students: List<Student>
    ) = students
        .groupBy { it.group }
        .keys
        .toList()

    suspend fun exportStudents(students: List<Student>, uri: Uri): Int {

        val deferredJob = viewModelScope.async {
            repository.exportStudents(students, uri)
        }
        return deferredJob.await()
    }

    suspend fun importStudents(uri: Uri): Int {
        val deferredJob = viewModelScope.async {
            repository.importStudents(uri)
        }
        return deferredJob.await()
    }
}

sealed interface StudentUiState {

    object Loading : StudentUiState

    object Empty : StudentUiState

    data class Success(
        val students: List<Student>,
        val preferenceGroup: String,
        val preferencePriority: Priority,
        val filterType: FilterType
    ) : StudentUiState
}