package com.simats.smartcart

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simats.smartcart.ui.screens.AiReviewAnalyzerScreen
import com.simats.smartcart.ui.screens.DiscoverScreen
import com.simats.smartcart.ui.screens.EditProfileScreen
import com.simats.smartcart.ui.screens.ForgotPasswordScreen
import com.simats.smartcart.ui.screens.FullProductOverviewScreen
import com.simats.smartcart.ui.screens.HomeScreen
import com.simats.smartcart.ui.screens.LoginScreen
import com.simats.smartcart.ui.screens.OnboardingScreen
import com.simats.smartcart.ui.screens.PreferencesScreen
import com.simats.smartcart.ui.screens.PriceTrackingScreen
import com.simats.smartcart.ui.screens.ProductDetailScreen
import com.simats.smartcart.ui.screens.ProfileScreen
import com.simats.smartcart.ui.screens.SearchResultsScreen
import com.simats.smartcart.ui.screens.SearchScreen
import com.simats.smartcart.ui.screens.SignUpScreen
import com.simats.smartcart.ui.screens.SavedDecisionsScreen
import com.simats.smartcart.ui.screens.SmartComparisonScreen
import com.simats.smartcart.ui.screens.SmarterChoiceScreen
import com.simats.smartcart.ui.screens.SplashScreen
import com.simats.smartcart.ui.screens.UiKitScreen
import com.simats.smartcart.ui.screens.WhyThisFitsScreen
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
                                },
                                onSignUpClick = {
                                    navController.navigate("signup")
                                },
                                onForgotPasswordClick = {
                                    navController.navigate("forgot_password")
                                }
                            )
                        }
                        composable("forgot_password") {
                            ForgotPasswordScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onResetSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("forgot_password") { inclusive = true }
                                    }
                                },
                                onLoginClick = {
                                    navController.navigate("login") {
                                        popUpTo("forgot_password") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onLoginClick = {
                                    navController.navigate("login") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                },
                                onSignUpSuccess = { _ ->
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                onTabSelected = { tab ->
                                    when (tab) {
                                        1 -> {
                                            navController.navigate("discover")
                                        }
                                        2 -> {
                                            navController.navigate("compare")
                                        }
                                        3 -> {
                                            navController.navigate("saved_decisions")
                                        }
                                        4 -> {
                                            navController.navigate("profile")
                                        }
                                    }
                                },
                                onSearchClick = {
                                    navController.navigate("search")
                                },
                                onProductClick = { name ->
                                    navController.navigate("product_detail/${Uri.encode(name)}")
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                }
                            )
                        }
                        composable("discover") {
                            DiscoverScreen(
                                onTabSelected = { tab ->
                                    when (tab) {
                                        0 -> {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                        2 -> {
                                            navController.navigate("compare")
                                        }
                                        3 -> {
                                            navController.navigate("saved_decisions")
                                        }
                                        4 -> {
                                            navController.navigate("profile")
                                        }
                                    }
                                },
                                onProductClick = { name ->
                                    navController.navigate("product_detail/${Uri.encode(name)}")
                                },
                                onCompareSelectedClick = { prod1, prod2 ->
                                    navController.navigate("compare?prod1=${Uri.encode(prod1)}&prod2=${Uri.encode(prod2)}")
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
                                },
                                onProductClick = { name ->
                                    navController.navigate("product_detail/${Uri.encode(name)}")
                                }
                            )
                        }
                        composable(
                            route = "product_detail/{productName}",
                            arguments = listOf(navArgument("productName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productName = backStackEntry.arguments?.getString("productName") ?: "JBL Tune 510BT"
                            ProductDetailScreen(
                                productName = productName,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onViewDetailsClick = { name ->
                                    navController.navigate("full_product_overview/${Uri.encode(name)}")
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                }
                            )
                        }
                        composable("product_detail") {
                            ProductDetailScreen(
                                productName = "JBL Tune 510BT",
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onViewDetailsClick = { name ->
                                    navController.navigate("full_product_overview/${Uri.encode(name)}")
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                }
                            )
                        }
                        composable(
                            route = "full_product_overview/{productName}",
                            arguments = listOf(navArgument("productName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productName = backStackEntry.arguments?.getString("productName") ?: "JBL Tune 510BT"
                            FullProductOverviewScreen(
                                productName = productName,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                },
                                onWhyItFitsClick = {
                                    navController.navigate("why_this_fits/${Uri.encode(productName)}")
                                }
                            )
                        }
                        composable("full_product_overview") {
                            FullProductOverviewScreen(
                                productName = "JBL Tune 510BT",
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                },
                                onWhyItFitsClick = {
                                    navController.navigate("why_this_fits")
                                }
                            )
                        }
                        composable(
                            route = "why_this_fits/{productName}",
                            arguments = listOf(navArgument("productName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productName = backStackEntry.arguments?.getString("productName") ?: "JBL Tune 510BT"
                            WhyThisFitsScreen(
                                productName = productName,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                },
                                onAlternativeClick = { altName ->
                                    navController.navigate("product_detail/${Uri.encode(altName)}")
                                }
                            )
                        }
                        composable("why_this_fits") {
                            WhyThisFitsScreen(
                                productName = "JBL Tune 510BT",
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                },
                                onAlternativeClick = { altName ->
                                    navController.navigate("product_detail/${Uri.encode(altName)}")
                                }
                            )
                        }
                        composable(
                            route = "compare?prod1={prod1}&prod2={prod2}",
                            arguments = listOf(
                                navArgument("prod1") { type = NavType.StringType; defaultValue = "JBL Tune 510BT" },
                                navArgument("prod2") { type = NavType.StringType; defaultValue = "Soundcore H30i" }
                            )
                        ) { backStackEntry ->
                            val prod1 = backStackEntry.arguments?.getString("prod1") ?: "JBL Tune 510BT"
                            val prod2 = backStackEntry.arguments?.getString("prod2") ?: "Soundcore H30i"
                            SmartComparisonScreen(
                                product1Name = prod1,
                                product2Name = prod2,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onTabSelected = { tab ->
                                    when (tab) {
                                        0 -> {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                        1 -> {
                                            navController.navigate("discover")
                                        }
                                        3 -> {
                                            navController.navigate("saved_decisions")
                                        }
                                        4 -> {
                                            navController.navigate("profile")
                                        }
                                    }
                                },
                                onSeeSummaryClick = { p1: String, p2: String ->
                                    navController.navigate("smarter_choice?prod1=${Uri.encode(p1)}&prod2=${Uri.encode(p2)}")
                                }
                            )
                        }
                        composable(
                            route = "smarter_choice?prod1={prod1}&prod2={prod2}",
                            arguments = listOf(
                                navArgument("prod1") { type = NavType.StringType; defaultValue = "JBL Tune 510BT" },
                                navArgument("prod2") { type = NavType.StringType; defaultValue = "Soundcore H30i" }
                            )
                        ) { backStackEntry ->
                            val prod1 = backStackEntry.arguments?.getString("prod1") ?: "JBL Tune 510BT"
                            val prod2 = backStackEntry.arguments?.getString("prod2") ?: "Soundcore H30i"
                            SmarterChoiceScreen(
                                product1Name = prod1,
                                product2Name = prod2,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onTabSelected = { tab ->
                                    when (tab) {
                                        0 -> {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                        1 -> {
                                            navController.navigate("discover")
                                        }
                                        3 -> {
                                            navController.navigate("saved_decisions")
                                        }
                                        4 -> {
                                            navController.navigate("profile")
                                        }
                                    }
                                },
                                onViewDetailsClick = { name ->
                                    navController.navigate("ai_review_analyzer/${Uri.encode(name)}")
                                },
                                onSaveComparisonClick = {
                                    navController.navigate("saved_decisions")
                                }
                            )
                        }
                        composable("saved_decisions") {
                            SavedDecisionsScreen(
                                onTabSelected = { tab ->
                                    when (tab) {
                                        0 -> {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                        1 -> {
                                            navController.navigate("discover")
                                        }
                                        2 -> {
                                            navController.navigate("compare")
                                        }
                                        4 -> {
                                            navController.navigate("profile")
                                        }
                                    }
                                },
                                onCompareClick = {
                                    navController.navigate("compare")
                                },
                                onOpenComparisonClick = {
                                    navController.navigate("compare")
                                },
                                onPriceDropClick = { name ->
                                    navController.navigate("price_tracking/${Uri.encode(name)}")
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                onTabSelected = { tab ->
                                    when (tab) {
                                        0 -> {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                        1 -> {
                                            navController.navigate("discover")
                                        }
                                        2 -> {
                                            navController.navigate("compare")
                                        }
                                        3 -> {
                                            navController.navigate("saved_decisions")
                                        }
                                    }
                                },
                                onShoppingHistoryClick = {
                                    navController.navigate("discover")
                                },
                                onSavedComparisonsClick = {
                                    navController.navigate("saved_decisions")
                                },
                                onEditPreferencesClick = {
                                    navController.navigate("preferences")
                                },
                                onPrivacyAndDataClick = {
                                    navController.navigate("ui_kit")
                                },
                                onEditProfileClick = {
                                    navController.navigate("edit_profile")
                                }
                            )
                        }
                        composable("edit_profile") {
                            EditProfileScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSaveSuccess = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("preferences") {
                            PreferencesScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSavePreferencesClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("ui_kit") {
                            UiKitScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "price_tracking/{productName}",
                            arguments = listOf(navArgument("productName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productName = backStackEntry.arguments?.getString("productName") ?: "JBL Tune 510BT"
                            PriceTrackingScreen(
                                productName = productName,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSaveTargetPriceClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("price_tracking") {
                            PriceTrackingScreen(
                                productName = "JBL Tune 510BT",
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSaveTargetPriceClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "ai_review_analyzer/{productName}",
                            arguments = listOf(navArgument("productName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productName = backStackEntry.arguments?.getString("productName") ?: "JBL Tune 510BT"
                            AiReviewAnalyzerScreen(
                                productName = productName,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("ai_review_analyzer") {
                            AiReviewAnalyzerScreen(
                                productName = "JBL Tune 510BT",
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
