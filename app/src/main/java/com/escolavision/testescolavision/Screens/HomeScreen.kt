package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.Test
import com.escolavision.testescolavision.API.TestsResponse
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R


// Clase de datos que representa un elemento de test con su estado de favorito
data class TestItem(
    val test: Test,
    var isFavorite: Boolean = false
)

// Pantalla principal que muestra la lista de tests disponibles
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Configuración inicial y obtención de datos del usuario
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""

    // Estados para manejar la lista de tests y la actualización
    var tests by remember { mutableStateOf<List<TestItem>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Configuración del drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Función para cargar los tests desde la API
    fun loadTests() {
        isRefreshing = true
        RetrofitClient.api.getTests().enqueue(object : Callback<TestsResponse> {
            override fun onResponse(call: Call<TestsResponse>, response: Response<TestsResponse>) {
                if (response.isSuccessful) {
                    // Filtra los tests visibles y los convierte en TestItems
                    tests = response.body()?.tests?.filter { it.isVisible == 1 }?.map {
                        TestItem(test = it)
                    } ?: emptyList()
                }
                isRefreshing = false
            }
            override fun onFailure(call: Call<TestsResponse>, t: Throwable) {
                isRefreshing = false
            }
        })
    }

    // Efecto que se ejecuta al iniciar la pantalla para cargar los tests
    LaunchedEffect(Unit) {
        loadTests()
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
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Tests",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center),
                                    color = colorResource(id = R.color.titulos),
                                )
                            }
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
                    // Contenido principal con lista de tests
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.fondoInicio))
                            .padding(paddingValues)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Lista actualizable con gesto de pull-to-refresh
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing),
                            onRefresh = { loadTests() }
                        ) {
                            // Lista scrolleable de tests
                            LazyColumn(
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Renderiza cada test como un botón
                                items(tests) { testItem ->
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = { navController.navigate("test_detail_screen/${testItem.test.id}") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = MaterialTheme.shapes.medium,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                                        ) {
                                            Text(text = testItem.test.nombretest, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}
