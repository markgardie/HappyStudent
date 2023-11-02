package com.example.happystudent.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.happystudent.feature.students.StudentViewModel
import com.example.happystudent.feature.students.studentListRoute
import com.example.happystudent.feature.students.studentListScreen

@Composable
fun HappyStudentNavHost() {

    val navController = rememberNavController()
    val studentViewModel: StudentViewModel = hiltViewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        NavHost(
            navController = navController,
            startDestination = studentListRoute
        ) {

            studentListScreen(studentViewModel)

        }
    }
}