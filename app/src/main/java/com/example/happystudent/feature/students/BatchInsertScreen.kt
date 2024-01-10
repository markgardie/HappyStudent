package com.example.happystudent.feature.students

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.happystudent.R
import com.example.happystudent.core.theme.components.NavBackTopBar
import java.text.DateFormat
import java.util.Date

@Composable
fun BatchInsertScreen(
    viewModel: StudentViewModel,
    navigateBackToList: () -> Unit
) {

    var groupNameText by remember {
        mutableStateOf("")
    }

    var studentListString by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            NavBackTopBar { navigateBackToList() }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.batch_insert_instruction),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(300.dp)
            )

            OutlinedTextField(
                value = groupNameText,
                onValueChange = { groupNameText = it },
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .width(300.dp),
                label = { Text(text = stringResource(id = R.string.group_name)) }
            )

            OutlinedTextField(
                value = studentListString,
                onValueChange = { studentListString = it },
                label = { Text(text = stringResource(id = R.string.student_list)) },
                modifier = Modifier
                    .width(300.dp),
            )

            Button(
                onClick = {

                    val currentDate = DateFormat
                        .getDateInstance()
                        .format(Date())

                    viewModel.batchInsert(
                        insertDate = currentDate,
                        group = groupNameText,
                        studentListString = studentListString
                    )

                    navigateBackToList()

                },
                modifier = Modifier.padding(vertical = 32.dp)

            ) {
                Text(text = stringResource(id = R.string.add))
            }
        }

    }

}

