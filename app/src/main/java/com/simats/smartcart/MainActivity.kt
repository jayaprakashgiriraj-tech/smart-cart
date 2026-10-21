package com.simats.smartcart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simats.smartcart.ui.screens.DiscoverScreen
import com.simats.smartcart.ui.screens.HomeScreen
import com.simats.smartcart.ui.screens.LoginScreen
import com.simats.smartcart.ui.screens.OnboardingScreen
import com.simats.smartcart.ui.screens.SearchResultsScreen
import com.simats.smartcart.ui.screens.SearchScreen
import com.simats.smartcart.ui.screens.SplashScreen
import com.simats.smartcart.ui.theme.SMARTCARTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMARTCARTTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "onboarding", // Starting at onboarding for easy preview/testing
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen()
                        }
                        composable("onboarding") {
                            OnboardingScreen(
                                onNextClick = {
                                    navController.navigate("login") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                },
                                onSkipClick = {
                                    navController.navigate("login") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginClick = { _, _ ->
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                onTabSelected = { tab ->
                                    if (tab == 1) {
                                        navController.navigate("discover")
                                    }
                                },
                                onSearchClick = {
                                    navController.navigate("search")
                                }
                            )
                        }
                        composable("discover") {
                            DiscoverScreen(
                                onTabSelected = { tab ->
                                    if (tab == 0) {
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                        composable("search") {
                            SearchScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onFindMatchesClick = {
                                    navController.navigate("search_results")
                                }
                            )
                        }
                        composable("search_results") {
                            SearchResultsScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSeeFullRecommendationsClick = {
                                    navController.navigate("discover")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
