package com.escolavision.testescolavision

import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.escolavision.testescolavision.Screens.AboutScreen
import com.escolavision.testescolavision.Screens.AreasScreen
import com.escolavision.testescolavision.Screens.CentrosScreen
import com.escolavision.testescolavision.Screens.FirstScreen
import com.escolavision.testescolavision.Screens.HelpScreen
import com.escolavision.testescolavision.Screens.HomeScreen
import com.escolavision.testescolavision.Screens.LoginScreen
import com.escolavision.testescolavision.Screens.ProfileScreen
import com.escolavision.testescolavision.Screens.RegisterScreen
import com.escolavision.testescolavision.Screens.ResultTestScreen
import com.escolavision.testescolavision.Screens.ResultsScreen
import com.escolavision.testescolavision.Screens.SettingsScreen
import com.escolavision.testescolavision.Screens.StudentsScreen
import com.escolavision.testescolavision.Screens.TestDetailScreen

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "first_screen") {
        composable("home_screen") { backStackEntry ->
            HomeScreen(navController)

            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            onBackPressedDispatcher?.addCallback {
                navController.navigate("first_screen") {
                    popUpTo("first_screen") { inclusive = true }
                }
            }
        }
        composable("profile_screen") { backStackEntry ->
            ProfileScreen(navController)
            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            onBackPressedDispatcher?.addCallback {
                navController.navigate("first_screen") {
                    popUpTo("first_screen") { inclusive = true }
                }
            }
        }
        composable("results_screen") { backStackEntry ->
            ResultsScreen(navController)
        }
        composable("settings_screen") { backStackEntry ->
            SettingsScreen(navController)
        }
        composable("help_screen") { backStackEntry ->
            HelpScreen(navController)
        }
        composable("about_screen") { backStackEntry ->
            AboutScreen(navController)
        }

        composable("test_detail_screen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: 0
            TestDetailScreen(navController, id)
        }

        composable("first_screen") {
            FirstScreen(navController)
        }

        composable("login_screen") {
            LoginScreen(navController)
        }

        composable("register_screen") {
            RegisterScreen(navController)
        }

        composable("areas_screen") { backStackEntry ->
            AreasScreen(navController)
        }
        
        composable("students_screen") { backStackEntry ->
            StudentsScreen(navController)
        }

        composable("result_test_screen/{resultados}/{pantallaAnterior}") { backStackEntry ->
            val resultadosString = backStackEntry.arguments?.getString("resultados") ?: ""
            val resultados = resultadosString.split(";").mapNotNull { it.toDoubleOrNull() }
            val pantallaAnterior = backStackEntry.arguments?.getString("pantallaAnterior") ?: ""
            ResultTestScreen(navController, resultados, pantallaAnterior)
        }

        composable("centros_screen") { backStackEntry ->
            CentrosScreen(navController)
        }

    }
}
