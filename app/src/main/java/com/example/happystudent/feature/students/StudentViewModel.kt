package com.example.happystudent.feature.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happystudent.core.data.repository.StudentRepository
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
    private val repository: StudentRepository
): ViewModel() {

    val uiState: StateFlow<StudentUiState> = repository.getStudentStream()
        .map {
            if (it.isEmpty()) StudentUiState.Empty
            else StudentUiState.Success(it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            StudentUiState.Loading
        )

    fun upsertStudent(student: Student) {
        viewModelScope.launch {
            repository.upsertStudent(student)
        }
    }

    fun deleteStudent(studentId: Int) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
        }
    }
}

sealed interface StudentUiState {

    object Loading: StudentUiState

    object Empty: StudentUiState

    data class Success(val students: List<Student>): StudentUiState
}