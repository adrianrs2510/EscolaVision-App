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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    val id_centro = preferencesManager.getCenterData()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var intentos by remember { mutableStateOf<List<Intento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val esAlumno = tipo == "Alumno"

    val loadIntentos = {
        isLoading = true
        RetrofitClient.api.getIntentos(id_centro = id_centro).enqueue(object : Callback<IntentoListResponse> {
            override fun onResponse(call: Call<IntentoListResponse>, response: Response<IntentoListResponse>) {
                if (response.isSuccessful) {
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

    // Llamar a loadIntentos cuando se carga la pantalla por primera vez
    LaunchedEffect(Unit) {
        loadIntentos()
    }

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
                        // Usar SwipeRefresh para el pull-to-refresh
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


@Composable
fun IntentoItem(intento: Intento, esAlumno: Boolean, id_centro: String, navController: NavController) {
    var alumnoNombre by remember { mutableStateOf("Cargando...") }

    if (!esAlumno) {
        LaunchedEffect(intento.idusuario) {
            fetchAlumnoName(intento.idusuario, id_centro = id_centro) { nombre ->
                alumnoNombre = nombre
            }
        }
    }

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

@Composable
fun BarChart(data: List<Int>, modifier: Modifier = Modifier) {
    val maxValue = data.maxOrNull() ?: 0
    val barWidth = 20.dp
    val spacing = 8.dp
    val areas = listOf("Área 1", "Área 2", "Área 3", "Área 4", "Área 5")

    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
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


