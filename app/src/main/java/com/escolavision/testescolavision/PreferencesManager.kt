package com.escolavision.testescolavision

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

    fun saveLogin(id: Int, tipo: String, id_centro: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("UserId", id)
        editor.putString("UserType", tipo)  // Guardamos el tipo de usuario
        editor.putString("UserIdCentro", id_centro)
        editor.putBoolean("IsLoggedIn", true)
        editor.apply() // Si necesitas resultados inmediatos, podrías usar commit()
    }

    fun clearLogin() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("IsLoggedIn", false)
    }

    fun getLoginData(): Pair<Int, String?> {
        val id = sharedPreferences.getInt("UserId", 0)
        val tipo = sharedPreferences.getString("UserType", "")
        return Pair(id, tipo)
    }

    fun saveIsOrientador(isOrientador: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("is_orientador", isOrientador)
        editor.apply()
    }

    fun getIsOrientador(): Int {
        val isOrientador = sharedPreferences.getInt("is_orientador", 0)
        return isOrientador
    }

    fun saveDarkTheme(isDark: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("isDarkTheme", isDark)
        editor.apply()
    }

    fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("isDarkTheme", false) // false es el valor predeterminado (tema claro)
    }

    fun getCenterData(): String {
        val id_centro = sharedPreferences.getString("UserIdCentro", "")
        return id_centro.toString()
    }


}

