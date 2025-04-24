package com.escolavision.testescolavision.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.LoginRequest
import com.escolavision.testescolavision.API.LoginResponse
import com.escolavision.testescolavision.ShowAlertDialog
import com.escolavision.testescolavision.ViewModel.LoginViewModel
import com.escolavision.testescolavision.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R


// Pantalla de inicio de sesión que maneja la autenticación de usuarios
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    // Configuración inicial y gestión de estado
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Diálogo de error para mostrar mensajes al usuario
    if (showErrorDialog) {
        ShowAlertDialog(message = errorMessage) { showErrorDialog = false }
    }

    // Función para manejar el proceso de inicio de sesión
    fun handleLogin() {
        val loginRequest = LoginRequest(viewModel.usuario, viewModel.contraseña)
        Log.d("LoginDebug", "Usuario: ${viewModel.usuario}, Contraseña: ${viewModel.contraseña}")
        
        // Llamada a la API para autenticar
        RetrofitClient.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginDebug", "Respuesta del servidor: ${response.body()?.toString() ?: "null"}")
                
                // Manejo de respuesta exitosa
                if (response.isSuccessful && response.body()?.status == "success") {
                    // Extracción de datos del usuario
                    val id = response.body()?.id ?: 0
                    val tipo = response.body()?.tipo ?: ""
                    val is_orientador = response.body()?.is_orientador ?: 0
                    val id_centro = response.body()?.id_centro ?: ""
                    
                    // Guardado de datos en preferencias
                    preferencesManager.saveLogin(id, tipo, id_centro)
                    preferencesManager.saveIsOrientador(is_orientador)
                    
                    // Navegación según el tipo de usuario
                    if(tipo != "Profesor" || is_orientador == 1){
                        navController.navigate("home_screen") {
                            popUpTo("first_screen") { inclusive = true }
                        }
                    }else{
                        navController.navigate("students_screen") {
                            popUpTo("first_screen") { inclusive = true }
                        }
                    }
                } else {
                    // Manejo de error en la respuesta
                    errorMessage = "Login fallido: ${response.body()?.message ?: "Error desconocido"}"
                    showErrorDialog = true
                }
            }

            // Manejo de error en la conexión
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("Error de red", "" + t.message)
                errorMessage = "Error de red: ${t.message}"
                showErrorDialog = true
            }
        })
    }

    // Interfaz de usuario
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .navigationBarsPadding().imePadding(),
        contentAlignment = Alignment.Center
    ) {
        // Contenido principal
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de la aplicación
            Image(painter = painterResource(id = R.drawable.logo_app), 
                  contentDescription = null, 
                  modifier = Modifier.size(128.dp))
            
            // Título de la aplicación
            Spacer(modifier = Modifier.height(16.dp))
            Text("EscolaVision", 
                 fontSize = 36.sp, 
                 fontWeight = FontWeight.Bold, 
                 color = colorResource(id = R.color.titulos))
            
            // Campo de usuario
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = viewModel.usuario,
                onValueChange = { viewModel.updateUsuario(it) },
                singleLine = true,
                maxLines = 1,
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
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
            
            // Campo de contraseña
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.contraseña,
                onValueChange = { viewModel.updateContraseña(it) },
                label = { Text("Contraseña") },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
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
            
            // Botón de inicio de sesión
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { handleLogin() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.azulBoton)
                ),
            ) { Text("Iniciar Sesión") }
            
            // Enlaces adicionales
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { navController.navigate("register_screen") },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) { Text("¿No tienes cuenta? Regístrate") }
            
            // Botón de acceso como invitado
            TextButton(
                onClick = {
                    preferencesManager.saveLogin(0, "invitado", "1")
                    preferencesManager.saveIsOrientador(0)
                    navController.navigate("home_screen") {
                        popUpTo("first_screen") { inclusive = false }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) { Text("Continuar como invitado") }
        }
    }
}
