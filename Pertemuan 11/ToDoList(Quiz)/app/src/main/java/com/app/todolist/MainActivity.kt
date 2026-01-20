package com.app.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.todolist.data.model.GoogleAuthUiClient
import com.app.todolist.presentation.sign_in.SignInScreen
import com.app.todolist.presentation.sign_in.SignInViewModel
import com.app.todolist.presentation.todo.*
import com.app.todolist.ui.theme.ToDoListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy { GoogleAuthUiClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val todoViewModel = viewModel<TodoViewModel>()

                    // [UBAH] Menambahkan properti animasi transisi
                    // Note: Membutuhkan navigation-compose versi 2.7.0+
                    NavHost(
                        navController = navController,
                        startDestination = "sign_in",
                        // [BARU] Animasi Masuk (Slide dari kiri)
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            ) + fadeIn(animationSpec = tween(500))
                        },
                        // [BARU] Animasi Keluar
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            ) + fadeOut(animationSpec = tween(500))
                        },
                        // [BARU] Animasi Back Masuk
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            ) + fadeIn(animationSpec = tween(500))
                        },
                        // [BARU] Animasi Back Keluar
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            ) + fadeOut(animationSpec = tween(500))
                        }
                    ) {
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("todo_list") {
                                        popUpTo("sign_in") {
                                            inclusive = true
                                        }
                                    }
                                }
                            }

                            LaunchedEffect(state.isSignInSuccessfull) {
                                if (state.isSignInSuccessfull) {
                                    navController.navigate("todo_list") {
                                        popUpTo("sign_in") {
                                            inclusive = true
                                        }
                                    }
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(state = state, onSignInClick = {
                                lifecycleScope.launch {
                                    val result = googleAuthUiClient.signIn()
                                    viewModel.onSignInResult(result)
                                }
                            })
                        }
                        composable("todo_list") {
                            TodoScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                viewModel = todoViewModel,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        navController.navigate("sign_in") {
                                            popUpTo("todo_list") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                },
                                onNavigateToEdit = { todoId -> navController.navigate("edit_todo/$todoId") }
                            )
                        }

                        composable(
                            route = "edit_todo/{todoId}",
                            arguments = listOf(navArgument("todoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val todoId = backStackEntry.arguments?.getString("todoId") ?: ""
                            val todos by todoViewModel.todos.collectAsState()
                            val todo = todos.find { it.id == todoId }
                            val userId = googleAuthUiClient.getSignedInUser()?.userId ?: ""

                            todo?.let {
                                EditTodoScreen(
                                    todo = it,
                                    // [UBAH DI SINI] Menerima title, priority, DAN category
                                    onSave = { newTitle, newPriority, newCategory ->
                                        todoViewModel.updateTitle(userId, todoId, newTitle)
                                        todoViewModel.updatePriority(userId, todoId, newPriority)
                                        todoViewModel.updateCategory(userId, todoId, newCategory) // Fungsi baru
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}