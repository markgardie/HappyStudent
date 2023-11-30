package com.example.happystudent.feature.students

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happystudent.R
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.theme.Green40
import com.example.happystudent.core.theme.Red40
import com.example.happystudent.core.theme.Yellow60
import com.example.happystudent.feature.students.StudentViewModel.Companion.FIRST_PRIORITY
import com.example.happystudent.feature.students.StudentViewModel.Companion.GROUP_FILTER_TYPE
import com.example.happystudent.feature.students.StudentViewModel.Companion.PRIORITY_FILTER_TYPE
import com.example.happystudent.feature.students.StudentViewModel.Companion.SECOND_PRIORITY
import com.example.happystudent.feature.students.StudentViewModel.Companion.THIRD_PRIORITY
import com.example.happystudent.feature.students.navigation.DEFAULT_STUDENT_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentViewModel,
    navigateToUpsert: (Int) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var filter by remember {
        mutableStateOf(FIRST_PRIORITY)
    }
    var filterType by remember {
        mutableIntStateOf(PRIORITY_FILTER_TYPE)
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            StudentListTopBar(
                onShowBottomSheet = { show ->
                    showBottomSheet = show
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToUpsert(DEFAULT_STUDENT_ID) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new student")
            }
        }
    ) { innerPadding ->

        when (uiState) {
            is StudentUiState.Empty -> EmptyState()
            is StudentUiState.Loading -> LoadingState()
            is StudentUiState.Success -> {

                FilterBottomSheet(
                    sheetState = sheetState,
                    showBottomState = showBottomSheet,
                    scope = scope,
                    onShowChange = { show ->
                        showBottomSheet = show
                    },
                    onChangeFilter = { newPriority ->
                        filter = newPriority
                    },
                    groups = viewModel.getGroups((uiState as StudentUiState.Success).students),
                    onChangeGroup = { group ->
                        filter = group
                    },
                    onChangeFilterType = { type ->
                        filterType = type
                    },
                    filter = filter
                )



                StudentList(
                    students = viewModel.filterStudents(
                        students = (uiState as StudentUiState.Success).students,
                        filterType = filterType,
                        filter = filter
                    ),
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
    onChangeFilter: (String) -> Unit,
    onChangeFilterType: (Int) -> Unit,
    onChangeGroup: (String) -> Unit,
    groups: List<String>,
    filter: String
) {

    if (showBottomState) {
        ModalBottomSheet(
            onDismissRequest = { onShowChange(false) },
            sheetState = sheetState
        ) {

            PriorityChips(
                onChangeFilter = onChangeFilter,
                onChangeFilterType = onChangeFilterType,
                filter = filter
            )

            GroupChips(
                groups = groups,
                onChangeFilterType = onChangeFilterType,
                onChangeFilter = onChangeGroup,
                filter = filter
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityChips(
    onChangeFilter: (String) -> Unit,
    onChangeFilterType: (Int) -> Unit,
    filter: String
) {

    val priorities = listOf(
        FIRST_PRIORITY, SECOND_PRIORITY, THIRD_PRIORITY
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
                    onChangeFilterType(PRIORITY_FILTER_TYPE)
                    onChangeFilter(priority)
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
    onChangeFilterType: (Int) -> Unit,
    onChangeFilter: (String) -> Unit,
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
                    onChangeFilterType(GROUP_FILTER_TYPE)
                    onChangeFilter(group)
                },
                label = { Text(text = group) }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListTopBar(
    onShowBottomSheet: (Boolean) -> Unit
) {

    TopAppBar(
        title = { Text(text = "Happy Student") },
        actions = {
            IconButton(onClick = { onShowBottomSheet(true) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Фільтр учнів"
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
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
fun LoadingState() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }

}

@Composable
fun EmptyState() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Немає учнів")

    }

}

