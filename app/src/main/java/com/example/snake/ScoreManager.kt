package com.example.snake

import android.content.Context
import android.content.SharedPreferences

// 分数管理器 - 使用SharedPreferences存储最高分和历史记录
class ScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("snake_game", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_HISTORY = "history"
        private const val MAX_HISTORY_SIZE = 10
    }
    
    // 获取最高分
    fun getHighScore(): Int {
        return prefs.getInt(KEY_HIGH_SCORE, 0)
    }
    
    // 保存最高分
    fun saveHighScore(score: Int) {
        if (score > getHighScore()) {
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply()
        }
    }
    
    // 获取历史记录
    fun getHistory(): List<Int> {
        val historyString = prefs.getString(KEY_HISTORY, "") ?: ""
        return if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(",").mapNotNull { it.toIntOrNull() }
        }
    }
    
    // 保存分数到历史记录
    fun saveScore(score: Int) {
        // 更新最高分
        saveHighScore(score)
        
        // 更新历史记录
        val history = getHistory().toMutableList()
        history.add(score)
        history.sortDescending()
        
        // 只保留前10个
        val trimmedHistory = history.take(MAX_HISTORY_SIZE)
        
        prefs.edit().putString(KEY_HISTORY, trimmedHistory.joinToString(",")).apply()
    }
    
    // 清除历史记录
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}