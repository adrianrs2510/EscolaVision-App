package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R


@Composable
fun FirstScreen(navController: NavController) {
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.fondoInicio))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painterResource(id = R.drawable.logo_instituto),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "IES Politécnico Hermenegildo Lanz",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorResource(id = R.color.titulos)
                        )
                    )
                    Text(
                        "Granada",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.logo_app),
                    contentDescription = null,
                    modifier = Modifier.size(256.dp)
                )
                Text(
                    "EscolaVision",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 55.sp,
                        color = colorResource(id = R.color.titulos)
                    )
                )
                Text(
                    "Tu App de Orientación Escolar",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.Gray,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            IconButton(
                onClick = {
                    scope.launch {
                        val context = navController.context
                        val preferencesManager = PreferencesManager(context)
                        if (preferencesManager.isLoggedIn()) {
                            val (id, tipo) = preferencesManager.getLoginData()
                            val is_orientador = preferencesManager.getIsOrientador()
                            if(tipo == "Alumno" || is_orientador == 1 || tipo == "invitado"){
                                navController.navigate("home_screen") {
                                    popUpTo("first_screen") { inclusive = true }
                                }
                            }else{
                                navController.navigate("students_screen") {
                                    popUpTo("first_screen") { inclusive = true }
                                }
                            }
                        } else {
                            navController.navigate("login_screen") {
                                popUpTo("first_screen") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(120.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "Go to next screen",
                    tint = Color.Unspecified
                )
            }
        }
    }
}