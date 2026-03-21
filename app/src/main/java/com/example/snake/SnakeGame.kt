package com.example.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun SnakeGame(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scoreManager = remember { ScoreManager(context) }
    
    var gameState by remember { mutableStateOf(GameState()) }
    var lastDragX by remember { mutableStateOf(0f) }
    var lastDragY by remember { mutableStateOf(0f) }
    var hasMoved by remember { mutableStateOf(false) }
    var isGameOverSaved by remember { mutableStateOf(false) }
    
    // 边缘滑动返回
    var edgeSwipeStarted by remember { mutableStateOf(false) }
    var edgeSwipeDistance by remember { mutableStateOf(0f) }
    val edgeWidthPx = 120 // 边缘区域宽度（像素）
    val minSwipeDistance = 100 // 最小滑动距离才触发返回

    // 最小拖动距离阈值
    val minDragDistance = 30f

    // 游戏循环
    LaunchedEffect(Unit) {
        while (true) {
            delay(GameConfig.GAME_SPEED)
            gameState = moveSnake(gameState)
        }
    }

    // 保存分数
    LaunchedEffect(gameState.gameOver) {
        if (gameState.gameOver && !isGameOverSaved && gameState.score > 0) {
            scoreManager.saveScore(gameState.score)
            isGameOverSaved = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        // 检测是否从左边缘开始滑动
                        if (offset.x < edgeWidthPx) {
                            edgeSwipeStarted = true
                            edgeSwipeDistance = 0f
                        }
                    },
                    onDragEnd = {
                        // 只有从左边缘滑动且滑动距离超过阈值时才触发返回
                        if (edgeSwipeStarted && edgeSwipeDistance > minSwipeDistance) {
                            // 保存当前分数
                            if (gameState.score > 0 && !gameState.gameOver) {
                                scoreManager.saveScore(gameState.score)
                            }
                            onBack()
                        }
                        edgeSwipeStarted = false
                        edgeSwipeDistance = 0f
                    },
                    onDragCancel = {
                        edgeSwipeStarted = false
                        edgeSwipeDistance = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (edgeSwipeStarted) {
                            edgeSwipeDistance += abs(dragAmount)
                            change.consume()
                        }
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A4A6A)
                )
            ) {
                Text(stringResource(R.string.back))
            }
            
            Text(
                text = stringResource(R.string.score, gameState.score),
                color = Color(0xFFFFD700),
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 标题
        Text(
            text = stringResource(R.string.game_title),
            color = Color.White,
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 游戏画布
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                lastDragX = offset.x
                                lastDragY = offset.y
                                hasMoved = false
                            },
                            onDragEnd = {},
                            onDragCancel = {},
                            onDrag = { change, dragAmount ->
                                val deltaX = change.position.x - lastDragX
                                val deltaY = change.position.y - lastDragY
                                
                                // 只有移动超过阈值才改变方向
                                if (!hasMoved && (kotlin.math.abs(deltaX) > minDragDistance || kotlin.math.abs(deltaY) > minDragDistance)) {
                                    hasMoved = true
                                    val direction = if (kotlin.math.abs(deltaX) > kotlin.math.abs(deltaY)) {
                                        if (deltaX > 0) Direction.RIGHT else Direction.LEFT
                                    } else {
                                        if (deltaY > 0) Direction.DOWN else Direction.UP
                                    }
                                    gameState = changeDirection(gameState, direction)
                                }
                                
                                lastDragX = change.position.x
                                lastDragY = change.position.y
                                change.consume()
                            }
                        )
                    }
            ) {
                val cellSize = size.minDimension / GameConfig.GRID_SIZE

                // 绘制背景网格
                for (i in 0 until GameConfig.GRID_SIZE) {
                    for (j in 0 until GameConfig.GRID_SIZE) {
                        val color = if ((i + j) % 2 == 0) Color(0xFF16213E) else Color(0xFF1A1A2E)
                        drawRect(
                            color = color,
                            topLeft = Offset(i * cellSize, j * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }

                // 绘制食物
                val foodX = gameState.food.x.toFloat()
                val foodY = gameState.food.y.toFloat()
                drawRect(
                    color = Color(0xFFFF6B6B),
                    topLeft = Offset(
                        foodX * cellSize + cellSize * 0.1f,
                        foodY * cellSize + cellSize * 0.1f
                    ),
                    size = Size(cellSize * 0.8f, cellSize * 0.8f)
                )

                // 绘制蛇
                gameState.snake.forEachIndexed { index, pos ->
                    val color = if (index == 0) Color(0xFF00FF00) else Color(0xFF32CD32)
                    val posX = pos.x
                    val posY = pos.y
                    drawRect(
                        color = color,
                        topLeft = Offset(
                            posX * cellSize + cellSize * 0.05f,
                            posY * cellSize + cellSize * 0.05f
                        ),
                        size = Size(cellSize * 0.9f, cellSize * 0.9f)
                    )
                }
            }

            // 游戏结束遮罩
            if (gameState.gameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.game_over),
                            color = Color.Red,
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.final_score, gameState.score),
                            color = Color.White,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                isGameOverSaved = false
                                gameState = GameState()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF00)
                            )
                        ) {
                            Text(stringResource(R.string.restart), color = Color.Black)
                        }
                    }
                }
            }
        }

        // 操作提示
        Text(
            text = stringResource(R.string.swipe_hint),
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        // 重新开始按钮
        Button(
            onClick = { 
                isGameOverSaved = false
                gameState = GameState() 
            },
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A4A6A)
            )
        ) {
            Text(stringResource(R.string.restart))
        }
    }
}