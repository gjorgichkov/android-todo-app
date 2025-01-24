package com.example.todo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todo.ui.home.HomeDestination
import com.example.todo.ui.home.HomeScreen
import com.example.todo.ui.todo.ToDoScreen
import com.example.todo.ui.todo.ToDoScreenDestination

@Composable
fun ToDoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToToDoEntry = { navController.navigate("${ToDoScreenDestination.route}/-1") },
                navigateToToDoUpdate = {
                    navController.navigate("${ToDoScreenDestination.route}/$it")
                }
            )
        }

        composable(
            route = ToDoScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(ToDoScreenDestination.toDoIdArg) {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val toDoId = backStackEntry.arguments?.getInt(ToDoScreenDestination.toDoIdArg)
            ToDoScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                toDoId = if (toDoId != -1) toDoId else null
            )
        }
    }
}