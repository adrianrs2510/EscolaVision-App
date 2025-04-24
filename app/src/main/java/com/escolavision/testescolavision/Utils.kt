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

@Composable
fun ShowAlertDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Error") },
        text = { Text(text = message) },
        confirmButton = {
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

// Function to convert image to Base64 string
fun imageToBase64(uri: Uri, context: Context): String? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null

    return try {
        val maxWidth = 300
        val maxHeight = 300
        val resizedImage = Bitmap.createScaledBitmap(bitmap, maxWidth, maxHeight, true)

        val baos = ByteArrayOutputStream()
        var compressionQuality = 0.9f
        var base64Image: String

        do {
            baos.reset()
            resizedImage.compress(Bitmap.CompressFormat.JPEG, (compressionQuality * 100).toInt(), baos)
            base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            compressionQuality -= 0.1f
        } while (base64Image.length > 20000 && compressionQuality > 0.1f)

        if (base64Image.length > 20000) {
            throw IllegalArgumentException("The image cannot be compressed enough to meet the limit.")
        }

        base64Image
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}