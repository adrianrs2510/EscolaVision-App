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
import com.escolavision.testescolavision.API.CentroCompleto
import com.escolavision.testescolavision.API.CentroResponse
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R


// Pantalla principal que muestra la información del centro educativo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentrosScreen(navController: NavController) {
    // Configuración inicial y obtención de datos del usuario
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    val idCentro = preferencesManager.getCenterData()
    
    // Configuración del drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados para manejar los datos del centro y la carga
    var centro by remember { mutableStateOf<CentroCompleto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Función para cargar los datos del centro desde la API
    fun loadCentro() {
        RetrofitClient.api.getCentro(id = idCentro).enqueue(object : Callback<CentroResponse> {
            override fun onResponse(call: Call<CentroResponse>, response: Response<CentroResponse>) {
                if (response.isSuccessful) {
                    centro = response.body()?.centros?.firstOrNull()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<CentroResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    // Efecto que se ejecuta cuando cambia el ID del centro
    LaunchedEffect(idCentro) {
        loadCentro()
    }

    // Estructura principal de la interfaz
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
                                text = "Mi Centro",
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
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = Color.Transparent)
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.fondoInicio))
                            .padding(paddingValues)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        ) {


                            centro?.let { data ->
                                Text(
                                    text = "Datos del centro",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.titulos),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                CentroCardDatosCentro(centro!!)
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Datos de contacto",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.titulos),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                CentroCardDatosContacto(centro!!)
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Localización",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.titulos),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                CentroCardLocalizacion(centro!!)
                            } ?: run {
                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            )
        }
    )
}

// Componente que muestra la información de localización del centro
@Composable
fun CentroCardLocalizacion(data: CentroCompleto) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Sección de dirección
            Text(
                text = "Dirección",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = data.domicilio,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Sección de localidad
            Text(
                text = "Localidad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = data.localidad + " ("+data.codigo_postal+")",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Sección de provincia
            Text(
                text = "Provincia",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = data.provincia + " ("+data.comunidad_autonoma+")",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

// Componente que muestra los datos generales del centro
@Composable
fun CentroCardDatosCentro(data: CentroCompleto) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Sección del código del centro
            Text(
                text = "Código del Centro",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = data.codigo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Sección de denominación genérica
            Text(
                text = "Denominación genérica",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = data.denominacion_generica,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Sección de denominación específica
            Text(
                text = "Denominación del centro",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = data.denominacion_especifica,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

// Componente que muestra la información de contacto del centro
@Composable
fun CentroCardDatosContacto(data: CentroCompleto) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Sección de teléfono principal
            Text(
                text = "Teléfono",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = data.telefono,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Sección de teléfono secundario con manejo de nulos
            Text(
                text = "Teléfono secundario",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            if(data.telefono_secundario != null){
                Text(
                    text = data.telefono_secundario,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }else{
                Text(
                    text = "-",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}