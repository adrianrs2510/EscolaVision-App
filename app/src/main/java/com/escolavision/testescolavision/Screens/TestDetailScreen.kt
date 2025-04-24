package com.escolavision.testescolavision.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.Intento
import com.escolavision.testescolavision.API.IntentoRequest
import com.escolavision.testescolavision.API.IntentoResponse
import com.escolavision.testescolavision.API.Preguntas
import com.escolavision.testescolavision.API.PreguntasListResponse
import com.escolavision.testescolavision.API.PxA
import com.escolavision.testescolavision.API.PxaListResponse
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Pantalla que muestra los detalles y preguntas de un test específico
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDetailScreen(navController: NavController, testId: Any) {
    // Configuración inicial y estados
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val idUsuario = preferencesManager.getLoginData().first
    val tipoUsuario = preferencesManager.getLoginData().second ?: ""
    
    // Estados para manejar preguntas y respuestas
    var preguntas by remember { mutableStateOf<List<Preguntas>>(emptyList()) }
    var respuestas by remember { mutableStateOf<Map<Int, Float>>(emptyMap()) }
    var pxa by remember { mutableStateOf<List<PxA>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Función para cargar las preguntas del test
    fun loadPreguntas() {
        isRefreshing = true
        RetrofitClient.api.getPreguntas().enqueue(object : Callback<PreguntasListResponse> {
            override fun onResponse(call: Call<PreguntasListResponse>, response: Response<PreguntasListResponse>) {
                if (response.isSuccessful) {
                    var testIdd = Integer.parseInt(testId.toString())
                    preguntas = response.body()?.preguntas?.filter { it.idtest == testIdd } ?: emptyList()
                }
                isRefreshing = false
            }

            override fun onFailure(call: Call<PreguntasListResponse>, t: Throwable) {
                isRefreshing = false
            }
        })


    }

    // Función para cargar la relación preguntas-áreas
    fun loadPxa() {
        RetrofitClient.api.getPxa().enqueue(object : Callback<PxaListResponse> {
            override fun onResponse(call: Call<PxaListResponse>, response: Response<PxaListResponse>) {
                if (response.isSuccessful) {
                    pxa = response.body()?.pxa ?: emptyList()
                }
            }

            override fun onFailure(call: Call<PxaListResponse>, t: Throwable) {}
        })
    }

    // Efecto que carga los datos iniciales
    LaunchedEffect(Unit) {
        loadPreguntas()
        loadPxa()
    }

    // Función para calcular los resultados del test
    fun calcularResultados(): String {
        // Mapea todas las respuestas con valor por defecto 5
        val respuestasCompletas = preguntas.associate { it.id to (respuestas[it.id] ?: 5f) }

        // Calcula promedios por área
        val resultadosPorArea = mutableMapOf<Int, Float>()
        val totalPorArea = mutableMapOf<Int, Int>()

        // Procesa cada respuesta y la asigna a sus áreas correspondientes
        respuestasCompletas.forEach { (idPregunta, respuesta) ->
            val areas = pxa.filter { it.idpregunta == idPregunta }.map { it.idarea }
            areas.forEach { area ->
                resultadosPorArea[area] = (resultadosPorArea[area] ?: 0f) + respuesta
                totalPorArea[area] = (totalPorArea[area] ?: 0) + 1
            }
        }

        // Calcula el promedio final para cada área
        val resultados = mutableListOf<Float>()
        for (i in 1..5) {
            val totalRespuestas = totalPorArea[i] ?: 0
            val sumaRespuestas = resultadosPorArea[i] ?: 0f
            resultados.add(if (totalRespuestas > 0) sumaRespuestas / totalRespuestas else 0f)
        }

        return resultados.joinToString(";")
    }

    // Función para guardar el intento en la base de datos
    fun insertarIntento() {
        val resultados = calcularResultados()

        // Obtiene la fecha y hora actual
        val now = LocalDateTime.now()
        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss")
        val fechaActual = now.format(formatterDate)
        val horaActual = now.format(formatterTime)
        
        // Prepara los datos del intento
        val testIdd = Integer.parseInt(testId.toString())
        val intentoData = IntentoRequest(
            tabla = "intentos",
            datos = Intento(
                idtest = testIdd,
                idusuario = idUsuario.toInt(),
                fecha = fechaActual,
                hora = horaActual,
                resultados = resultados
            )
        )
        // Solo guarda si el usuario está registrado
        if(idUsuario.toInt() != 0) {
            RetrofitClient.api.insertarIntento(intentoData).enqueue(object : Callback<IntentoResponse> {
                    override fun onResponse(
                        call: Call<IntentoResponse>,
                        response: Response<IntentoResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(context, "Test realizado correctamente", Toast.LENGTH_SHORT).show()
                            navController.navigate("result_test_screen/${resultados}/home_screen")
                        } else {
                            Toast.makeText(context, "Test fallido", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<IntentoResponse>, t: Throwable) {
                        Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        else{
            navController.navigate("result_test_screen/${resultados}/home_screen")
        }
    }

    // Estructura principal de la pantalla
    Scaffold(
        // Barra superior con título y botones
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Preguntas del Test",
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { respuestas = preguntas.associate { it.id to 5f } }) {
                        Text(text = "Nuevo", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            ) {
                // Lista de preguntas con pull-to-refresh
                Box(modifier = Modifier.weight(1f)) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = { loadPreguntas() }
                    ) {
                        // Lista scrolleable de preguntas
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Cada pregunta con su slider de respuesta
                            items(preguntas) { pregunta ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(5.dp, RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = pregunta.enunciado,
                                            color = Color.Black,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Slider(
                                                    value = respuestas[pregunta.id] ?: 5f,
                                                    onValueChange = {
                                                        respuestas =
                                                            respuestas.toMutableMap().apply {
                                                                put(pregunta.id, it)
                                                            }
                                                    },
                                                    valueRange = 0f..10f,
                                                    steps = 9,
                                                    colors = SliderDefaults.colors(
                                                        thumbColor = Color(0xFF1976D2),
                                                        activeTrackColor = Color(0xFF1976D2)
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                for (i in 0..10) {
                                                    Text(
                                                        text = "$i",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1976D2),
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Botón para enviar el test
                Button(
                    onClick = { insertarIntento() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text(
                        text = "Enviar Test",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}
