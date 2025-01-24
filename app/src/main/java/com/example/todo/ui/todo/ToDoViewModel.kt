package com.example.todo.ui.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.Reminder
import com.example.todo.data.ToDo
import com.example.todo.data.ToDosRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ToDoViewModel(
    private val toDosRepository: ToDosRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var toDoUiState by mutableStateOf(ToDoUiState())
        private set

    private val toDoId: Int = savedStateHandle.get<Int>("toDoId") ?: -1

    init {
        viewModelScope.launch {
            if (toDoId != -1) {
                toDosRepository.getToDoStream(toDoId).firstOrNull()?.let {
                    toDoUiState = it.toToDoUiState(isEntryValid = true)
                }
            } else {
                toDoUiState = ToDoUiState()
            }
        }
    }

    fun updateUiState(toDoDetails: ToDoDetails) {
        toDoUiState = ToDoUiState(
            toDoDetails = toDoDetails,
            isEntryValid = validateInput(toDoDetails)
        )
    }

    suspend fun saveOrUpdateItem() {
        if (toDoUiState.isEntryValid) {
            if (toDoId != -1) {
                toDosRepository.updateToDo(toDoUiState.toDoDetails.toToDo())
            } else {
                toDosRepository.insertToDo(toDoUiState.toDoDetails.toToDo())
            }
        }
    }

    suspend fun deleteItem() {
        if (toDoId != -1) {
            toDosRepository.deleteToDo(toDoUiState.toDoDetails.toToDo())
        }
    }

    private fun validateInput(uiState: ToDoDetails = toDoUiState.toDoDetails): Boolean {
        return uiState.title.isNotBlank() && uiState.description.isNotBlank()
    }

    fun scheduleReminder(reminder: Reminder) {
        toDosRepository.scheduleReminder(
            reminder.duration,
            reminder.unit,
            reminder.toDoTitle
        )
    }
}

data class ToDoUiState(
    val toDoDetails: ToDoDetails = ToDoDetails(),
    val isEntryValid: Boolean = false
)

data class ToDoDetails(
    val id: Int = 0,
    val title: String = "",
    val description: String = ""
)

fun ToDoDetails.toToDo(): ToDo = ToDo(
    id = id,
    title = title,
    description = description
)

fun ToDo.toToDoUiState(isEntryValid: Boolean = false): ToDoUiState = ToDoUiState(
    toDoDetails = this.toToDoDetails(),
    isEntryValid = isEntryValid
)

fun ToDo.toToDoDetails(): ToDoDetails = ToDoDetails(
    id = id,
    title = title,
    description = description
)
