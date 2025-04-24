package com.escolavision.testescolavision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavigationComponent(navController)
                }
            }
        }
    }
}
