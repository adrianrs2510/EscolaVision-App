/**
 * Pantalla de configuración de EscolaVision.
 * 
 * Esta pantalla permite a los usuarios personalizar la aplicación:
 * - Gestión del tema de la aplicación (claro/oscuro)
 * - Configuraciones de usuario
 * - Opciones de cierre de sesión
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño adaptativo
 * - Persistencia de preferencias de usuario
 * - Gestión de temas visuales
 * - Navegación integrada con menú lateral
 * - Control de sesión de usuario
 * 
 * La pantalla actúa como centro de control para las
 * preferencias y configuraciones personalizadas del usuario,
 * permitiendo adaptar la experiencia de la aplicación
 * a sus necesidades específicas.
 */

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


// Pantalla de configuración que permite al usuario personalizar la aplicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    // Configuración inicial y obtención de datos del usuario
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    
    // Estado para el tema oscuro
    var isDarkTheme by remember { mutableStateOf(preferencesManager.getDarkTheme()) }
    
    // Configuración del drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estructura principal con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Componente del menú lateral
            MenuDrawer(
                navController = navController,
                id = id,
                tipo = tipo,
                scope = scope,
                drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
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
                                text = "Configuración",
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
                        // Botón de menú izquierdo
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, 
                                     contentDescription = "Menú", 
                                     tint = Color.White)
                            }
                        },
                        // Botón transparente para mantener simetría
                        actions = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, 
                                     contentDescription = "Menú", 
                                     tint = Color.Transparent)
                            }
                        }
                    )
                },
                // Contenido principal
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.fondoInicio))
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // Opción de tema oscuro
                        Text(
                            text = "Tema Oscuro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Switch para activar/desactivar tema oscuro
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { isDark ->
                                isDarkTheme = isDark
                                preferencesManager.saveDarkTheme(isDark) 
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón de cerrar sesión
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
            )
        }
    )
}
