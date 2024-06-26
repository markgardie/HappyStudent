package com.example.happystudent.feature.students

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.happystudent.R
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.model.Student.Companion.ZERO_PROB
import com.example.happystudent.core.theme.components.NavBackTopBar
import com.example.happystudent.core.theme.padding
import java.text.DateFormat
import java.util.Date


private const val STUDENT_PHOTO_SIZE = 150

@Composable
fun UpsertStudentScreen(
    viewModel: StudentViewModel,
    studentId: Int,
    evaluatedProb: Double,
    navigateToSurvey: () -> Unit,
    navigateBackToList: () -> Unit
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val student = findStudent(uiState, studentId)

    var nameText by rememberSaveable {
        mutableStateOf(student?.name ?: "")
    }

    var groupText by rememberSaveable {
        mutableStateOf(student?.group ?: "")
    }

    var probabilityText by remember {
       mutableStateOf(
           if (evaluatedProb <= ZERO_PROB) {
               student?.leaving_probability?.toString() ?: "0.0"
           } else evaluatedProb.toString()
       )
    }

    var imageUri by rememberSaveable {
        mutableStateOf(student?.imageUri?.toUri())
    }

    try {
        imageUri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { imageUri = uri } }
    )


    Scaffold(
        topBar = { NavBackTopBar(navigateBackToList) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Box(
                modifier = Modifier
                    .padding(MaterialTheme.padding.large),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = imageUri,
                    error = painterResource(id = R.drawable.avatar_placeholder),
                    contentDescription = stringResource(id = R.string.student_photo),
                    modifier = Modifier
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .size(STUDENT_PHOTO_SIZE.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )


                Icon(
                    modifier = Modifier
                        .clickable { imageUri = null },
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete_photo)
                )


            }

            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = MaterialTheme.padding.medium),
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text(text = stringResource(id = R.string.student_name)) }
            )

            OutlinedTextField(
                value = groupText,
                onValueChange = { groupText = it },
                label = { Text(text = stringResource(id = R.string.group_name)) }
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = MaterialTheme.padding.medium),
                value = probabilityText,
                onValueChange = { probabilityText = it },
                label = { Text(text = stringResource(id = R.string.probability)) }
            )

            Row(
                modifier = Modifier.padding(vertical = MaterialTheme.padding.large),
                horizontalArrangement = Arrangement.Center
            ) {

                FilledTonalButton(
                    modifier = Modifier.padding(horizontal = MaterialTheme.padding.medium),
                    onClick = {
                        navigateToSurvey()
                    }
                ) {
                    Text(text = stringResource(R.string.evaluate_student))
                }

                Button(
                    onClick = {

                        val currentDate = DateFormat
                            .getDateInstance()
                            .format(Date())

                        viewModel.upsertStudent(
                            Student(
                                id = studentId,
                                name = nameText,
                                group = groupText,
                                leaving_probability = probabilityText.toDouble(),
                                update_date = currentDate,
                                imageUri = imageUri.toString()
                            )
                        )
                        navigateBackToList()

                    }) {
                    Text(text = stringResource(R.string.save))
                }
            }

        }
    }


}

fun findStudent(uiState: StudentUiState, studentId: Int): Student? {

    val students = when (uiState) {
        is StudentUiState.Success -> uiState.students
        else -> emptyList()
    }

    for (student in students) {
        if (student.id == studentId) {
            return student
        }
    }

    return null
}