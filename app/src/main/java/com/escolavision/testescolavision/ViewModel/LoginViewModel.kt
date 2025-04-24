package com.escolavision.testescolavision.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

// ViewModel para manejar el estado del login
class LoginViewModel(private val state: SavedStateHandle) : ViewModel() {
    // Estado para el usuario
    var usuario by mutableStateOf(state.get("usuario") ?: "")
        private set

    // Estado para la contraseña
    var contraseña by mutableStateOf(state.get("contraseña") ?: "")
        private set

    // Actualiza el valor de usuario y guarda en el estado
    fun updateUsuario(value: String) {
        usuario = value
        state["usuario"] = value
    }

    // Actualiza el valor de contraseña y guarda en el estado
    fun updateContraseña(value: String) {
        contraseña = value
        state["contraseña"] = value
    }
}