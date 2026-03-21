package com.example.snake

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 主菜单界面
@Composable
fun MainMenu(
    onStartGame: () -> Unit,
    onViewHistory: () -> Unit
) {
    val context = LocalContext.current
    val scoreManager = remember { ScoreManager(context) }
    val highScore = scoreManager.getHighScore()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 标题
        Text(
            text = "🐍 贪吃蛇",
            color = Color(0xFF00FF00),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 最高分显示
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF16213E)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "最高分",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Text(
                    text = "$highScore",
                    color = Color(0xFFFFD700),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 开始游戏按钮
        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FF00)
            )
        ) {
            Text(
                text = "开始游戏",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 查看历史记录按钮
        Button(
            onClick = onViewHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A4A6A)
            )
        ) {
            Text(
                text = "历史记录",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 历史记录界面
@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scoreManager = remember { ScoreManager(context) }
    val history = scoreManager.getHistory()
    val highScore = scoreManager.getHighScore()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp)
    ) {
        // 顶部导航
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A4A6A)
                )
            ) {
                Text("← 返回")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 标题
        Text(
            text = "🏆 历史记录",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 最高分
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF16213E)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "最高分",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Text(
                    text = "$highScore",
                    color = Color(0xFFFFD700),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 历史记录列表
        if (history.isEmpty()) {
            Text(
                text = "暂无游戏记录",
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "游戏记录",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(history) { index, score ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF16213E)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "第 ${index + 1} 名",
                                color = when (index) {
                                    0 -> Color(0xFFFFD700)
                                    1 -> Color(0xFFC0C0C0)
                                    2 -> Color(0xFFCD7F32)
                                    else -> Color.Gray
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$score 分",
                                color = Color(0xFF00FF00),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}