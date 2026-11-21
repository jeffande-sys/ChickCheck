package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.CluckCheckViewModel
import com.example.ui.navigation.CluckNavHost
import com.example.ui.theme.CluckCheckTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CluckCheckViewModel by viewModels {
        CluckCheckViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            CluckCheckTheme {
                CluckNavHost(viewModel = viewModel)
            }
        }
    }
}
