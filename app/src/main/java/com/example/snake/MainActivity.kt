package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
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
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }
            
            DisposableEffect(Unit) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        when (currentScreen) {
                            is Screen.Game, is Screen.History -> {
                                currentScreen = Screen.Menu
                            }
                            else -> {
                                finish()
                            }
                        }
                    }
                }
                onBackPressedDispatcher.addCallback(callback)
                onDispose { }
            }
            
            SnakeTheme {
                when (currentScreen) {
                    is Screen.Menu -> MainMenu(
                        onStartGame = { currentScreen = Screen.Game },
                        onViewHistory = { currentScreen = Screen.History }
                    )
                    is Screen.Game -> SnakeGame(
                        onBack = { 
                            currentScreen = Screen.Menu 
                        }
                    )
                    is Screen.History -> HistoryScreen(
                        onBack = { currentScreen = Screen.Menu }
                    )
                }
            }
        }
    }
}
