package com.escolavision.testescolavision.Screens

// Importaciones necesarias para la funcionalidad de la pantalla
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.Area
import com.escolavision.testescolavision.API.AreaListResponse
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R


// Pantalla principal de Áreas que muestra una lista de áreas disponibles
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreasScreen(navController: NavController) {
    // Configuración inicial del contexto y preferencias del usuario
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    
    // Configuración del estado del drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados para manejar la lista de áreas y el estado de carga
    var areas by remember { mutableStateOf<List<Area>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Función para cargar las áreas desde la API
    val loadAreas = {
        isLoading = true
        RetrofitClient.api.getAreas().enqueue(object : Callback<AreaListResponse> {
            override fun onResponse(call: Call<AreaListResponse>, response: Response<AreaListResponse>) {
                if (response.isSuccessful) {
                    areas = response.body()?.areas ?: emptyList()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<AreaListResponse>, t: Throwable) {
                Log.e("AreasScreen", "Error al cargar áreas: ${t.message}")
                isLoading = false
            }
        })
    }

    // Efecto que se ejecuta al iniciar la pantalla para cargar los datos
    LaunchedEffect(Unit) {
        loadAreas()
    }

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
                                text = "Áreas",
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
                    // Contenido principal con lista de áreas
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.fondoInicio))
                            .padding(paddingValues)
                    ) {
                        // Lista actualizable con gesto de pull-to-refresh
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing = isLoading),
                            onRefresh = { loadAreas() }
                        ) {
                            // Lista scrolleable de áreas
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                items(areas) { area ->
                                    AreaItem(area = area)
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}

// Componente que representa un elemento individual de área en la lista
@Composable
fun AreaItem(area: Area) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decodificación y visualización del logo del área desde Base64
            val decodedString = Base64.decode(area.logo, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            val imageBitmap: ImageBitmap = bitmap.asImageBitmap()

            // Visualización del logo
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp)
            )
            
            // Información textual del área
            Column {
                Text(text = area.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = area.descripción, fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Justify)
            }
        }
    }
}
