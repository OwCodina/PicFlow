package picflow.editor

import kotlin.math.roundToInt

/** Rectángulo en píxeles dentro de la imagen. */
data class RectanguloPx(val izquierda: Int, val arriba: Int, val ancho: Int, val alto: Int)

/**
 * Recorte expresado como fracción (0.0 a 0.45) que se quita de cada borde.
 * Con un máximo de 45% por lado siempre queda al menos un 10% de la imagen.
 */
data class Recorte(
    val izquierda: Float = 0f,
    val derecha: Float = 0f,
    val arriba: Float = 0f,
    val abajo: Float = 0f
) {
    val activo: Boolean
        get() = izquierda > 0f || derecha > 0f || arriba > 0f || abajo > 0f

    /** Convierte las fracciones al rectángulo real, nunca de tamaño cero. */
    fun aRectangulo(anchoImagen: Int, altoImagen: Int): RectanguloPx {
        val l = (anchoImagen * izquierda).roundToInt().coerceIn(0, anchoImagen - 1)
        val t = (altoImagen * arriba).roundToInt().coerceIn(0, altoImagen - 1)
        val r = (anchoImagen * (1f - derecha)).roundToInt().coerceIn(l + 1, anchoImagen)
        val b = (altoImagen * (1f - abajo)).roundToInt().coerceIn(t + 1, altoImagen)
        return RectanguloPx(l, t, r - l, b - t)
    }

    companion object {
        const val MAXIMO = 0.45f
    }
}