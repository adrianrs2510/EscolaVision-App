package com.escolavision.testescolavision.ViewModel
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.escolavision.testescolavision.API.Centro

class RegisterViewModel : ViewModel() {
    var nombre = mutableStateOf("")
        private set
    var dni = mutableStateOf("")
        private set
    var claveAcceso = mutableStateOf("")
        private set
    var tipo = mutableStateOf("Alumno")
        private set
    var area = mutableStateOf("")
        private set
    var isAlumnoSelected = mutableStateOf(true)
        private set
    var selectedImageUri = mutableStateOf<Uri?>(null)
        private set
    var email = mutableStateOf("")
        private set
    var edad = mutableStateOf("")
        private set
    var selectedComunidad = mutableStateOf("")
        private set
    var selectedProvincia = mutableStateOf("")
        private set
    var selectedMunicipio = mutableStateOf("")
        private set
    var searchQuery = mutableStateOf("")
        private set
    var centros = mutableStateOf<List<Centro>>(emptyList())
        private set
    var centroSeleccionado = mutableStateOf(Centro("", ""))
        private set


    // Función para actualizar la búsqueda
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    // Función para actualizar los centros encontrados
    fun updateCentros(centrosList: List<Centro>?) {
        if (centrosList != null) {
            centros.value = centrosList
        }
    }

    fun updateNombre(value: String) {
        nombre.value = value
    }

    fun updateDni(value: String) {
        dni.value = value
    }

    fun updateClaveAcceso(value: String) {
        claveAcceso.value = value
    }

    fun selectAlumno() {
        tipo.value = "Alumno"
        isAlumnoSelected.value = true
    }

    fun selectProfesor() {
        tipo.value = "Profesor"
        isAlumnoSelected.value = false
    }

    fun updateArea(value: String) {
        area.value = value
    }

    fun updateImageUri(uri: Uri?) {
        selectedImageUri.value = uri
    }

    fun updateEmail(value: String) {
        email.value = value
    }

    fun updateEdad(it: String) {
        edad.value = it
    }

    fun setFilters(comunidad: String, provincia: String, localidad: String) {
        selectedComunidad.value = comunidad
        selectedProvincia.value = provincia
        selectedMunicipio.value = localidad
    }
}
