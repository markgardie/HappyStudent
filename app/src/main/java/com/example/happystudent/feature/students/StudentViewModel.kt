package com.example.happystudent.feature.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happystudent.core.data.di.OfflineFirstRepository
import com.example.happystudent.core.data.repository.StudentRepository
import com.example.happystudent.core.database.model.StudentGroup
import com.example.happystudent.core.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    @OfflineFirstRepository private val repository: StudentRepository
) : ViewModel() {

    val uiState: StateFlow<StudentUiState> = repository.getStudentStream().map {
        if (it.isEmpty()) StudentUiState.Empty
        else StudentUiState.Success(it)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), StudentUiState.Loading
    )


    fun upsertStudent(student: Student) {

        val priority = if (student.leaving_probability > CRITICAL_PROB) FIRST_PRIORITY
        else if (student.leaving_probability > IMPORTANT_PROB) SECOND_PRIORITY
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

    fun filterStudents(
        students: List<Student>,
        filterType: Int,
        filter: String
    ) =
        if (filterType == PRIORITY_FILTER_TYPE) {
            filterByPriority(students, filter)
        }
        else {
            filterByGroup(students, filter)
        }

    private fun filterByPriority(students: List<Student>, priority: String) =
        students.filter { it.priority == priority }


    private fun filterByGroup(students: List<Student>, group: String) =
        students.filter { it.group == group }

    fun getGroups(students: List<Student>) =
        students
            .groupBy { it.group }
            .keys
            .toList()

    companion object {

        const val CRITICAL_PROB = 70
        const val IMPORTANT_PROB = 40

        const val FIRST_PRIORITY = "Критично"
        const val SECOND_PRIORITY = "Варті уваги"
        const val THIRD_PRIORITY = "Задовільно"

        const val PRIORITY_FILTER_TYPE = 0
        const val GROUP_FILTER_TYPE = 1

    }

}

sealed interface StudentUiState {

    object Loading : StudentUiState

    object Empty : StudentUiState

    data class Success(val students: List<Student>) : StudentUiState
}