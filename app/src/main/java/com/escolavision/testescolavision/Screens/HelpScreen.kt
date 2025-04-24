package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R


// Pantalla de ayuda que muestra información de soporte y FAQ
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    // Configuración inicial y obtención de datos del usuario
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    
    // Configuración del drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estructura principal de la interfaz con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Componente del menú lateral
            MenuDrawer(
                navController = navController,
                id = id,
                tipo = tipo,
                scope = scope,
                drawerState = drawerState,
                preferencesManager = preferencesManager
            )
        },
        content = {
            // Estructura principal de la pantalla
            Scaffold(
                // Barra superior con título y botón de menú
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Ayuda",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = colorResource(id = R.color.titulos)
                            )
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = colorResource(id = R.color.fondoInicio)
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch { }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = Color.Transparent)
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    // Contenido principal organizado en secciones
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.fondoInicio))
                            .padding(paddingValues)
                    ) {
                        // Sección de Ayuda y Soporte
                        Text(
                            text = "Ayuda y Soporte",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        // Sección de Preguntas Frecuentes
                        Text(
                            text = "Preguntas Frecuentes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        // Pregunta 1: Cambio de contraseña
                        Text(
                            text = "1. ¿Cómo puedo cambiar mi contraseña?",
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                        )
                        Text(
                            text = "Para cambiar tu contraseña, ve a la sección de Perfil...",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        // Pregunta 2: Visualización de resultados
                        Text(
                            text = "2. ¿Cómo puedo ver mis resultados?",
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                        )
                        Text(
                            text = "Puedes ver tus resultados en la sección de Resultados...",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        // Sección de Contacto
                        Text(
                            text = "Contacto",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Si necesitas más ayuda, puedes contactarnos...",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )

                        // Espaciador para separación visual
                        Spacer(modifier = Modifier.height(20.dp))

                        // Sección Acerca de
                        Text(
                            text = "Acerca de",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Escolavision App",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Versión 1.0.0",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        Text(
                            text = "Desarrollado por Escolavision Team",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )

                        // Sección de Política de Privacidad
                        Text(
                            text = "Política de Privacidad",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Nuestra aplicación respeta tu privacidad...",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                    }
                }
            )
        }
    )
}