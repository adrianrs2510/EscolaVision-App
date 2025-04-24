package com.escolavision.testescolavision.Screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.escolavision.testescolavision.ShowAlertDialog
import com.escolavision.testescolavision.ViewModel.RegisterViewModel
import com.escolavision.testescolavision.imageToBase64
import com.escolavision.testescolavision.API.Centro
import com.escolavision.testescolavision.API.CentroListResponse
import com.escolavision.testescolavision.API.RegisterRequest
import com.escolavision.testescolavision.API.RegisterResponse
import com.escolavision.testescolavision.API.Usuarios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.time.format.DateTimeFormatter
import com.escolavision.testescolavision.R


@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val context = LocalContext.current
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showErrorDialog) {
        ShowAlertDialog(message = errorMessage) { showErrorDialog = false }
    }

    fun showError(message: String) {
        errorMessage = message
        showErrorDialog = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(16.dp)
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            RegisterForm(viewModel, context, navController, ::showError)
        }
    }
}

@Composable
fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Registro",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.titulos)
        )
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun RegisterForm(viewModel: RegisterViewModel, context: Context, navController: NavController, showError: (String) -> Unit) {
    /*val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.updateImageUri(uri) }*/

    InputField(value = viewModel.nombre.value, label = "Nombre y Apellidos") { viewModel.updateNombre(it) }
    InputField(value = viewModel.email.value, label = "Email") { viewModel.updateEmail(it) }
    InputField(value = viewModel.dni.value, label = "DNI") { viewModel.updateDni(it) }
    InputField(label = "Año de Nacimiento", value = viewModel.edad.value) { viewModel.updateEdad(it) }
    InputField(value = viewModel.claveAcceso.value, label = "Contraseña", isPassword = true) { viewModel.updateClaveAcceso(it) }

    //ImagePicker(imagePickerLauncher, viewModel.selectedImageUri.value.toString())

    ToggleButtons(viewModel)

    SearchCentros(viewModel)
    var centroSeleccionado by remember { mutableStateOf<Centro?>(null) }

    CentrosList(viewModel) { centro ->
        centroSeleccionado = centro
        viewModel.centroSeleccionado.value = centro
    }

    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { handleRegister(viewModel, context, navController, showError) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Text("Registrarse", color = Color.White)
    }
}

@Composable
fun FilterDialog(viewModel: RegisterViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Estados de los filtros
    var comunidades by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var provincias by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var municipios by remember { mutableStateOf<List<String>>(emptyList()) }

    // Filtros seleccionados
    var selectedComunidad by remember { mutableStateOf("") }
    var selectedProvincia by remember { mutableStateOf("") }
    var selectedMunicipio by remember { mutableStateOf("") }

    // Flags para indicar si los datos de provincias y municipios están cargados
    var provinciasCargadas by remember { mutableStateOf(false) }
    var municipiosCargados by remember { mutableStateOf(false) }

    // Cargar las comunidades cuando se abre el diálogo
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            comunidades = fetchComunidades()
        }
    }

    // Filtrar las provincias cuando se selecciona una comunidad
    LaunchedEffect(selectedComunidad) {
        if (selectedComunidad.isNotEmpty()) {
            coroutineScope.launch {
                provincias = fetchProvincias(comunidades.find { it.first == selectedComunidad }?.second ?: "")
                selectedProvincia = "" // Resetear provincia
                municipios = emptyList() // Resetear municipios
                provinciasCargadas = true // Marcar provincias como cargadas
            }
        }
    }

    // Filtrar los municipios cuando se selecciona una provincia
    LaunchedEffect(selectedProvincia) {
        if (selectedProvincia.isNotEmpty()) {
            coroutineScope.launch {
                municipios = fetchMunicipios(provincias.find { it.first == selectedProvincia }?.second ?: "")
                selectedMunicipio = "" // Resetear municipio
                municipiosCargados = true // Marcar municipios como cargados
            }
        }
    }

    // Convertir las listas de pares en listas de strings
    val comunidadesOptions = comunidades.map { it.first }
    val provinciasOptions = provincias.map { it.first }
    val municipiosOptions = municipios

    // Mostrar el AlertDialog para seleccionar filtros
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Seleccionar Filtros") },
        text = {
            Column {
                // Selector de Comunidad Autónoma
                DropdownSelector(
                    label = "Comunidad Autónoma",
                    options = comunidadesOptions,
                    selectedOption = selectedComunidad,
                    onOptionSelected = { selectedComunidad = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Selector de Provincia, habilitado solo si las provincias están cargadas
                DropdownSelector(
                    label = "Provincia",
                    options = provinciasOptions,
                    selectedOption = selectedProvincia,
                    onOptionSelected = { selectedProvincia = it },
                    enabled = provinciasCargadas && selectedComunidad.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Selector de Municipio, habilitado solo si los municipios están cargados
                DropdownSelector(
                    label = "Municipio",
                    options = municipiosOptions,
                    selectedOption = selectedMunicipio,
                    onOptionSelected = { selectedMunicipio = it },
                    enabled = municipiosCargados && selectedProvincia.isNotEmpty()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Actualizar los filtros en el ViewModel
                viewModel.setFilters(selectedComunidad, selectedProvincia, selectedMunicipio)

                // Llamar a la función para hacer la búsqueda
                searchCentros(viewModel, context)

                // Cerrar el diálogo
                onDismiss()
            }) {
                Text("Buscar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCentros(viewModel: RegisterViewModel) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // Filtrar los centros según el texto de búsqueda
    val filteredCentros = viewModel.centros.value.filter {
        it.denominacion_especifica.contains(searchText, ignoreCase = true)
    }

    // Actualizar los centros solo cuando sea necesario
    LaunchedEffect(searchText) {
        if (searchText.isEmpty()) {
            searchCentros(viewModel, context)
        } else {
            viewModel.updateCentros(filteredCentros)
        }
    }

    OutlinedTextField(
        value = searchText,
        onValueChange = { newText -> searchText = newText },
        label = { Text("Buscar Centro") },
        trailingIcon = {
            Row {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Filtrar",
                        tint = Color.Unspecified
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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

    if (showDialog) {
        FilterDialog(viewModel, onDismiss = { showDialog = false })
    }
}




const val key = "a4bed7909a6572f45ec3fcc7bc36722db648c87dd6cdef01666f1b04e242b40c"

suspend fun fetchComunidades(): List<Pair<String, String>> {
    return try {
        // Mover la operación de red a un hilo de fondo
        withContext(Dispatchers.IO) {
            val response = URL("https://apiv1.geoapi.es/comunidades?type=JSON&key=$key&sandbox=0").readText()
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("data")
            List(jsonArray.length()) {
                val comunidad = jsonArray.getJSONObject(it)
                val nombreComunidad = comunidad.getString("COM")
                val codigoComunidad = comunidad.getString("CCOM")
                Pair(nombreComunidad, codigoComunidad)  // Retorna un par (nombre, código)
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}



suspend fun fetchProvincias(comunidad: String): List<Pair<String, String>> {
    return try {
        withContext(Dispatchers.IO) {
            val response = URL("https://apiv1.geoapi.es/provincias?CCOM=$comunidad&type=JSON&key=$key&sandbox=0").readText()
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("data")
            List(jsonArray.length()) {
                val provincia = jsonArray.getJSONObject(it)
                val nombreProvincia = provincia.getString("PRO")
                val codigoProvincia = provincia.getString("CPRO")
                Pair(nombreProvincia, codigoProvincia)  // Retorna un par (nombre, código)
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

suspend fun fetchMunicipios(provincia: String): List<String> {
    return try {
        withContext(Dispatchers.IO) {
            val response = URL("https://apiv1.geoapi.es/municipios?CPRO=$provincia&type=JSON&key=$key&sandbox=0").readText()
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("data")
            List(jsonArray.length()) {
                val provincia = jsonArray.getJSONObject(it)
                provincia.getString("DMUN50")
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}


@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    enabled: Boolean = true,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (enabled) Color.Gray else Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clickable(enabled) { expanded = true }
                .padding(12.dp)
        ) {
            Text(text = if (selectedOption.isNotEmpty()) selectedOption else "Seleccionar", color = if (enabled) Color.Black else Color.Gray)
        }
        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}




fun searchCentros(viewModel: RegisterViewModel, context: Context) {

    val localidad:String = viewModel.selectedMunicipio.value.toString()
    RetrofitClient.api.searchCentros(localidad = localidad).enqueue(object : Callback<CentroListResponse> {
        override fun onResponse(call: Call<CentroListResponse>, response: Response<CentroListResponse>) {
            if (response.isSuccessful) {
                val centros = response.body()?.centros
                viewModel.updateCentros(centros)
            } else {
                Toast.makeText(context,"No se encontraron resultados", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<CentroListResponse>, t: Throwable) {
            Toast.makeText(context,"Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

@Composable
fun CentrosList(viewModel: RegisterViewModel, onCentroSeleccionado: (Centro) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.Gray)
            .background(color = Color.White)
    ) {
        for (centro in viewModel.centros.value) {
            item {
                CentroItem(centro, onCentroSeleccionado)
            }
        }
        if(viewModel.centros.value.isEmpty()){
            item {
                Text("No se encontraron centros en ese municipio")
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    viewModel.centroSeleccionado.value?.let {
        Text(text = "Centro seleccionado: ${it.denominacion_especifica}", color = colorResource(id = R.color.titulos))
    }
}


@Composable
fun CentroItem(centro: Centro, onCentroSeleccionado: (Centro) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCentroSeleccionado(centro) },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Centro: ${centro.denominacion_especifica}", fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun ImagePicker(imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>, selectedImageUri: String?) {
    Button(
        onClick = { imagePickerLauncher.launch("image/*") },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Text("Seleccionar imagen", color = Color.White)
    }
    selectedImageUri?.let { uri ->
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberImagePainter(uri),
            contentDescription = null,
            modifier = Modifier.size(128.dp).clip(CircleShape)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(value: String, label: String, isPassword: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
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
}

@Composable
fun ToggleButtons(viewModel: RegisterViewModel) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { viewModel.selectAlumno() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isAlumnoSelected.value) colorResource(id = R.color.azulBoton) else Color.Gray
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Alumno", color = Color.White)
        }
        Button(
            onClick = { viewModel.selectProfesor() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!viewModel.isAlumnoSelected.value) colorResource(id = R.color.azulBoton) else Color.Gray
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Profesor", color = Color.White)
        }
    }
}

private fun handleRegister(viewModel: RegisterViewModel, context: Context, navController: NavController, showError: (String) -> Unit) {
    if (viewModel.nombre.value.isEmpty() || viewModel.dni.value.isEmpty() || viewModel.claveAcceso.value.isEmpty()) {
        showError("Todos los campos son requeridos")
        return
    }

    val base64Image = viewModel.selectedImageUri.value?.let { imageToBase64(it, context) }
    if (base64Image != null && base64Image.length > 20000) {
        showError("Imagen demasiado grande")
        return
    }

    val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val fechavm = viewModel.edad.value
    val fecha = fechavm.format(formatterDate)


    val registerRequest = RegisterRequest(
        tabla = "usuarios",
        datos = Usuarios(
            nombre = viewModel.nombre.value,
            dni = viewModel.dni.value,
            email = viewModel.email.value,
            fecha_nacimiento = fecha,
            tipo_usuario = if (viewModel.tipo.value == "Alumno") "1" else "2",
            contraseña = viewModel.claveAcceso.value,
            foto = base64Image,
            id = 0,
            is_orientador = 0,
            id_centro = viewModel.centroSeleccionado.value.id
        )
    )

    RetrofitClient.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
            if (response.isSuccessful && response.body()?.status == "success") {
                Toast.makeText(context, "Registro realizado correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("login_screen") { popUpTo("first_screen") { inclusive = false } }
            } else {
                showError("Registro fallido")
            }
        }

        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
            showError("Error de red: ${t.message}")
        }
    })
}