package com.example.happystudent.feature.students.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.happystudent.feature.students.BatchInsertScreen
import com.example.happystudent.feature.students.StudentViewModel


const val batchInsertRoute = "batch_insert"

fun NavController.navigateToBatch() {
    this.navigate(batchInsertRoute)
}

fun NavGraphBuilder.batchInsertScreen(
    viewModel: StudentViewModel,
    navigateBackToList: () -> Unit
) {

    composable(
        route = batchInsertRoute,

    ) {
        BatchInsertScreen(
            viewModel = viewModel,
            navigateBackToList = navigateBackToList
        )
    }

}