/**
 * Utilidades generales de la aplicación EscolaVision.
 * 
 * Esta clase contiene funciones y componentes de utilidad:
 * - Manejo y procesamiento de imágenes
 * - Conversión de imágenes a formato Base64
 * - Compresión adaptativa de imágenes
 * - Componentes de UI reutilizables
 * - Diálogos de alerta personalizados
 * 
 * Características principales:
 * - Compresión inteligente de imágenes
 * - Límites de tamaño configurables
 * - Mantenimiento de proporción de aspecto
 * - Componentes Composable para diálogos
 * - Manejo de errores y excepciones
 * 
 * Este archivo actúa como una biblioteca de utilidades
 * compartidas que proporciona funcionalidades comunes
 * utilizadas en diferentes partes de la aplicación.
 */

 package com.escolavision.testescolavision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

// Importaciones necesarias para el manejo de imágenes y componentes de UI

@Composable
// Componente Composable que muestra un diálogo de alerta personalizado
// @param message: Mensaje a mostrar en el diálogo
// @param onDismiss: Función lambda que se ejecuta al cerrar el diálogo
fun ShowAlertDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Error") },
        text = { Text(text = message) },
        confirmButton = {
            // Botón de confirmación con estilo personalizado
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF), 
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("OK")
            }
        }
    )
}

// Función que convierte una imagen URI a formato Base64 con compresión adaptativa
// @param uri: URI de la imagen a convertir
// @param context: Contexto de la aplicación necesario para acceder al ContentResolver
// @return String?: Cadena Base64 de la imagen o null si hay error
fun imageToBase64(uri: Uri, context: Context): String? {
    // Obtiene el InputStream de la imagen desde el ContentResolver
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null

    return try {
        // Dimensiones máximas para la imagen redimensionada
        val maxWidth = 300
        val maxHeight = 300
        // Redimensiona la imagen manteniendo la proporción
        val resizedImage = Bitmap.createScaledBitmap(bitmap, maxWidth, maxHeight, true)

        val baos = ByteArrayOutputStream()
        var compressionQuality = 0.9f  // Calidad inicial de compresión
        var base64Image: String

        // Bucle de compresión adaptativa
        do {
            baos.reset()
            // Comprime la imagen con calidad progresivamente menor
            resizedImage.compress(Bitmap.CompressFormat.JPEG, (compressionQuality * 100).toInt(), baos)
            base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            compressionQuality -= 0.1f  // Reduce la calidad en cada iteración
        } while (base64Image.length > 20000 && compressionQuality > 0.1f)

        // Verifica si se alcanzó el tamaño límite
        if (base64Image.length > 20000) {
            throw IllegalArgumentException("The image cannot be compressed enough to meet the limit.")
        }

        base64Image
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}