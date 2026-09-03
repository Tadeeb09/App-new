package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TermsOfServiceScreen
import com.example.ui.viewmodel.ChatViewModel

object EvoroDestinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val CHAT = "chat"
    const val SETTINGS = "settings"
    const val ACCOUNT = "account"
    const val PRIVACY_POLICY = "privacy_policy"
    const val TERMS_OF_SERVICE = "terms_of_service"
}

@Composable
fun EvoroNavHost(
    viewModel: ChatViewModel,
    navController: NavHostController = rememberNavController()
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val imageOptions by viewModel.imageOptions.collectAsState()

    NavHost(
        navController = navController,
        startDestination = EvoroDestinations.SPLASH,
        enterTransition = { fadeIn(animationSpec = tween(250)) },
        exitTransition = { fadeOut(animationSpec = tween(250)) }
    ) {
        composable(EvoroDestinations.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    if (viewModel.isOnboardingCompleted()) {
                        navController.navigate(EvoroDestinations.CHAT) {
                            popUpTo(EvoroDestinations.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(EvoroDestinations.ONBOARDING) {
                            popUpTo(EvoroDestinations.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(EvoroDestinations.ONBOARDING) {
            OnboardingScreen(
                onFinishOnboarding = {
                    viewModel.setOnboardingCompleted()
                    navController.navigate(EvoroDestinations.CHAT) {
                        popUpTo(EvoroDestinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(EvoroDestinations.CHAT) {
            ChatScreen(
                viewModel = viewModel,
                onOpenSettings = {
                    navController.navigate(EvoroDestinations.SETTINGS)
                },
                onOpenAccount = {
                    navController.navigate(EvoroDestinations.ACCOUNT)
                }
            )
        }

        composable(
            route = EvoroDestinations.SETTINGS,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            SettingsScreen(
                currentModel = selectedModel,
                onSelectModel = { viewModel.setSelectedModel(it) },
                imageOptions = imageOptions,
                onUpdateImageOptions = { viewModel.setImageOptions(it) },
                onClearAllChats = { viewModel.clearAllConversations() },
                onOpenAccount = { navController.navigate(EvoroDestinations.ACCOUNT) },
                onOpenPrivacyPolicy = { navController.navigate(EvoroDestinations.PRIVACY_POLICY) },
                onOpenTermsOfService = { navController.navigate(EvoroDestinations.TERMS_OF_SERVICE) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = EvoroDestinations.ACCOUNT,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            AccountScreen(
                currentAccount = userAccount,
                onLogin = { email, name ->
                    viewModel.loginUser(email, name)
                },
                onLogout = {
                    viewModel.logoutUser()
                },
                onDeleteAccount = {
                    viewModel.deleteAccount()
                    navController.navigate(EvoroDestinations.CHAT) {
                        popUpTo(EvoroDestinations.CHAT) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = EvoroDestinations.PRIVACY_POLICY,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = EvoroDestinations.TERMS_OF_SERVICE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            TermsOfServiceScreen(onBack = { navController.popBackStack() })
        }
    }
}
