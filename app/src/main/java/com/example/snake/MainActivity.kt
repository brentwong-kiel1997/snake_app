package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.snake.ui.theme.SnakeTheme

// 屏幕类型
sealed class Screen {
    object Menu : Screen()
    object Game : Screen()
    object History : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnakeTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }

                when (currentScreen) {
                    is Screen.Menu -> MainMenu(
                        onStartGame = { currentScreen = Screen.Game },
                        onViewHistory = { currentScreen = Screen.History }
                    )
                    is Screen.Game -> SnakeGame(
                        onBack = { currentScreen = Screen.Menu }
                    )
                    is Screen.History -> HistoryScreen(
                        onBack = { currentScreen = Screen.Menu }
                    )
                }
            }
        }
    }
}
