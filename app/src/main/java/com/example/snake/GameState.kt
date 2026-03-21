package com.example.snake

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

// 食物位置
data class Food(val x: Int, val y: Int)

// 蛇的方向
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

// 游戏状态
data class GameState(
    val snake: List<Offset> = listOf(
        Offset(5f, 5f),
        Offset(4f, 5f),
        Offset(3f, 5f)
    ),
    val food: Food = Food(10, 10),
    val direction: Direction = Direction.RIGHT,
    val score: Int = 0,
    val gameOver: Boolean = false,
    val isPaused: Boolean = false
)

// 游戏配置
object GameConfig {
    const val GRID_SIZE = 20
    const val CELL_SIZE = 40
    const val GAME_SPEED = 150L
    const val INITIAL_SNAKE_LENGTH = 3
}

// 生成随机食物位置
fun generateFood(snake: List<Offset>): Food {
    val random = Random
    var food: Food
    do {
        food = Food(
            random.nextInt(GameConfig.GRID_SIZE),
            random.nextInt(GameConfig.GRID_SIZE)
        )
    } while (snake.any { it.x.toInt() == food.x && it.y.toInt() == food.y })
    return food
}

// 移动蛇
fun moveSnake(state: GameState): GameState {
    if (state.gameOver || state.isPaused) return state

    val head = state.snake.first()
    val newHead = when (state.direction) {
        Direction.UP -> Offset(head.x, head.y - 1)
        Direction.DOWN -> Offset(head.x, head.y + 1)
        Direction.LEFT -> Offset(head.x - 1, head.y)
        Direction.RIGHT -> Offset(head.x + 1, head.y)
    }

    // 检查碰撞
    val hitWall = newHead.x < 0 || newHead.x >= GameConfig.GRID_SIZE ||
                  newHead.y < 0 || newHead.y >= GameConfig.GRID_SIZE
    val hitSelf = state.snake.drop(1).any { it.x == newHead.x && it.y == newHead.y }

    if (hitWall || hitSelf) {
        return state.copy(gameOver = true)
    }

    val newSnake = mutableListOf(newHead)
    newSnake.addAll(state.snake)

    // 检查是否吃到食物
    return if (newHead.x.toInt() == state.food.x && newHead.y.toInt() == state.food.y) {
        // 吃到食物：添加新头部，不删除尾部（蛇变长）
        state.copy(
            snake = newSnake,
            food = generateFood(newSnake),
            score = state.score + 10
        )
    } else {
        // 没吃到食物：添加新头部，删除尾部（蛇长度不变）
        newSnake.removeAt(newSnake.lastIndex)
        state.copy(snake = newSnake)
    }
}

// 改变方向（防止直接反向）
fun changeDirection(state: GameState, newDirection: Direction): GameState {
    val currentDir = state.direction
    val isOpposite = when (currentDir) {
        Direction.UP -> newDirection == Direction.DOWN
        Direction.DOWN -> newDirection == Direction.UP
        Direction.LEFT -> newDirection == Direction.RIGHT
        Direction.RIGHT -> newDirection == Direction.LEFT
    }
    return if (isOpposite) state else state.copy(direction = newDirection)
}