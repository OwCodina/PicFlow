package picflow.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** Crea y copia archivos de fotos dentro del almacenamiento privado de la app. */
object ArchivosFotos {

    private fun carpeta(context: Context): File =
        File(context.filesDir, "fotos").apply { mkdirs() }

    /** Archivo vacío donde la cámara va a guardar una foto nueva. */
    fun crearArchivoNuevo(context: Context): File =
        File(carpeta(context), "foto_${System.currentTimeMillis()}_${(0..999).random()}.jpg")

    /** Uri que se le entrega a la app de cámara (requiere el FileProvider del manifest). */
    fun uriParaCamara(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    /** Copia una imagen elegida de la galería a la carpeta de la app. Devuelve la ruta o null si falla. */
    fun copiarDesdeUri(context: Context, uri: Uri): String? {
        val destino = crearArchivoNuevo(context)
        return try {
            val entrada = context.contentResolver.openInputStream(uri) ?: return null
            entrada.use { input -> destino.outputStream().use { salida -> input.copyTo(salida) } }
            destino.absolutePath
        } catch (e: Exception) {
            destino.delete()
            null
        }
    }
}
