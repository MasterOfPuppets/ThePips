package com.masterofpuppets.thepips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.masterofpuppets.thepips.core.navigation.MainScreen
import com.masterofpuppets.thepips.ui.theme.ThePipsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThePipsTheme {
                MainScreen()
            }
        }
    }
}