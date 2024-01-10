package com.example.happystudent.feature.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happystudent.core.data.di.OfflineFirstRepository
import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.datastore.DefaultFilterPreferencesRepository
import com.example.happystudent.core.datastore.FilterPreferences.FilterType
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.model.Student.Companion.CRITICAL_PROB
import com.example.happystudent.core.model.Student.Companion.FIRST_PRIORITY
import com.example.happystudent.core.model.Student.Companion.IMPORTANT_PROB
import com.example.happystudent.core.model.Student.Companion.SECOND_PRIORITY
import com.example.happystudent.core.model.Student.Companion.THIRD_PRIORITY
import com.example.happystudent.core.model.Student.Companion.UNDEFINED_PRIORITY
import com.example.happystudent.core.model.Student.Companion.ZERO_PROB
import dagger.hilt.android.lifecycle.HiltViewModel
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
            filter = preferences.filter,
            filterType = preferences.filterType
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            StudentUiState.Loading
        )


    fun upsertStudent(student: Student) {

        val priority = if (student.leaving_probability > CRITICAL_PROB) FIRST_PRIORITY
        else if (student.leaving_probability > IMPORTANT_PROB) SECOND_PRIORITY
        else if (student.leaving_probability == ZERO_PROB) UNDEFINED_PRIORITY
        else THIRD_PRIORITY

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
            .split(",\\s+|\\s+,\\s+|\\s+|,".toRegex())
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
        filter: String,
        filterType: FilterType
    ) {

        viewModelScope.launch {
            filterPreferencesRepository.updateFilter(filter)
            filterPreferencesRepository.updateFilterType(filterType)
        }

    }

    fun filterStudents(
        students: List<Student>,
        filterType: FilterType,
        filter: String
    ) =

        when (filterType) {
            FilterType.BY_PRIORITY -> students.filter { it.priority == filter }
            FilterType.BY_GROUP -> students.filter { it.group == filter }
            else -> students
        }


    fun getGroups(
        students: List<Student>
    ) = students
        .groupBy { it.group }
        .keys
        .toList()


}

sealed interface StudentUiState {

    object Loading : StudentUiState

    object Empty : StudentUiState

    data class Success(
        val students: List<Student>,
        val filter: String,
        val filterType: FilterType
    ) : StudentUiState
}