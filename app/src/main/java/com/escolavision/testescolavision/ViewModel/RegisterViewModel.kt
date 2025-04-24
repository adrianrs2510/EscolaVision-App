/**
 * ViewModel para la gestión del proceso de registro en EscolaVision.
 * 
 * Esta clase maneja el estado y la lógica del registro de usuarios:
 * - Gestión de datos personales (nombre, DNI, email, edad)
 * - Control de tipo de usuario (Alumno/Profesor)
 * - Gestión de imágenes de perfil
 * - Búsqueda y selección de centros educativos
 * - Filtros de ubicación geográfica
 * 
 * Características principales:
 * - Estados observables con MutableState
 * - Gestión de datos de registro
 * - Control de selección de centro educativo
 * - Manejo de imágenes URI
 * - Filtros por comunidad, provincia y municipio
 * - Validación de datos de usuario
 * 
 * El ViewModel actúa como intermediario entre la UI de registro
 * y la capa de datos, manteniendo el estado de la información
 * durante todo el proceso de registro de nuevos usuarios.
 */

 package com.escolavision.testescolavision.ViewModel
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.escolavision.testescolavision.API.Centro

// ViewModel que maneja los datos y estados del registro de usuarios
class RegisterViewModel : ViewModel() {
    // Información personal del usuario
    var nombre = mutableStateOf("")
        private set
    var dni = mutableStateOf("")
        private set
    var claveAcceso = mutableStateOf("")
        private set
    var email = mutableStateOf("")
        private set
    var edad = mutableStateOf("")
        private set

    // Tipo de usuario y área de especialización
    var tipo = mutableStateOf("Alumno")
        private set
    var area = mutableStateOf("")
        private set
    var isAlumnoSelected = mutableStateOf(true)
        private set

    // Imagen de perfil
    var selectedImageUri = mutableStateOf<Uri?>(null)
        private set

    // Estados para la búsqueda y selección de centro educativo
    var searchQuery = mutableStateOf("")
        private set
    var centros = mutableStateOf<List<Centro>>(emptyList())
        private set
    var centroSeleccionado = mutableStateOf(Centro("", ""))
        private set

    // Estados para filtros de ubicación
    var selectedComunidad = mutableStateOf("")
        private set
    var selectedProvincia = mutableStateOf("")
        private set
    var selectedMunicipio = mutableStateOf("")
        private set

    // Funciones de actualización para búsqueda y centros
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateCentros(centrosList: List<Centro>?) {
        if (centrosList != null) {
            centros.value = centrosList
        }
    }

    // Funciones de actualización para información personal
    fun updateNombre(value: String) {
        nombre.value = value
    }

    fun updateDni(value: String) {
        dni.value = value
    }

    fun updateClaveAcceso(value: String) {
        claveAcceso.value = value
    }

    fun updateEmail(value: String) {
        email.value = value
    }

    fun updateEdad(it: String) {
        edad.value = it
    }

    // Funciones para selección de tipo de usuario
    fun selectAlumno() {
        tipo.value = "Alumno"
        isAlumnoSelected.value = true
    }

    fun selectProfesor() {
        tipo.value = "Profesor"
        isAlumnoSelected.value = false
    }

    // Función para actualizar área de especialización
    fun updateArea(value: String) {
        area.value = value
    }

    // Función para actualizar imagen de perfil
    fun updateImageUri(uri: Uri?) {
        selectedImageUri.value = uri
    }

    // Función para actualizar filtros de ubicación
    fun setFilters(comunidad: String, provincia: String, localidad: String) {
        selectedComunidad.value = comunidad
        selectedProvincia.value = provincia
        selectedMunicipio.value = localidad
    }
}
