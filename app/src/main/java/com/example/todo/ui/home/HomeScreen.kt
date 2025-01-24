package com.example.todo.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.R
import com.example.todo.ToDoTopAppBar
import com.example.todo.data.ToDo
import com.example.todo.ui.AppViewModelProvider
import com.example.todo.ui.navigation.NavigationDestination
import com.example.todo.ui.theme.ToDoTheme

object HomeDestination : NavigationDestination {
    override val route: String = "home"
    override val titleRes: Int = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToToDoEntry: () -> Unit,
    navigateToToDoUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ToDoTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToToDoEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.todo_entry_title)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            toDoList = homeUiState.toDoList,
            onToDoClick = navigateToToDoUpdate,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeBody(
    toDoList: List<ToDo>,
    onToDoClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (toDoList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_todo_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            ToDoList(
                toDoList = toDoList,
                onToDoClick = { onToDoClick(it.id) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun ToDoList(
    toDoList: List<ToDo>,
    onToDoClick: (ToDo) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(
            items = toDoList,
            key = { it.id }
        ) { toDo ->
            ToDoItem(
                toDo = toDo,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .clickable { onToDoClick(toDo) }
            )
        }
    }
}

@Composable
private fun ToDoItem(
    toDo: ToDo,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            Text(
                text = toDo.title,
                style = MaterialTheme.typography.titleLarge,
            )

            Text(
                text = toDo.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    ToDoTheme {
        HomeBody(
            toDoList = listOf(
                ToDo(1, "Lab", "Finish the Android Lab Finish the Android Lab"),
                ToDo(2, "Finals", "Finish the Android Final, it's important"),
                ToDo(3, "Vacation", "Plan where you want to go on vacation!")
            ),
            onToDoClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    ToDoTheme {
        HomeBody(
            toDoList = listOf(),
            onToDoClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoItemPreview() {
    ToDoTheme {
        ToDoItem(
            ToDo(1, "Lab", "Finish the Android Lab")
        )
    }
}