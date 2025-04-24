package com.escolavision.testescolavision

import android.content.Context
import android.content.SharedPreferences

// Clase que maneja el almacenamiento y recuperación de preferencias del usuario
class PreferencesManager(context: Context) {
    // Instancia de SharedPreferences para almacenar datos de forma persistente
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

    // Guarda los datos de inicio de sesión del usuario
    fun saveLogin(id: Int, tipo: String, id_centro: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("UserId", id)          // ID del usuario
        editor.putString("UserType", tipo)    // Tipo de usuario (alumno/profesor)
        editor.putString("UserIdCentro", id_centro)  // ID del centro educativo
        editor.putBoolean("IsLoggedIn", true)  // Estado de inicio de sesión
        editor.apply()
    }

    // Limpia todos los datos de sesión almacenados
    fun clearLogin() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // Verifica si el usuario ha iniciado sesión
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("IsLoggedIn", false)
    }

    // Obtiene los datos básicos del usuario (ID y tipo)
    fun getLoginData(): Pair<Int, String?> {
        val id = sharedPreferences.getInt("UserId", 0)
        val tipo = sharedPreferences.getString("UserType", "")
        return Pair(id, tipo)
    }

    // Guarda el estado de orientador del usuario
    fun saveIsOrientador(isOrientador: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("is_orientador", isOrientador)
        editor.apply()
    }

    // Obtiene el estado de orientador del usuario
    fun getIsOrientador(): Int {
        return sharedPreferences.getInt("is_orientador", 0)
    }

    // Guarda la preferencia del tema oscuro
    fun saveDarkTheme(isDark: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("isDarkTheme", isDark)
        editor.apply()
    }

    // Obtiene la preferencia del tema oscuro
    fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("isDarkTheme", false)
    }

    // Obtiene el ID del centro educativo del usuario
    fun getCenterData(): String {
        val id_centro = sharedPreferences.getString("UserIdCentro", "")
        return id_centro.toString()
    }
}

