package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.navigation.EvoroNavHost
import com.example.ui.theme.EvoroBlack
import com.example.ui.theme.EvoroTheme
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EvoroTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(EvoroBlack)
                ) {
                    EvoroNavHost(viewModel = viewModel)
                }
            }
        }
    }
}
