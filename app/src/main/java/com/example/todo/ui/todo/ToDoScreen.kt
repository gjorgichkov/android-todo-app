package com.example.todo.ui.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.FIVE_SECONDS
import com.example.todo.ONE_DAY
import com.example.todo.R
import com.example.todo.SEVEN_DAYS
import com.example.todo.THIRTY_DAYS
import com.example.todo.ToDoTopAppBar
import com.example.todo.data.Reminder
import com.example.todo.ui.AppViewModelProvider
import com.example.todo.ui.navigation.NavigationDestination
import com.example.todo.ui.theme.ToDoTheme
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object ToDoScreenDestination : NavigationDestination {
    override val route: String = "todo_screen"
    override val titleRes: Int = R.string.app_name
    const val toDoIdArg = "toDoId"
    val routeWithArgs = "$route/{${toDoIdArg}}"

    val titleCreateRes = R.string.todo_entry_title  // When creating a new task
    val titleEditRes = R.string.edit_todo_title  // When editing an existing task
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    toDoId: Int? = null,
    viewModel: ToDoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var showReminderDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ToDoTopAppBar(
                title = stringResource(if (toDoId != null) ToDoScreenDestination.titleEditRes else ToDoScreenDestination.titleCreateRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
        ) {
            ToDoInputForm(
                toDoDetails = viewModel.toDoUiState.toDoDetails,
                onValueChange = viewModel::updateUiState,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveOrUpdateItem()
                        navigateBack()
                    }
                },
                enabled = viewModel.toDoUiState.isEntryValid,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.save_action))
            }
            if (toDoId != null) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.deleteItem()
                            navigateBack()
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.delete))
                }
                Button(
                    onClick = {
                        showReminderDialog = true
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.set_reminder))
                }
            }
            if (showReminderDialog) {
                ReminderDialogContent(
                    onDialogDismiss = { showReminderDialog = false },
                    toDoTitle = viewModel.toDoUiState.toDoDetails.title,
                    onScheduleReminder = { viewModel.scheduleReminder(it) }
                )
            }
        }
    }
}

@Composable
fun ToDoInputForm(
    toDoDetails: ToDoDetails,
    onValueChange: (ToDoDetails) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = toDoDetails.title,
            onValueChange = { onValueChange(toDoDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.todo_title_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = toDoDetails.description,
            onValueChange = { onValueChange(toDoDetails.copy(description = it)) },
            label = { Text(stringResource(R.string.todo_desc_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
    }
}

@Composable
fun ReminderDialogContent(
    onDialogDismiss: () -> Unit,
    toDoTitle: String,
    onScheduleReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminders = listOf(
        Reminder(R.string.five_seconds, FIVE_SECONDS, TimeUnit.SECONDS, toDoTitle),
        Reminder(R.string.one_day, ONE_DAY, TimeUnit.DAYS, toDoTitle),
        Reminder(R.string.one_week, SEVEN_DAYS, TimeUnit.DAYS, toDoTitle),
        Reminder(R.string.one_month, THIRTY_DAYS, TimeUnit.DAYS, toDoTitle)
    )

    AlertDialog(
        onDismissRequest = { onDialogDismiss() },
        confirmButton = {},
        title = { Text(stringResource(R.string.remind_me, toDoTitle)) },
        text = {
            Column {
                reminders.forEach {
                    Text(
                        text = stringResource(it.durationRes),
                        modifier = Modifier
                            .clickable {
                                onScheduleReminder(it)
                                onDialogDismiss()
                            }
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        },
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun ToDoScreenPreview() {
    ToDoTheme {
        ToDoScreen(
            navigateBack = { /* Do nothing */ },
            onNavigateUp = { /* Do nothing */ },
            toDoId = null  // Preview as a create new task scenario
        )
    }
}