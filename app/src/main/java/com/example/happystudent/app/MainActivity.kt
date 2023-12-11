package com.example.happystudent.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.happystudent.core.theme.HappyStudentTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HappyStudentTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                HappyStudentNavHost(this)
            }
        }
    }
}

