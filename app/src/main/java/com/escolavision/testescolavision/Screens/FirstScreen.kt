/**
 * Pantalla de bienvenida y presentación inicial de EscolaVision.
 * 
 * Esta pantalla sirve como punto de entrada principal a la aplicación, mostrando:
 * - Logo e información del IES Politécnico Hermenegildo Lanz
 * - Identidad visual de EscolaVision (logo y nombre)
 * - Sistema de redirección inteligente basado en el tipo de usuario
 * 
 * Características principales:
 * - Diseño Material 3 con elementos visuales corporativos
 * - Gestión automática de sesiones de usuario
 * - Navegación condicional según el tipo de usuario:
 *   · Alumnos y orientadores -> home_screen
 *   · Otros usuarios -> students_screen
 *   · Sin sesión -> login_screen
 * 
 * Esta pantalla actúa como punto de partida de la aplicación,
 * proporcionando una experiencia de usuario personalizada según
 * el tipo de acceso y los permisos del usuario.
 */

 package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R


// Pantalla de inicio de la aplicación que muestra la presentación inicial
@Composable
fun FirstScreen(navController: NavController) {
    // Scope para manejar corrutinas
    val scope = rememberCoroutineScope()

    // Tema principal de Material Design
    MaterialTheme {
        // Columna principal que contiene todos los elementos
        Column(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.fondoInicio))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera con logo e información del instituto
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Logo del instituto
                Image(
                    painterResource(id = R.drawable.logo_instituto),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Información del instituto
                Column {
                    // Nombre del instituto
                    Text(
                        "IES Politécnico Hermenegildo Lanz",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorResource(id = R.color.titulos)
                        )
                    )
                    // Ubicación del instituto
                    Text(
                        "Granada",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            // Sección central con logo y título de la aplicación
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                // Logo de la aplicación
                Image(
                    painterResource(id = R.drawable.logo_app),
                    contentDescription = null,
                    modifier = Modifier.size(256.dp)
                )
                // Título de la aplicación
                Text(
                    "EscolaVision",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 55.sp,
                        color = colorResource(id = R.color.titulos)
                    )
                )
                // Subtítulo de la aplicación
                Text(
                    "Tu App de Orientación Escolar",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.Gray,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            // Botón de navegación
            IconButton(
                onClick = {
                    scope.launch {
                        // Obtiene el contexto y las preferencias del usuario
                        val context = navController.context
                        val preferencesManager = PreferencesManager(context)
                        
                        // Verifica si el usuario está logueado y redirige según su tipo
                        if (preferencesManager.isLoggedIn()) {
                            val (id, tipo) = preferencesManager.getLoginData()
                            val is_orientador = preferencesManager.getIsOrientador()
                            
                            // Redirige según el tipo de usuario
                            if(tipo == "Alumno" || is_orientador == 1 || tipo == "invitado"){
                                navController.navigate("home_screen") {
                                    popUpTo("first_screen") { inclusive = true }
                                }
                            }else{
                                navController.navigate("students_screen") {
                                    popUpTo("first_screen") { inclusive = true }
                                }
                            }
                        } else {
                            // Si no está logueado, redirige a la pantalla de login
                            navController.navigate("login_screen") {
                                popUpTo("first_screen") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(120.dp)
            ) {
                // Icono de flecha para avanzar
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "Go to next screen",
                    tint = Color.Unspecified
                )
            }
        }
    }
}