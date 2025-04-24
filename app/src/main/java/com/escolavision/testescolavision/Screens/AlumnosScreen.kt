/**
 * Pantalla de gestión de alumnos de la aplicación EscolaVision.
 * 
 * Esta pantalla proporciona una interfaz completa para la gestión de alumnos, incluyendo:
 * - Listado de alumnos con búsqueda en tiempo real
 * - Funcionalidad CRUD completa (Crear, Leer, Actualizar, Eliminar)
 * - Gestión de imágenes de perfil
 * - Validación de datos de entrada
 * 
 * Características principales:
 * - Interfaz de usuario con Material Design 3
 * - Menú lateral para navegación
 * - Sistema de búsqueda integrado
 * - Gestión de estados con ViewModel
 * - Manejo de permisos y roles de usuario
 * - Integración con API REST para operaciones de base de datos
 * 
 * La pantalla es accesible solo para usuarios con permisos de administración
 * y permite la gestión completa de la información de los alumnos del centro.
 */

package com.escolavision.testescolavision.Screens

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import com.escolavision.testescolavision.ShowAlertDialog
import com.escolavision.testescolavision.ViewModel.AlumnosViewModel
import com.escolavision.testescolavision.imageToBase64
import com.escolavision.testescolavision.API.DeleteRequest
import com.escolavision.testescolavision.API.DeleteResponse
import com.escolavision.testescolavision.API.RegisterRequest
import com.escolavision.testescolavision.API.RegisterResponse
import com.escolavision.testescolavision.API.UpdateProfileResponse
import com.escolavision.testescolavision.API.UpdateRequest
import com.escolavision.testescolavision.API.Usuarios
import com.escolavision.testescolavision.API.UsuariosListResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter
import com.escolavision.testescolavision.R


// Importaciones necesarias para la funcionalidad de la pantalla
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(navController: NavController, viewModel: AlumnosViewModel = viewModel()) {
    // Variables de estado y contexto
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    
    // Obtención de datos del usuario y centro desde las preferencias
    val id = preferencesManager.getLoginData().first
    val tipo = preferencesManager.getLoginData().second ?: ""
    val id_centro = preferencesManager.getCenterData()
    
    // Estado del drawer (menú lateral) y scope para corrutinas
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados para manejo de diálogos y errores
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Estados para la lista de alumnos y filtrado
    var alumnos by remember { mutableStateOf<List<Usuarios>>(emptyList()) }
    var filteredAlumnos by remember { mutableStateOf<List<Usuarios>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedAlumno by remember { mutableStateOf<Usuarios?>(null) }
    
    // Estados para los diferentes diálogos
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Función para filtrar la lista de alumnos según la búsqueda
    val filteredList = alumnos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) || 
        it.dni.contains(searchQuery, ignoreCase = true)
    }

    // Función para cargar la lista de usuarios desde la API
    fun loadUsuarios() {
        RetrofitClient.api.getUsuarioData(id_centro = id_centro).enqueue(object : Callback<UsuariosListResponse> {
            override fun onResponse(call: Call<UsuariosListResponse>, response: Response<UsuariosListResponse>) {
                if (response.isSuccessful) {
                    alumnos = response.body()?.usuarios?.filter { it.tipo_usuario == "Alumno" } ?: emptyList()
                    filteredAlumnos = alumnos
                }
                isLoading = false
            }

            override fun onFailure(call: Call<UsuariosListResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    // Efecto que se ejecuta al iniciar la pantalla para cargar los datos
    LaunchedEffect(Unit) {
        loadUsuarios()
    }

    // Diálogo de edición de alumno
    if (showDialog && selectedAlumno != null) {
        EditAlumnoDialog(
            alumno = selectedAlumno!!,
            onDismiss = { showDialog = false },
            onSave = { updatedAlumno ->
                handleProfileUpdate(updatedAlumno, context, navController, ::loadUsuarios)
            }
        )
    }

    // Diálogo de confirmación para eliminar alumno
    if (showDeleteDialog && selectedAlumno != null) {
        DeleteAlumnoDialog(alumno = selectedAlumno!!, onDismiss = { showDeleteDialog = false }, onDelete = {
            val deleteRequest = DeleteRequest(
                tabla = "usuarios",
                id = selectedAlumno!!.id
            )
            Log.d("Retrofit", "Request: $deleteRequest")

            RetrofitClient.api.delete(deleteRequest).enqueue(object : Callback<DeleteResponse> {
                override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                    Log.d("Retrofit", "Response: ${response.body()}")
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(context, "Alumno eliminado correctamente", Toast.LENGTH_SHORT).show()
                        alumnos = alumnos.filter { it.id != selectedAlumno!!.id }
                    } else {
                        Toast.makeText(context, "Eliminación fallida", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

            showDeleteDialog = false
        })
    }

    // Diálogo de error
    if (showErrorDialog) {
        ShowAlertDialog(message = errorMessage) { showErrorDialog = false }
    }

    // Diálogo para añadir nuevo alumno
    if (showAddDialog) {
        AddAlumnoDialog(id_centro = id_centro,
            onDismiss = { showAddDialog = false },
            onAdd = { newAlumno ->

                val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val fechavm = viewModel.edad.value
                val fecha = fechavm.format(formatterDate)
                val base64Image = viewModel.selectedImageUri.value?.let { imageToBase64(it, context) }
                if (base64Image != null && base64Image.length > 20000) {
                    Toast.makeText(context, "Imagen demasiado grande", Toast.LENGTH_SHORT).show()
                }

                val contraseña = viewModel.dni.value+viewModel.edad.value
                viewModel.claveAcceso.value = contraseña

                val registerRequest = RegisterRequest(
                    tabla = "usuarios",
                    datos = Usuarios(
                        nombre = viewModel.nombre.value,
                        dni = viewModel.dni.value,
                        email = viewModel.email.value,
                        fecha_nacimiento = fecha,
                        tipo_usuario = "1",
                        contraseña = viewModel.claveAcceso.value,
                        foto = base64Image,
                        is_orientador = 0,
                        id = 0,
                        id_centro = id_centro
                    )
                )

                Log.d("Retrofit", "Request: $registerRequest")
                RetrofitClient.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(context, "Alumno añadido correctamente", Toast.LENGTH_SHORT).show()
                            loadUsuarios()
                        } else {
                            Toast.makeText(context, "Registro fallido", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Log.d("Retrofit", "Error: ${t.message}")
                        Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

                showAddDialog = false
            }
        )
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuDrawer(navController, id, tipo, scope, drawerState, preferencesManager)
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Alumnos",
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
                                scope.launch { showAddDialog = true  }
                            }) {
                                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add", tint = Color.White )
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
                        // Campo de búsqueda siempre visible
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            label = { Text("Buscar Alumno") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color.White,
                                focusedBorderColor = colorResource(id = R.color.azulBoton),
                                unfocusedBorderColor = Color.Gray,
                                containerColor = Color.White,
                                focusedLabelColor = colorResource(id = R.color.azulBoton),
                                unfocusedLabelColor = colorResource(id = R.color.azulBoton)
                            )
                        )

                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredList.size) { index ->
                                    val alumno = filteredList[index]
                                    AlumnoItem(alumno, onEdit = {
                                        selectedAlumno = it
                                        showDialog = true
                                    }, onDelete = {
                                        selectedAlumno = it
                                        showDeleteDialog = true
                                    })
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}

// Función para manejar la actualización del perfil de un alumno
private fun handleProfileUpdate(
    updatedUser: Usuarios,
    context: Context,
    navController: NavController,
    loadUserData: () -> Unit // Añadir esta línea
) {
    if (updatedUser.nombre.isBlank() || updatedUser.fecha_nacimiento.isBlank()) {
        Toast.makeText(context, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
        return
    }

    val base64Image = updatedUser.foto
    if (base64Image != null && base64Image.length > 20000) {
        Toast.makeText(context, "Imagen demasiado grande", Toast.LENGTH_SHORT).show()
        return
    }

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
    RetrofitClient.api.update(updateRequest).enqueue(object : Callback<UpdateProfileResponse> {
        override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
            if (response.isSuccessful && response.message() == "OK") {
                Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                loadUserData() // Aquí llamamos a loadUserData() para recargar el perfil
            } else {
                Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}


@Composable
fun AlumnoItem(alumno: Usuarios, onEdit: (Usuarios) -> Unit, onDelete: (Usuarios) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Nombre: ${alumno.nombre}", fontWeight = FontWeight.Bold)
                Text("DNI: ${alumno.dni}")
            }
            IconButton(onClick = { onEdit(alumno) }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { onDelete(alumno) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlumnoDialog(alumno: Usuarios, onDismiss: () -> Unit, onSave: (Usuarios) -> Unit) {
    var nombre by remember { mutableStateOf(alumno.nombre) }
    var email by remember { mutableStateOf(alumno.email) }
    var edad by remember { mutableStateOf(alumno.fecha_nacimiento) }
    var dni by remember { mutableStateOf(alumno.dni) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton)),
                onClick = {
                val updatedAlumno = alumno.copy(
                    nombre = nombre,
                    email = email,
                    fecha_nacimiento = edad,
                    dni = dni
                )
                onSave(updatedAlumno) 
                onDismiss() 
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton)),
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        },
        text = {
            Column {
                TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") })
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = edad, onValueChange = { edad = it }, label = { Text("Año de Nacimiento") })
            }
        }
    )
}


@Composable
fun DeleteAlumnoDialog(alumno: Usuarios, onDismiss: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton))) {
                Text("Cancelar")
            }
        },
        text = {
            Text("¿Estás seguro de que deseas eliminar a ${alumno.nombre}?")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlumnoDialog(id_centro: String, onDismiss: () -> Unit, onAdd: (Usuarios) -> Unit, viewModel: AlumnosViewModel = viewModel(), context: Context = LocalContext.current) {
    var isDniValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isEdadValid by remember { mutableStateOf(true) }

    val isValid = viewModel.nombre.value.isNotEmpty() && (viewModel.dni.value.isNotEmpty() || viewModel.email.value.isNotEmpty()) && viewModel.edad.value.isNotEmpty()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.updateImageUri(uri) }




    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        val base64Image = viewModel.selectedImageUri.value?.let { imageToBase64(it, context) }
                        if (base64Image != null && base64Image.length > 20000) {
                            Toast.makeText(context, "Imagen demasiado grande", Toast.LENGTH_SHORT).show()
                        }
                        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val fechavm = viewModel.edad.value
                        val fecha = fechavm.format(formatterDate)
                        val newAlumno = Usuarios(
                            id = 0,
                            nombre = viewModel.nombre.value,
                            dni = viewModel.dni.value,
                            tipo_usuario = "1",
                            foto = base64Image,
                            fecha_nacimiento = fecha,
                            is_orientador = 0,
                            contraseña = "",
                            email = viewModel.email.value,
                            id_centro = id_centro
                        )

                        onAdd(newAlumno)
                        onDismiss()
                    }
                },
                enabled = isValid
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = {
            Text("Añadir Alumno")
        },
        text = {
            Column {
                InputField(
                    value = viewModel.nombre.value,
                    label = "Nombre y Apellidos"
                ) { viewModel.nombre.value = it }
                InputField(value = viewModel.email.value, label = "Email") {
                    viewModel.email.value = it; isEmailValid =
                    Patterns.EMAIL_ADDRESS.matcher(it).matches()
                }
                InputField(value = viewModel.dni.value, label = "DNI") {
                    viewModel.dni.value = it; isDniValid = it.length == 9
                }

                if ((viewModel.dni.value.isNotEmpty() && !isDniValid) || (viewModel.email.value.isNotEmpty() && !isEmailValid)) {
                    Text("DNI o Email no válido", color = Color.Red, fontSize = 12.sp)
                }
                InputField(
                    value = viewModel.edad.value,
                    label = "Año de Nacimiento"
                ) { viewModel.edad.value = it; isEdadValid = it.length == 4 }
                if ((viewModel.edad.value.isNotEmpty() && !isEdadValid)) {
                    Text("Año de nacimiento no válido", color = Color.Red, fontSize = 12.sp)
                }
                ImagePicker(imagePickerLauncher, viewModel.selectedImageUri.value.toString())
            }
        }
    )
}