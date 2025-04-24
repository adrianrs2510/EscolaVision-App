package com.escolavision.testescolavision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController

// Actividad principal que sirve como punto de entrada de la aplicación
class MainActivity : ComponentActivity() {
    // Método onCreate que se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configura el contenido de la actividad usando Jetpack Compose
        setContent {
            // Aplica el tema Material Design a toda la aplicación
            MaterialTheme {
                // Crea y recuerda una instancia del controlador de navegación
                val navController = rememberNavController()
                
                // Superficie principal que aplica el color de fondo del tema
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Componente de navegación que maneja todas las rutas de la aplicación
                    NavigationComponent(navController)
                }
            }
        }
    }
}
