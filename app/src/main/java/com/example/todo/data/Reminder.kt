package com.example.todo.data

import androidx.annotation.StringRes
import java.util.concurrent.TimeUnit

data class Reminder(
    @StringRes val durationRes: Int,
    val duration: Long,
    val unit: TimeUnit,
    val toDoTitle: String
)