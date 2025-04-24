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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDrawer(
    navController: NavController,
    id: Int,
    tipo: String,
    scope: CoroutineScope,
    drawerState: DrawerState,
    preferencesManager: PreferencesManager
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(250.dp)
            .background(colorResource(id = R.color.fondoInicio))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(colorResource(id = R.color.fondoInicio))
                .padding(16.dp)
        ) {
            if (tipo == "Alumno" || tipo == "invitado" || preferencesManager.getIsOrientador() == 1) {
                MenuButton("Tests") {
                    navController.navigate("home_screen")
                    scope.launch { drawerState.close() }
                }
            }

            MenuButton("Áreas") {
                navController.navigate("areas_screen")
                scope.launch { drawerState.close() }
            }

            if (tipo == "Profesor") {
                MenuButton("Alumnos") {
                    navController.navigate("students_screen")
                    scope.launch { drawerState.close() }
                }
            }

            MenuButton("Perfil") {
                navController.navigate("profile_screen")
                scope.launch { drawerState.close() }
            }

            MenuButton("Mi Centro") {
                navController.navigate("centros_screen")
                scope.launch { drawerState.close() }
            }

            if(tipo != "invitado"){
                MenuButton("Resultados") {
                    navController.navigate("results_screen")
                    scope.launch { drawerState.close() }
                }
            }

            /*MenuButton("Configuración") {
                navController.navigate("settings_screen")
                scope.launch { drawerState.close() }
            }*/
            MenuButton("Ayuda") {
                navController.navigate("help_screen")
                scope.launch { drawerState.close() }
            }
            /*MenuButton("Acerca de") {
                navController.navigate("about_screen")
                scope.launch { drawerState.close() }
            }*/

            Spacer(modifier = Modifier.weight(1f))

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
