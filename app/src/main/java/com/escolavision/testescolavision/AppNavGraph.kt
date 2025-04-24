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

        // Pantalla de perfil de usuario
        composable("profile_screen") { backStackEntry ->
            ProfileScreen(navController)
            // Manejo del botón de retroceso para volver a la pantalla inicial
            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            onBackPressedDispatcher?.addCallback {
                navController.navigate("first_screen") {
                    popUpTo("first_screen") { inclusive = true }
                }
            }
        }

        // Pantalla de resultados generales
        composable("results_screen") { backStackEntry ->
            ResultsScreen(navController)
        }

        // Pantalla de configuración
        composable("settings_screen") { backStackEntry ->
            SettingsScreen(navController)
        }

        // Pantalla de ayuda
        composable("help_screen") { backStackEntry ->
            HelpScreen(navController)
        }

        // Pantalla de información sobre la aplicación
        composable("about_screen") { backStackEntry ->
            AboutScreen(navController)
        }

        // Pantalla de detalle de test con parámetro ID
        composable("test_detail_screen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: 0
            TestDetailScreen(navController, id)
        }

        // Pantalla inicial de la aplicación
        composable("first_screen") {
            FirstScreen(navController)
        }

        // Pantalla de inicio de sesión
        composable("login_screen") {
            LoginScreen(navController)
        }

        // Pantalla de registro
        composable("register_screen") {
            RegisterScreen(navController)
        }

        // Pantalla de áreas de evaluación
        composable("areas_screen") { backStackEntry ->
            AreasScreen(navController)
        }
        
        // Pantalla de gestión de estudiantes
        composable("students_screen") { backStackEntry ->
            StudentsScreen(navController)
        }

        // Pantalla de resultados de test específico con parámetros
        composable("result_test_screen/{resultados}/{pantallaAnterior}") { backStackEntry ->
            // Procesa los resultados desde la URL y los convierte a lista de doubles
            val resultadosString = backStackEntry.arguments?.getString("resultados") ?: ""
            val resultados = resultadosString.split(";").mapNotNull { it.toDoubleOrNull() }
            val pantallaAnterior = backStackEntry.arguments?.getString("pantallaAnterior") ?: ""
            ResultTestScreen(navController, resultados, pantallaAnterior)
        }

        // Pantalla de gestión de centros educativos
        composable("centros_screen") { backStackEntry ->
            CentrosScreen(navController)
        }
    }
}
