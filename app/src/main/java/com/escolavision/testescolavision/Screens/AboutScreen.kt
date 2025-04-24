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



// Composable principal que representa la pantalla "Acerca de"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    // Obtiene el contexto actual y gestiona las preferencias del usuario
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    
    // Estado del drawer (menú lateral) y scope para corrutinas
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Implementación del drawer modal de navegación
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Contenido del menú lateral
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
                // Barra superior personalizada
                topBar = {
                    TopAppBar(
                        title = {
                            // Título de la pantalla
                            Text(
                                text = "Acerca de",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = colorResource(id = R.color.titulos),
                            )
                        },
                        // Configuración de colores y botones de la barra superior
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = colorResource(id = R.color.fondoInicio)
                        ),
                        // Botón de menú
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                            }
                        },
                        // Botón invisible para mantener simetría
                        actions = {
                            IconButton(onClick = {
                                scope.launch { }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = Color.Transparent)
                            }
                        }
                    )
                },
                // Contenido principal de la pantalla
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.fondoInicio))
                            .padding(paddingValues)
                    ) {
                        // Información de la aplicación
                        Text(
                            text = "Escolavision App",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        
                        // Versión de la aplicación
                        Text(
                            text = "Versión 1.0.0",
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        
                        // Créditos del desarrollador
                        Text(
                            text = "Desarrollado por Escolavision Team",
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        
                        // Sección de política de privacidad
                        Text(
                            text = "Política de Privacidad",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.titulos),
                            modifier = Modifier.padding(16.dp)
                        )
                        
                        // Texto de la política de privacidad
                        Text(
                            text = "Nuestra aplicación respeta tu privacidad y no comparte tus datos con terceros.",
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