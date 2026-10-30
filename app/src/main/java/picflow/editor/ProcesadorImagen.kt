package picflow.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

/** Operaciones sobre bitmaps (decodificar, rotar, recortar, aplicar color y guardar). */
object ProcesadorImagen {

    /**
     * Abre la imagen reducida (máximo [maxLado] px) para no quedarse sin memoria
     * y la deja derecha según los datos EXIF de la cámara.
     */
    fun decodificar(ruta: String, maxLado: Int = 2048): Bitmap? {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, medidas)
        if (medidas.outWidth <= 0 || medidas.outHeight <= 0) return null

        var reduccion = 1
        while (max(medidas.outWidth, medidas.outHeight) / reduccion > maxLado) reduccion *= 2

        val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
        val bitmap = BitmapFactory.decodeFile(ruta, opciones) ?: return null
        return corregirOrientacion(bitmap, ruta)
    }

    private fun corregirOrientacion(bitmap: Bitmap, ruta: String): Bitmap {
        val orientacion = try {
            ExifInterface(ruta).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val grados = when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        return if (grados == 0f) bitmap else rotar(bitmap, grados)
    }

    fun rotar(bitmap: Bitmap, grados: Float): Bitmap {
        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }

    /** Recorta el rectángulo indicado y le aplica la matriz de color. */
    fun aplicar(origen: Bitmap, rectangulo: RectanguloPx, matriz: FloatArray): Bitmap {
        val recortado = Bitmap.createBitmap(
            origen, rectangulo.izquierda, rectangulo.arriba, rectangulo.ancho, rectangulo.alto
        )
        val resultado = Bitmap.createBitmap(recortado.width, recortado.height, Bitmap.Config.ARGB_8888)
        val pintura = Paint().apply { colorFilter = ColorMatrixColorFilter(ColorMatrix(matriz)) }
        Canvas(resultado).drawBitmap(recortado, 0f, 0f, pintura)
        return resultado
    }

    fun guardarJpeg(bitmap: Bitmap, archivo: File, calidad: Int = 92) {
        FileOutputStream(archivo).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, salida)
        }
    }
}
