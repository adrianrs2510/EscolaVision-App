package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R


// Componente del menú lateral que proporciona navegación principal de la aplicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDrawer(
    navController: NavController,      // Controlador de navegación
    id: Int,                          // ID del usuario actual
    tipo: String,                     // Tipo de usuario (Alumno, Profesor, invitado)
    scope: CoroutineScope,            // Scope para operaciones coroutine
    drawerState: DrawerState,         // Estado del drawer
    preferencesManager: PreferencesManager // Gestor de preferencias
) {
    // Configuración del panel lateral
    ModalDrawerSheet(
        modifier = Modifier
            .width(250.dp)
            .background(colorResource(id = R.color.fondoInicio))
    ) {
        // Contenedor principal de opciones del menú
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(colorResource(id = R.color.fondoInicio))
                .padding(16.dp)
        ) {
            // Opción de Tests - Solo visible para alumnos, invitados y orientadores
            if (tipo == "Alumno" || tipo == "invitado" || preferencesManager.getIsOrientador() == 1) {
                MenuButton("Tests") {
                    navController.navigate("home_screen")
                    scope.launch { drawerState.close() }
                }
            }

            // Opción de Áreas - Visible para todos los usuarios
            MenuButton("Áreas") {
                navController.navigate("areas_screen")
                scope.launch { drawerState.close() }
            }

            // Opción de Alumnos - Solo visible para profesores
            if (tipo == "Profesor") {
                MenuButton("Alumnos") {
                    navController.navigate("students_screen")
                    scope.launch { drawerState.close() }
                }
            }

            // Opción de Perfil - Visible para todos los usuarios
            MenuButton("Perfil") {
                navController.navigate("profile_screen")
                scope.launch { drawerState.close() }
            }

            // Opción de Mi Centro - Visible para todos los usuarios
            MenuButton("Mi Centro") {
                navController.navigate("centros_screen")
                scope.launch { drawerState.close() }
            }

            // Opción de Resultados - No visible para usuarios invitados
            if(tipo != "invitado"){
                MenuButton("Resultados") {
                    navController.navigate("results_screen")
                    scope.launch { drawerState.close() }
                }
            }

            // Opción de Ayuda - Visible para todos los usuarios
            MenuButton("Ayuda") {
                navController.navigate("help_screen")
                scope.launch { drawerState.close() }
            }

            // Espaciador flexible para empujar el botón de cerrar sesión al fondo
            Spacer(modifier = Modifier.weight(1f))

            // Botón de cerrar sesión
            Button(
                onClick = {
                    preferencesManager.clearLogin()
                    navController.navigate("first_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// Componente reutilizable para botones del menú
@Composable
fun MenuButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
