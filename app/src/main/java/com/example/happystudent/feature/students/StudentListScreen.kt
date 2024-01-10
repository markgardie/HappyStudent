package com.example.happystudent.feature.students

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.happystudent.R
import com.example.happystudent.core.datastore.FilterPreferences.FilterType
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.model.Student.Companion.ALL
import com.example.happystudent.core.model.Student.Companion.CRITICAL_PROB
import com.example.happystudent.core.model.Student.Companion.FIRST_PRIORITY
import com.example.happystudent.core.model.Student.Companion.IMPORTANT_PROB
import com.example.happystudent.core.model.Student.Companion.SECOND_PRIORITY
import com.example.happystudent.core.model.Student.Companion.THIRD_PRIORITY
import com.example.happystudent.core.model.Student.Companion.UNDEFINED_ID
import com.example.happystudent.core.model.Student.Companion.UNDEFINED_PRIORITY
import com.example.happystudent.core.model.Student.Companion.ZERO_PROB
import com.example.happystudent.core.theme.Green40
import com.example.happystudent.core.theme.PurpleGrey40
import com.example.happystudent.core.theme.Red40
import com.example.happystudent.core.theme.Yellow60
import com.example.happystudent.core.theme.components.FabItem
import com.example.happystudent.core.theme.components.MultiFabState
import com.example.happystudent.core.theme.components.MultiFloatingActionButton
import com.example.happystudent.core.theme.components.rememberMultiFabState
import com.example.happystudent.core.theme.padding
import com.example.happystudent.feature.students.util.formatList
import kotlinx.coroutines.delay


const val FAB_ROTATE = 315f
const val ONE_STUDENT_FAB_LABEL = "Додати одного студента"
const val GROUP_FAB_LABEL = "Додати групу студентів"
const val ONE_STUDENT_FAB_ID = 0
const val GROUP_FAB_ID = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentViewModel,
    navigateToUpsert: (Int) -> Unit,
    navigateToBatch: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    var multiFabState by rememberMultiFabState()

    val fabItems = listOf(
        FabItem(id = ONE_STUDENT_FAB_ID, label = ONE_STUDENT_FAB_LABEL),
        FabItem(id = GROUP_FAB_ID, label = GROUP_FAB_LABEL)
    )


    when (uiState) {

        is StudentUiState.Empty -> EmptyState(
            navigateToUpsert = navigateToUpsert,
            multiFabState = multiFabState,
            fabItems = fabItems,
            onStateChanged = { multiFabState = it },
            navigateToBatch = navigateToBatch
        )

        is StudentUiState.Loading -> LoadingState(navigateToUpsert = navigateToUpsert)
        is StudentUiState.Success -> {

            Scaffold(
                topBar = {
                    StudentListTopBar(
                        onShowBottomSheet = { show ->
                            showBottomSheet = show
                        },
                        shareText = viewModel
                            .filterStudents(
                                (uiState as StudentUiState.Success).students,
                                (uiState as StudentUiState.Success).filterType,
                                (uiState as StudentUiState.Success).filter
                            )
                            .formatList()
                    )
                },
                floatingActionButton = {

                    MultiFloatingActionButton(
                        state = multiFabState,
                        onStateChange = {
                            multiFabState = it
                        },
                        mainIconRes = R.drawable.ic_add,
                        rotateDegree = FAB_ROTATE,
                        fabItems = fabItems,
                        onItemClicked = { fabItem ->
                            if (fabItem.label == ONE_STUDENT_FAB_LABEL) {
                                navigateToUpsert(UNDEFINED_ID)
                            } else {
                                navigateToBatch()
                            }
                        }
                    )

                }
            ) { innerPadding ->
                FilterBottomSheet(
                    sheetState = sheetState,
                    showBottomState = showBottomSheet,
                    onShowChange = { show ->
                        showBottomSheet = show
                    },
                    groups = viewModel.getGroups((uiState as StudentUiState.Success).students),
                    filter = (uiState as StudentUiState.Success).filter,
                    onUpdateFilterPreferences = viewModel::updateFilterPreferences
                )


                StudentList(
                    students = viewModel.filterStudents(
                        (uiState as StudentUiState.Success).students,
                        (uiState as StudentUiState.Success).filterType,
                        (uiState as StudentUiState.Success).filter
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
        ALL, UNDEFINED_PRIORITY, FIRST_PRIORITY, SECOND_PRIORITY, THIRD_PRIORITY
    )

    Text(
        modifier = Modifier
            .padding(
                top = MaterialTheme.padding.small,
                bottom =MaterialTheme.padding.small,
                start = MaterialTheme.padding.medium
            ),
        text = stringResource(R.string.priority_filter)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.padding.medium,
                bottom = MaterialTheme.padding.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small)
    ) {

        items(priorities) { priority ->
            FilterChip(
                selected = filter == priority,
                onClick = {
                    val filterType =
                        if (priority == ALL) FilterType.ALL
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
            .padding(
                top = MaterialTheme.padding.small,
                bottom = MaterialTheme.padding.small,
                start = MaterialTheme.padding.medium
            ),
        text = stringResource(R.string.group_filter)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.padding.medium,
                bottom = MaterialTheme.padding.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small)
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
    shareText: String
) {

    val context = LocalContext.current

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    TopAppBar(
        title = { Text(text = stringResource(R.string.happy_student_top_bar)) },
        actions = {
            IconButton(onClick = { onShowBottomSheet(true) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = stringResource(R.string.student_filter)
                )
            }
            IconButton(onClick = { startActivity(context, shareIntent, null) }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share_student_list)
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
            .padding(MaterialTheme.padding.medium, MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete_student)
        )

    }
}

@Composable
fun StudentCard(
    student: Student,
    navigateToUpsert: (Int) -> Unit
) {
    val context = LocalContext.current

    try {
        context.contentResolver.takePersistableUriPermission(
            student.imageUri.toUri(),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val color by remember {
        mutableStateOf(
            if (student.leaving_probability > CRITICAL_PROB) Red40
            else if (student.leaving_probability > IMPORTANT_PROB) Yellow60
            else if (student.leaving_probability == ZERO_PROB) PurpleGrey40
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
            AsyncImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                model = student.imageUri.toUri(),
                error = painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = stringResource(id = R.string.student_photo),
                contentScale = ContentScale.Crop
            )
        },
        trailingContent = {
            Column {

                Text(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.padding.small)
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
                onClick = { navigateToUpsert(UNDEFINED_ID) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add_student))
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
    navigateToUpsert: (Int) -> Unit,
    navigateToBatch: () -> Unit,
    multiFabState: MultiFabState,
    fabItems: List<FabItem>,
    onStateChanged: (MultiFabState) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            MultiFloatingActionButton(
                state = multiFabState,
                onStateChange = {
                    onStateChanged(it)
                },
                mainIconRes = R.drawable.ic_add,
                rotateDegree = FAB_ROTATE,
                fabItems = fabItems,
                onItemClicked = { fabItem ->
                    if (fabItem.label == ONE_STUDENT_FAB_LABEL) navigateToUpsert(UNDEFINED_ID)
                    else navigateToBatch()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = stringResource(R.string.empty_student_list))

        }
    }


}

