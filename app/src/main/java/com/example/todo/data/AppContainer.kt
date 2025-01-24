package com.example.todo.data

import android.content.Context

interface AppContainer {
    val toDosRepository: ToDosRepository
}

class AppDataContainer(
    private val context: Context
) : AppContainer {
    override val toDosRepository: ToDosRepository by lazy {
        OfflineToDosRepository(
            context = context,
            toDoDao = ToDoDatabase.getDatabase(context).toDoDao()
        )
    }
}