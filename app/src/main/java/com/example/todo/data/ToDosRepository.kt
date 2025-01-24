package com.example.todo.data

import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

interface ToDosRepository {

    // For the notifications
    fun scheduleReminder(duration: Long, unit: TimeUnit, toDoTitle: String)

    fun getAllToDosStream(): Flow<List<ToDo>>

    fun getToDoStream(id: Int): Flow<ToDo?>

    suspend fun insertToDo(toDo: ToDo)

    suspend fun updateToDo(toDo: ToDo)

    suspend fun deleteToDo(toDo: ToDo)
}