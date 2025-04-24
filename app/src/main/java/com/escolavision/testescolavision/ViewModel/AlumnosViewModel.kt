package com.escolavision.testescolavision.ViewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AlumnosViewModel : ViewModel() {

    var nombre = mutableStateOf("")
        private set
    var dni = mutableStateOf("")
        private set
    var claveAcceso = mutableStateOf("")
        private set
    var tipo = mutableStateOf("alumno")
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

    fun updateImageUri(uri: Uri?) {
        selectedImageUri.value = uri
    }

}