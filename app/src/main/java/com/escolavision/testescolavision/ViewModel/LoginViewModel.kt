/**
 * ViewModel para la gestión del inicio de sesión en EscolaVision.
 * 
 * Esta clase maneja el estado y la lógica relacionada con la autenticación:
 * - Gestión de credenciales de usuario (usuario/DNI y contraseña)
 * - Persistencia de datos de sesión
 * - Manejo de estados de formulario de login
 * 
 * Características principales:
 * - Estados observables con MutableState
 * - Integración con SavedStateHandle para persistencia
 * - Gestión de datos de autenticación
 * - Actualización segura de credenciales
 * 
 * El ViewModel actúa como intermediario entre la UI de login
 * y la lógica de autenticación, manteniendo el estado de las
 * credenciales durante el proceso de inicio de sesión.
 */

 package com.escolavision.testescolavision.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

// ViewModel que maneja la lógica y estados del inicio de sesión
class LoginViewModel(private val state: SavedStateHandle) : ViewModel() {
    // Estado para el nombre de usuario o DNI
    // Se inicializa desde SavedStateHandle o cadena vacía si no existe
    var usuario by mutableStateOf(state.get("usuario") ?: "")
        private set

    // Estado para la contraseña del usuario
    // Se inicializa desde SavedStateHandle o cadena vacía si no existe
    var contraseña by mutableStateOf(state.get("contraseña") ?: "")
        private set

    // Función para actualizar el nombre de usuario
    // Actualiza tanto el estado local como el SavedStateHandle
    fun updateUsuario(value: String) {
        usuario = value
        state["usuario"] = value
    }

    // Función para actualizar la contraseña
    // Actualiza tanto el estado local como el SavedStateHandle
    fun updateContraseña(value: String) {
        contraseña = value
        state["contraseña"] = value
    }
}