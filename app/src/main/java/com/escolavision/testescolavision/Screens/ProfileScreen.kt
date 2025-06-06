/**
 * Pantalla de gestión de perfil de usuario en EscolaVision.
 * 
 * Esta pantalla permite a los usuarios ver y editar su información personal:
 * - Visualización de datos del perfil (nombre, DNI, foto)
 * - Edición de información personal
 * - Actualización de contraseña
 * - Gestión de foto de perfil
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño adaptativo
 * - Carga y actualización de datos desde la API
 * - Gestión de imágenes en formato Base64
 * - Validación de campos y datos
 * - Sistema de actualización pull-to-refresh
 * 
 * La pantalla proporciona una interfaz completa para que los usuarios
 * gestionen su información personal y mantengan actualizado su perfil
 * en la plataforma EscolaVision.
 */

 package com.escolavision.testescolavision.Screens

import RetrofitClient
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import com.escolavision.testescolavision.API.UpdateProfileResponse
import com.escolavision.testescolavision.API.UpdateRequest
import com.escolavision.testescolavision.API.Usuarios
import com.escolavision.testescolavision.API.UsuariosListResponse
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R


// Pantalla de perfil que muestra y permite editar la información del usuario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // Configuración inicial y estados
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    val id_centro = preferencesManager.getCenterData()
    
    // Estados para manejar la UI y los datos
    var user by remember { mutableStateOf<Usuarios?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Configuración del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Función para cargar los datos del usuario desde la API
    fun loadUserData() {
        val userId = id
        if (false) {
            Toast.makeText(context, "ID inválido", Toast.LENGTH_SHORT).show()
            isLoading = false
            return
        }

        RetrofitClient.api.getUsuarioData(id_centro = id_centro).enqueue(object : Callback<UsuariosListResponse> {
            override fun onResponse(call: Call<UsuariosListResponse>, response: Response<UsuariosListResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val alumnosList = response.body()?.usuarios ?: emptyList()
                    val alumno = alumnosList.find { it.id == userId }
                    if (alumno != null) {
                        user = Usuarios(
                            nombre = alumno.nombre,
                            dni = alumno.dni,
                            contraseña = alumno.contraseña,
                            foto = alumno.foto,
                            email = alumno.email,
                            id = alumno.id,
                            is_orientador = alumno.is_orientador,
                            fecha_nacimiento = alumno.fecha_nacimiento,
                            tipo_usuario = alumno.tipo_usuario,
                            id_centro = alumno.id_centro
                        )
                    }
                } else {
                    Toast.makeText(context, "Fallo al cargar los datos del usuario", Toast.LENGTH_SHORT).show()
                }
                isRefreshing = false 
            }

            override fun onFailure(call: Call<UsuariosListResponse>, t: Throwable) {
                isLoading = false
                isRefreshing = false 
                Log.d("ProfileScreen", "onFailure: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Efecto que carga los datos al iniciar la pantalla
    LaunchedEffect(id) {
        loadUserData()
    }

    // Diálogo de edición de perfil
    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                showEditDialog = false
                handleProfileUpdate(updatedUser, context, navController, ::loadUserData)
            },
            id = id.toString()
        )
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
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    isRefreshing = true
                    loadUserData() 
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Perfil",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = colorResource(id = R.color.titulos),
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
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorResource(id = R.color.fondoInicio)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (user != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorResource(id = R.color.fondoInicio))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.fondoInicio))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        user!!.foto?.let { foto ->
                                            if (foto.isNotEmpty()) {
                                                val imageBytes = Base64.decode(foto, Base64.DEFAULT)
                                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                                Image(
                                                    bitmap = bitmap.asImageBitmap(),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(128.dp)
                                                        .clip(CircleShape)
                                                )
                                            } else {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_person2),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(128.dp)
                                                        .clip(CircleShape)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "${user!!.nombre}",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            textAlign = TextAlign.Center,
                                            color = colorResource(id = R.color.titulos)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "DNI: ${user!!.dni}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.Gray
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                showEditDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                                        ) {
                                            Text(
                                                text = "Editar Perfil",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorResource(id = R.color.fondoInicio)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Invitado",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.titulos)
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    )
}


// Diálogo para editar el perfil del usuario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(user: Usuarios?, onDismiss: () -> Unit, onSave: (Usuarios) -> Unit, id: String) {
    // Validación inicial
    if (user == null) {
        return
    }

    // Selector de imágenes para la foto de perfil
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { user.foto = it.toString() }
    }

    // Estados para los campos editables
    var nombre by remember { mutableStateOf(user.nombre) }
    var foto by remember { mutableStateOf(user.foto) }
    var edad by remember { mutableStateOf(user.fecha_nacimiento) }
    var contraseña by remember { mutableStateOf("") }

    // Estructura del diálogo
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Perfil") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(text = "Nombre y Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text(text = "Año de nacimiento") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contraseña,
                    onValueChange = { contraseña = it },
                    label = { Text(text = "Introduce nueva Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                ImagePicker(imagePickerLauncher, foto)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val updatedUser = user.copy(
                    nombre = nombre,
                    foto = foto,
                    contraseña = contraseña,
                    fecha_nacimiento = edad,
                    id = Integer.parseInt(id)
                )
                onSave(updatedUser)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Función para manejar la actualización del perfil
private fun handleProfileUpdate(
    updatedUser: Usuarios,
    context: Context,
    navController: NavController,
    loadUserData: () -> Unit 
) {
    // Validación de campos requeridos
    if (updatedUser.nombre.isBlank() || updatedUser.fecha_nacimiento.isBlank()) {
        Toast.makeText(context, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
        return
    }

    // Validación del tamaño de la imagen
    val base64Image = updatedUser.foto
    if (base64Image != null && base64Image.length > 20000) {
        Toast.makeText(context, "Imagen demasiado grande", Toast.LENGTH_SHORT).show()
        return
    }

    // Preparación de la solicitud de actualización
    val updateRequest = UpdateRequest(
        datos = Usuarios(
            id = updatedUser.id,
            nombre = updatedUser.nombre,
            dni = updatedUser.dni,
            fecha_nacimiento = updatedUser.fecha_nacimiento,
            contraseña = updatedUser.contraseña,
            foto = base64Image,
            tipo_usuario = if ((updatedUser.tipo_usuario == "Alumno")) "1" else "2",
            is_orientador = updatedUser.is_orientador,
            email = updatedUser.email,
            id_centro = updatedUser.id_centro
        ),
        tabla = "usuarios",
        id = updatedUser.id
    )

    // Llamada a la API para actualizar el perfil
    RetrofitClient.api.update(updateRequest).enqueue(object : Callback<UpdateProfileResponse> {
        override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
            if (response.isSuccessful && response.message() == "OK") {
                Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                loadUserData()
            } else {
                Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

