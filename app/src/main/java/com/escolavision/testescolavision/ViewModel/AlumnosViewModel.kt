/**
 * ViewModel para la gestión de datos de alumnos y profesores en EscolaVision.
 * 
 * Esta clase maneja el estado y la lógica de negocio relacionada con:
 * - Datos personales de usuarios (nombre, DNI, email)
 * - Credenciales de acceso
 * - Tipo de usuario (alumno/profesor)
 * - Gestión de imágenes de perfil
 * - Estados de UI para formularios
 * 
 * Características principales:
 * - Estados observables con MutableState
 * - Gestión de datos de perfil
 * - Control de tipos de usuario
 * - Manejo de imágenes URI
 * - Validación de datos de usuario
 * 
 * El ViewModel actúa como intermediario entre la UI y
 * la capa de datos, manteniendo el estado de la información
 * del usuario durante el ciclo de vida de la aplicación.
 */

 package com.escolavision.testescolavision.ViewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// ViewModel que maneja los datos y estados de los alumnos
class AlumnosViewModel : ViewModel() {

    // Nombre del alumno o profesor
    var nombre = mutableStateOf("")
        private set

    // DNI del usuario
    var dni = mutableStateOf("")
        private set

    // Contraseña de acceso
    var claveAcceso = mutableStateOf("")
        private set

    // Tipo de usuario (por defecto "alumno")
    var tipo = mutableStateOf("alumno")
        private set

    // Área de especialización (para profesores)
    var area = mutableStateOf("")
        private set

    // Estado que indica si el usuario seleccionado es alumno
    var isAlumnoSelected = mutableStateOf(true)
        private set

    // URI de la imagen de perfil seleccionada
    var selectedImageUri = mutableStateOf<Uri?>(null)
        private set

    // Correo electrónico del usuario
    var email = mutableStateOf("")
        private set

    // Edad o fecha de nacimiento del usuario
    var edad = mutableStateOf("")
        private set

    // Función para actualizar la URI de la imagen de perfil
    fun updateImageUri(uri: Uri?) {
        selectedImageUri.value = uri
    }
}