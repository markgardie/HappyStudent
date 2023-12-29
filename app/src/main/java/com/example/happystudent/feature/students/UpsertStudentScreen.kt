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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.happystudent.R
import com.example.happystudent.core.model.Student
import com.example.happystudent.core.theme.components.NavBackTopBar
import com.example.happystudent.feature.students.navigation.DEFAULT_PROBABILITY
import java.lang.Exception
import java.text.DateFormat
import java.util.Date

@Composable
fun UpsertStudentScreen(
    viewModel: StudentViewModel,
    studentId: Int,
    probability: Double,
    navigateToList: () -> Unit,
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
            if (probability == DEFAULT_PROBABILITY) {
                student?.leaving_probability?.toString() ?: "0.0"
            } else probability.toString()
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
                    .padding(32.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = imageUri,
                    error = painterResource(id = R.drawable.avatar_placeholder),
                    contentDescription = "Фото студента",
                    modifier = Modifier
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )


                Icon(
                    modifier = Modifier
                        .clickable { imageUri = null },
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Видалити фото"
                )


            }




            TextField(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text(text = "Ім'я") }
            )

            TextField(
                value = groupText,
                onValueChange = { groupText = it },
                label = { Text(text = "Група") }
            )

            TextField(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                value = probabilityText,
                onValueChange = { probabilityText = it },
                label = { Text(text = "Вірогідність відвалу") }
            )

            Row(
                modifier = Modifier.padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                FilledTonalButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {
                        navigateToSurvey()
                    }
                ) {
                    Text(text = "Оцінити учня")
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
                                imageUri = imageUri.toString(),
                                priority = ""
                            )
                        )
                        navigateToList()

                    }) {
                    Text(text = "Зберегти")
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