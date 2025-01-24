package com.example.todo.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todo.ToDoApplication
import com.example.todo.ui.home.HomeViewModel
import com.example.todo.ui.todo.ToDoViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(toDoApplication().container.toDosRepository)
        }

        initializer {
            ToDoViewModel(
                toDoApplication().container.toDosRepository,
                this.createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.toDoApplication(): ToDoApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ToDoApplication)