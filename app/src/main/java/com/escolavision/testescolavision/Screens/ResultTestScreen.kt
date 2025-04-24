package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.escolavision.testescolavision.API.Area
import com.escolavision.testescolavision.API.AreaListResponse
import com.escolavision.testescolavision.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultTestScreen(navController: NavController, resultados: List<Double>, pantallaAnterior: String) {
    var areas by remember { mutableStateOf<List<Area>>(emptyList()) }
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    var isLoading by remember { mutableStateOf(true) }

    // Cargar áreas desde la API
    LaunchedEffect(Unit) {
        isLoading = true
        RetrofitClient.api.getAreas().enqueue(object : Callback<AreaListResponse> {
            override fun onResponse(call: Call<AreaListResponse>, response: Response<AreaListResponse>) {
                if (response.isSuccessful) {
                    areas = response.body()?.areas ?: emptyList()
                } else {
                }
                isLoading = false
            }


            override fun onFailure(call: Call<AreaListResponse>, t: Throwable) {
                isLoading = false
            }
        })

    }

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
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colorResource(id = R.color.fondoInicio)),
                navigationIcon = {
                    if(pantallaAnterior == "home_screen"){
                        IconButton(onClick = { navController.navigate("home_screen") }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Menú", tint = Color.White)
                        }
                    }else{
                        IconButton(onClick = { navController.navigate("results_screen") }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Menú", tint = Color.White)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
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
                // Muestra un indicador de carga mientras se obtienen los datos
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorResource(id = R.color.azulBoton))
                    }
                } else {
                    // Verifica si hay suficientes áreas para los resultados
                    if (areas.size >= resultados.size) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(resultados.indices.toList()) { index ->
                                if (areas.isNotEmpty() && areas.size > index) { // Verifica si el índice es válido
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = colorResource(
                                                id = R.color.azulBoton
                                            )
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = "${areas[index].nombre}: ${resultados[index]}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = colorResource(id = R.color.titulos)
                                            )
                                            Text(
                                                text = areas[index].descripción,
                                                fontSize = 16.sp,
                                                color = colorResource(id = R.color.titulos)
                                            )
                                        }
                                    }
                                } else {
                                    // En caso de que las áreas estén vacías o el índice sea incorrecto
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No hay resultados para mostrar.",
                                            color = Color.Red,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

