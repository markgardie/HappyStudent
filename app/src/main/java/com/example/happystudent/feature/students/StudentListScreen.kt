package com.example.happystudent.feature.students

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.happystudent.core.datastore.FilterPreferences.Priority
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.model.Student.Companion.CRITICAL_PROB
import com.example.happystudent.core.model.Student.Companion.IMPORTANT_PROB
import com.example.happystudent.core.model.Student.Companion.UNDEFINED_ID
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


private const val FAB_ROTATE = 315f
private const val ONE_STUDENT_FAB_ID = 0
private const val GROUP_FAB_ID = 1
private const val STUDENT_ICON_SIZE = 50

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

    var isLongPressEnabled by remember {
        mutableStateOf(false)
    }
    val onLongPressEnabledChange = { value: Boolean -> isLongPressEnabled = value }
    var selectedStudentsForDelete by remember {
        mutableStateOf<List<Student>>(emptyList())
    }

    var multiFabState by rememberMultiFabState()

    val fabItems = listOf(
        FabItem(id = ONE_STUDENT_FAB_ID, label = stringResource(R.string.add_a_student)),
        FabItem(id = GROUP_FAB_ID, label = stringResource(R.string.add_a_group))
    )

    var allStudentsSelected by remember {
        mutableStateOf(false)
    }




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

            LaunchedEffect(key1 = allStudentsSelected) {
                selectedStudentsForDelete =
                    if (allStudentsSelected)
                        selectedStudentsForDelete.plus((uiState as StudentUiState.Success).students)
                    else
                        selectedStudentsForDelete.minus((uiState as StudentUiState.Success).students.toSet())
            }

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
                                (uiState as StudentUiState.Success).preferenceGroup,
                                (uiState as StudentUiState.Success).preferencePriority
                            )
                            .formatList(),
                        isLongPressEnabled = isLongPressEnabled,
                        onLongPressEnabledChange = onLongPressEnabledChange,
                        selectedStudentsForDelete = selectedStudentsForDelete,
                        deleteStudent = viewModel::deleteStudent,
                        onClearStudentsForDelete = { selectedStudentsForDelete = emptyList() },
                        allStudentsSelected = allStudentsSelected,
                        onChangeAllStudentsSelected = { allStudentsSelected = it },
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
                            if (fabItem.id == ONE_STUDENT_FAB_ID) {
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
                    preferenceGroup = (uiState as StudentUiState.Success).preferenceGroup,
                    preferencePriority = (uiState as StudentUiState.Success).preferencePriority,
                    onUpdateFilterPreferences = viewModel::updateFilterPreferences
                )


                StudentList(
                    students = viewModel.filterStudents(
                        (uiState as StudentUiState.Success).students,
                        (uiState as StudentUiState.Success).filterType,
                        (uiState as StudentUiState.Success).preferenceGroup,
                        (uiState as StudentUiState.Success).preferencePriority
                    ),
                    navigateToUpsert = navigateToUpsert,
                    innerPadding = innerPadding,
                    isLongPressEnabled = isLongPressEnabled,
                    onLongPressEnabledChange = onLongPressEnabledChange,
                    selectedStudentsForDelete = selectedStudentsForDelete,
                    onStudentSelectForDelete = { student ->
                        selectedStudentsForDelete =
                            if (selectedStudentsForDelete.contains(student)) {
                                selectedStudentsForDelete.minus(student)
                            } else {
                                selectedStudentsForDelete.plus(student)
                            }
                    }
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
    onUpdateFilterPreferences: (String, Priority, FilterType) -> Unit,
    groups: List<String>,
    preferenceGroup: String,
    preferencePriority: Priority
) {

    if (showBottomState) {
        ModalBottomSheet(
            onDismissRequest = { onShowChange(false) },
            sheetState = sheetState
        ) {

            Column(
                modifier = Modifier.padding(start = MaterialTheme.padding.medium)
            ) {
                TextButton(
                    onClick = {
                        onUpdateFilterPreferences("", Priority.UNDEFINED, FilterType.NO_FILTER)
                    }
                ) {
                    Text(text = stringResource(R.string.reset_filters))
                }

                PriorityChips(
                    onUpdateFilterPreferences = onUpdateFilterPreferences,
                    preferencePriority = preferencePriority
                )

                GroupChips(
                    groups = groups,
                    onUpdateFilterPreferences = onUpdateFilterPreferences,
                    preferenceGroup = preferenceGroup
                )
            }


        }
    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun PriorityChips(
    onUpdateFilterPreferences: (String, Priority, FilterType) -> Unit,
    preferencePriority: Priority
) {

    Text(
        modifier = Modifier
            .padding(
                top = MaterialTheme.padding.small,
                bottom = MaterialTheme.padding.small
            ),
        text = stringResource(R.string.priority_filter)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = MaterialTheme.padding.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small)
    ) {

        items(
            listOf(
                Priority.ZERO,
                Priority.FIRST,
                Priority.SECOND,
                Priority.THIRD
            )
        ) { priority ->
            FilterChip(
                selected = preferencePriority == priority,
                onClick = {

                    onUpdateFilterPreferences("", priority, FilterType.BY_PRIORITY)
                },
                label = {

                    val priorityText =
                        when (priority) {
                            Priority.ZERO -> stringResource(R.string.priority_undefined)
                            Priority.FIRST -> stringResource(R.string.priority_first)
                            Priority.SECOND -> stringResource(R.string.priority_second)
                            Priority.THIRD -> stringResource(R.string.priority_third)
                            else -> ""
                        }

                    Text(text = priorityText)

                }
            )
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChips(
    groups: List<String>,
    onUpdateFilterPreferences: (String, Priority, FilterType) -> Unit,
    preferenceGroup: String
) {

    Text(
        modifier = Modifier
            .padding(
                top = MaterialTheme.padding.small,
                bottom = MaterialTheme.padding.small
            ),
        text = stringResource(R.string.group_filter)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = MaterialTheme.padding.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small)
    ) {
        items(groups) { group ->

            FilterChip(
                selected = group == preferenceGroup,
                onClick = {
                    onUpdateFilterPreferences(group, Priority.UNDEFINED, FilterType.BY_GROUP)
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
    isLongPressEnabled: Boolean,
    onLongPressEnabledChange: (Boolean) -> Unit,
    onClearStudentsForDelete: () -> Unit,
    selectedStudentsForDelete: List<Student>,
    deleteStudent: (Int) -> Unit,
    allStudentsSelected: Boolean,
    onChangeAllStudentsSelected: (Boolean) -> Unit,
) {

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    TopAppBar(
        title = {
            if (!isLongPressEnabled) {
                Text(text = stringResource(R.string.happy_student_top_bar))
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allStudentsSelected,
                        onCheckedChange = onChangeAllStudentsSelected,
                    )

                    Text(text = stringResource(R.string.select_all))
                }
            }
        },
        actions = {
            if (!isLongPressEnabled) {
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

                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.more_options),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.export_data))
                        },
                        onClick = { /* TODO */ },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.import_data))
                        },
                        onClick = { /* TODO */ },
                    )

                }
            } else {
                IconButton(onClick = {
                    selectedStudentsForDelete.forEach {
                        deleteStudent(it.id)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }
        },
        navigationIcon = {
            if (isLongPressEnabled) {
                IconButton(onClick = {
                    onLongPressEnabledChange(false)
                    onClearStudentsForDelete()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    )

}

@Composable
fun StudentList(
    students: List<Student>,
    navigateToUpsert: (Int) -> Unit,
    innerPadding: PaddingValues,
    isLongPressEnabled: Boolean,
    onLongPressEnabledChange: (Boolean) -> Unit,
    selectedStudentsForDelete: List<Student>,
    onStudentSelectForDelete: (Student) -> Unit
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
            StudentCard(
                student = it,
                navigateToUpsert = navigateToUpsert,
                isLongPressEnabled = isLongPressEnabled,
                isSelected = selectedStudentsForDelete.contains(it),
                onLongPressEnabledChange = onLongPressEnabledChange,
                onStudentSelectForDelete = onStudentSelectForDelete
            )

        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentCard(
    student: Student,
    navigateToUpsert: (Int) -> Unit,
    isLongPressEnabled: Boolean,
    isSelected: Boolean,
    onLongPressEnabledChange: (Boolean) -> Unit,
    onStudentSelectForDelete: (Student) -> Unit
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


    ListItem(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    if (isLongPressEnabled) onStudentSelectForDelete(student)
                    else navigateToUpsert(student.id)
                },
                onLongClick = { onLongPressEnabledChange(true) }
            ),
        headlineContent = {
            Text(text = student.name)
        },
        supportingContent = {

            Text(text = student.group)

        },
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (isLongPressEnabled) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null,
                        modifier = Modifier.padding(end = MaterialTheme.padding.small)
                    )
                }

                AsyncImage(
                    modifier = Modifier
                        .size(STUDENT_ICON_SIZE.dp)
                        .clip(CircleShape),
                    model = student.imageUri.toUri(),
                    error = painterResource(id = R.drawable.avatar_placeholder),
                    contentDescription = stringResource(id = R.string.student_photo),
                    contentScale = ContentScale.Crop
                )
            }

        },
        trailingContent = {
            Column {

                Text(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.padding.small)
                        .background(
                            color = if (student.leaving_probability > CRITICAL_PROB) Red40
                            else if (student.leaving_probability > IMPORTANT_PROB) Yellow60
                            else if (student.leaving_probability <= ZERO_PROB) PurpleGrey40
                            else Green40,
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
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_student)
                )
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
                    if (fabItem.id == ONE_STUDENT_FAB_ID) navigateToUpsert(UNDEFINED_ID)
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

