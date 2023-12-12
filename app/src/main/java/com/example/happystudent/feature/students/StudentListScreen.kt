package com.example.happystudent.feature.students

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happystudent.R
import com.example.happystudent.core.datastore.FilterPreferences.FilterType
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.theme.Green40
import com.example.happystudent.core.theme.Red40
import com.example.happystudent.core.theme.Yellow60
import com.example.happystudent.feature.students.StudentViewModel.Companion.DEFAULT
import com.example.happystudent.feature.students.StudentViewModel.Companion.FIRST_PRIORITY
import com.example.happystudent.feature.students.StudentViewModel.Companion.SECOND_PRIORITY
import com.example.happystudent.feature.students.StudentViewModel.Companion.THIRD_PRIORITY
import com.example.happystudent.feature.students.navigation.DEFAULT_STUDENT_ID
import com.example.happystudent.feature.students.util.formatList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentViewModel,
    navigateToUpsert: (Int) -> Unit,
    context: Context
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    when (uiState) {
        is StudentUiState.Empty -> EmptyState(navigateToUpsert = navigateToUpsert)
        is StudentUiState.Loading -> LoadingState(navigateToUpsert = navigateToUpsert)
        is StudentUiState.Success -> {

            Scaffold(
                topBar = {
                    StudentListTopBar(
                        onShowBottomSheet = { show ->
                            showBottomSheet = show
                        },
                        shareText = viewModel.filterStudents().formatList(),
                        context = context
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navigateToUpsert(DEFAULT_STUDENT_ID) }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new student")
                    }
                }
            ) { innerPadding ->
                FilterBottomSheet(
                    sheetState = sheetState,
                    showBottomState = showBottomSheet,
                    scope = scope,
                    onShowChange = { show ->
                        showBottomSheet = show
                    },
                    groups = viewModel.getGroups(),
                    filter = (uiState as StudentUiState.Success).filter,
                    onUpdateFilterPreferences = viewModel::updateFilterPreferences
                )


                StudentList(
                    students = viewModel.filterStudents(),
                    deleteStudent = viewModel::deleteStudent,
                    navigateToUpsert = navigateToUpsert,
                    innerPadding = innerPadding
                )
            }


        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    showBottomState: Boolean,
    sheetState: SheetState,
    scope: CoroutineScope,
    onShowChange: (Boolean) -> Unit,
    onUpdateFilterPreferences: (String, FilterType) -> Unit,
    groups: List<String>,
    filter: String
) {

    if (showBottomState) {
        ModalBottomSheet(
            onDismissRequest = { onShowChange(false) },
            sheetState = sheetState
        ) {

            PriorityChips(
                onUpdateFilterPreferences = onUpdateFilterPreferences,
                filter = filter
            )

            GroupChips(
                groups = groups,
                onUpdateFilterPreferences = onUpdateFilterPreferences,
                filter = filter
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityChips(
    onUpdateFilterPreferences: (String, FilterType) -> Unit,
    filter: String
) {

    val priorities = listOf(
        DEFAULT, FIRST_PRIORITY, SECOND_PRIORITY, THIRD_PRIORITY
    )

    Text(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp),
        text = "Фільтр по пріоритетам"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(priorities) { priority ->
            FilterChip(
                selected = filter == priority,
                onClick = {
                    val filterType =
                        if (priority == DEFAULT) FilterType.DEFAULT
                        else FilterType.BY_PRIORITY

                    onUpdateFilterPreferences(priority, filterType)
                },
                label = { Text(text = priority) }
            )
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChips(
    groups: List<String>,
    onUpdateFilterPreferences: (String, FilterType) -> Unit,
    filter: String
) {

    Text(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp),
        text = "Фільтр по групам"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(groups) { group ->

            FilterChip(
                selected = group == filter,
                onClick = {
                    onUpdateFilterPreferences(group, FilterType.BY_GROUP)
                },
                label = { Text(text = group) }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListTopBar(
    onShowBottomSheet: (Boolean) -> Unit,
    shareText: String,
    context: Context
) {

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    TopAppBar(
        title = { Text(text = "Happy Student") },
        actions = {
            IconButton(onClick = { onShowBottomSheet(true) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Фільтр учнів"
                )
            }
            IconButton(onClick = { startActivity(context, shareIntent, null) }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Поділитись списком"
                )
            }
        }
    )

}

@Composable
fun StudentList(
    students: List<Student>,
    deleteStudent: (Int) -> Unit,
    navigateToUpsert: (Int) -> Unit,
    innerPadding: PaddingValues
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        items(
            items = students,
            key = { student ->
                student.id
            }
        ) {
            StudentDismissItem(
                student = it,
                deleteStudent = deleteStudent,
                navigateToUpsert = navigateToUpsert
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDismissItem(
    student: Student,
    deleteStudent: (Int) -> Unit,
    navigateToUpsert: (Int) -> Unit
) {

    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(student)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToEnd) {
                show = false
                true
            } else false
        }, positionalThreshold = { 150.dp.toPx() }
    )

    AnimatedVisibility(
        show, exit = fadeOut(spring())
    ) {
        SwipeToDismiss(
            state = dismissState,
            background = {
                DismissBackground()
            },
            dismissContent = {
                StudentCard(
                    student = student,
                    navigateToUpsert = navigateToUpsert
                )
            },
            directions = setOf(
                DismissDirection.StartToEnd
            )
        )
    }

    LaunchedEffect(show) {
        if (!show) {
            delay(800)
            deleteStudent(currentItem.id)
        }
    }

}

@Composable
fun DismissBackground() {

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            Icons.Default.Delete,
            contentDescription = "delete"
        )

    }
}

@Composable
fun StudentCard(
    student: Student,
    navigateToUpsert: (Int) -> Unit
) {

    val color by remember {
        mutableStateOf(
            if (student.leaving_probability > 70) Red40
            else if (student.leaving_probability > 40) Yellow60
            else Green40
        )
    }

    ListItem(
        modifier = Modifier.clickable {
            navigateToUpsert(student.id)
        },
        headlineContent = {
            Text(text = student.name)
        },
        supportingContent = {

            Text(text = student.group)

        },
        leadingContent = {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Фото студента"
            )
        },
        trailingContent = {
            Column {

                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .background(
                            color = color,
                            shape = MaterialTheme.shapes.extraSmall
                        ),
                    text = "${student.leaving_probability}%",
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = student.update_date
                )
            }
        }
    )


}

@Composable
fun LoadingState(
    navigateToUpsert: (Int) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToUpsert(DEFAULT_STUDENT_ID) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new student")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }


}

@Composable
fun EmptyState(
    navigateToUpsert: (Int) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToUpsert(DEFAULT_STUDENT_ID) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new student")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Немає учнів")

        }
    }


}

