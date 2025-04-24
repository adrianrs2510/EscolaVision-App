/**
 * Pantalla de visualización de resultados de EscolaVision.
 * 
 * Esta pantalla permite ver y gestionar los resultados de los tests:
 * - Visualización de intentos de tests realizados
 * - Filtrado de resultados según tipo de usuario
 * - Gráficos de barras para visualización de datos
 * - Detalles específicos por intento
 * 
 * Características principales:
 * - Interfaz Material 3 con lista dinámica de resultados
 * - Sistema de actualización pull-to-refresh
 * - Gráficos interactivos por áreas
 * - Vista diferenciada para alumnos y profesores
 * - Navegación a detalles específicos de cada intento
 * 
 * La pantalla actúa como centro de análisis de resultados,
 * permitiendo tanto a alumnos como profesores revisar
 * el progreso y rendimiento en los tests realizados.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.Intento
import com.escolavision.testescolavision.API.IntentoListResponse
import com.escolavision.testescolavision.API.UsuariosListResponse
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R


// Pantalla que muestra los resultados de los tests realizados
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(navController: NavController) {
    // Configuración inicial y obtención de datos del usuario
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    val id_centro = preferencesManager.getCenterData()
    
    // Configuración del drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados para manejar la lista de intentos y carga
    var intentos by remember { mutableStateOf<List<Intento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val esAlumno = tipo == "Alumno"

    // Función para cargar los intentos desde la API
    val loadIntentos = {
        isLoading = true
        RetrofitClient.api.getIntentos(id_centro = id_centro).enqueue(object : Callback<IntentoListResponse> {
            override fun onResponse(call: Call<IntentoListResponse>, response: Response<IntentoListResponse>) {
                if (response.isSuccessful) {
                    // Filtra los intentos según el tipo de usuario
                    val allIntentos = response.body()?.intentos ?: emptyList()
                    intentos = if (esAlumno) {
                        allIntentos.filter { it.idusuario == id }
                    } else {
                        allIntentos
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<IntentoListResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    // Carga inicial de datos
    LaunchedEffect(Unit) {
        loadIntentos()
    }

    // Estructura principal con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
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
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Resultados",
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.fondoInicio))
                            .padding(paddingValues)
                    ) {
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing = isLoading),
                            onRefresh = { loadIntentos() }
                        ) {

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    items(intentos) { intento ->
                                        IntentoItem(intento, esAlumno, id_centro, navController)
                                    }
                                }

                        }
                    }
                }
            )
        }
    )
}

// Componente que representa un intento individual de test
@Composable
fun IntentoItem(intento: Intento, esAlumno: Boolean, id_centro: String, navController: NavController) {
    // Estado para el nombre del alumno
    var alumnoNombre by remember { mutableStateOf("Cargando...") }

    // Carga el nombre del alumno si el usuario no es alumno
    if (!esAlumno) {
        LaunchedEffect(intento.idusuario) {
            fetchAlumnoName(intento.idusuario, id_centro = id_centro) { nombre ->
                alumnoNombre = nombre
            }
        }
    }

    // Tarjeta que muestra los detalles del intento
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable{
                navController.navigate("result_test_screen/${intento.resultados}/results_screen")
            },
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID Test: ${intento.idtest}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            if (!esAlumno) Text(text = "Usuario: $alumnoNombre", fontSize = 16.sp, color = Color.White)
            Text(text = "Fecha: ${intento.fecha} ${intento.hora}", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Resultados:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)

            val resultados = intento.resultados.split(";").mapNotNull { it.toFloatOrNull()?.toInt() }

            BarChart(
                data = resultados,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Función para obtener el nombre del alumno desde la API
fun fetchAlumnoName(alumnoId: Int, id_centro: String, callback: (String) -> Unit) {
    RetrofitClient.api.getUsuarioData(id_centro = id_centro).enqueue(object : Callback<UsuariosListResponse> {
        override fun onResponse(call: Call<UsuariosListResponse>, response: Response<UsuariosListResponse>) {
            if (response.isSuccessful) {
                val alumnosList = response.body()?.usuarios ?: emptyList()
                val alumno = alumnosList.find { it.id == alumnoId }
                if (alumno != null) {
                    callback(alumno.nombre)
                } else {
                    callback("Profesor no encontrado")
                }
            } else {
                callback("Error al obtener el nombre del profesor")
            }
        }

        override fun onFailure(call: Call<UsuariosListResponse>, t: Throwable) {
            callback("Error de red: ${t.message}")
        }
    })
}

// Componente que muestra un gráfico de barras con los resultados
@Composable
fun BarChart(data: List<Int>, modifier: Modifier = Modifier) {
    val maxValue = data.maxOrNull() ?: 0
    val barWidth = 20.dp
    val spacing = 8.dp
    val areas = listOf("Área 1", "Área 2", "Área 3", "Área 4", "Área 5")

    // Estructura del gráfico de barras
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Barras del gráfico
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Genera una barra para cada valor
                data.forEach { value ->
                    val barHeight = (value.toFloat() / maxValue) * 100
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .height(barHeight.dp)
                            .background(Color.Blue)
                    )
                    Spacer(modifier = Modifier.width(spacing))
                }
            }
            
            // Etiquetas de las áreas
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                areas.forEachIndexed { index, area ->
                    Text(
                        text = area,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


