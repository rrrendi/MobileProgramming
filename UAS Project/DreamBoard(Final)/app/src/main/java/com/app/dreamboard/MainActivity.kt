package com.app.dreamboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.dreamboard.data.model.GoogleAuthUiClient
import com.app.dreamboard.presentation.dreamboard.DreamViewModel
import com.app.dreamboard.presentation.dreamboard.HomeScreen
import com.app.dreamboard.presentation.sign_in.SignInScreen
import com.app.dreamboard.presentation.sign_in.SignInViewModel
import com.app.dreamboard.ui.theme.DreamBoardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy { GoogleAuthUiClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dreamViewModel = viewModel<DreamViewModel>()
            // Ambil tema dari ViewModel (Device Storage)
            val isDarkTheme by dreamViewModel.isDarkTheme.collectAsStateWithLifecycle()

            DreamBoardTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController, startDestination = "splash",
                        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) + fadeIn(tween(500)) },
                        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) + fadeOut(tween(500)) },
                        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) + fadeIn(tween(500)) },
                        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) + fadeOut(tween(500)) }
                    ) {
                        composable("splash") {
                            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                                Image(painter = painterResource(id = R.drawable.logo_apk), contentDescription = null, modifier = Modifier.size(150.dp))
                            }
                            LaunchedEffect(Unit) {
                                delay(2000)
                                val dest = if (googleAuthUiClient.getSignedInUser() != null) "home" else "sign_in"
                                navController.navigate(dest) { popUpTo("splash") { inclusive = true } }
                            }
                        }
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            LaunchedEffect(state.isSignInSuccessfull) {
                                if (state.isSignInSuccessfull) {
                                    dreamViewModel.loadDreams()
                                    navController.navigate("home") { popUpTo("sign_in") { inclusive = true } }
                                    viewModel.resetState()
                                }
                            }
                            SignInScreen(state = state, onSignInClick = { lifecycleScope.launch { viewModel.setLoading(true); viewModel.onSignInResult(googleAuthUiClient.signIn()) } })
                        }
                        composable("home") {
                            HomeScreen(navController = navController, viewModel = dreamViewModel, isDarkTheme = isDarkTheme, onThemeChange = { dreamViewModel.toggleTheme(it) })
                        }
                    }
                }
            }
        }
    }
}