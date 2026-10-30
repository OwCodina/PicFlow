
package picflow.editor

/**
 * Matrices de color 4x5 (R, G, B, A) guardadas por filas en un FloatArray de 20 valores.
 * Es la misma convención de android.graphics.ColorMatrix y de Compose, por eso la misma matriz
 * sirve para la vista previa y para generar la imagen final. Es Kotlin puro: se puede probar sin Android.
 */
object MatrizColor {

    val IDENTIDAD: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    /** s = 0 deja la imagen en grises, 1 la deja igual y más de 1 aumenta los colores. */
    fun saturacion(s: Float): FloatArray {
        val inv = 1f - s
        val r = inv * 0.213f
        val g = inv * 0.715f
        val b = inv * 0.072f
        return floatArrayOf(
            r + s, g, b, 0f, 0f,
            r, g + s, b, 0f, 0f,
            r, g, b + s, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    }

    /**
     * brillo: de -1 a 1 (suma o resta luz). contraste: 1 = sin cambio, menos de 1 lo baja y más de 1 lo sube.
     */
    fun brilloContraste(brillo: Float, contraste: Float): FloatArray {
        val desplazamiento = (1f - contraste) * 128f + brillo * 100f
        return floatArrayOf(
            contraste, 0f, 0f, 0f, desplazamiento,
            0f, contraste, 0f, 0f, desplazamiento,
            0f, 0f, contraste, 0f, desplazamiento,
            0f, 0f, 0f, 1f, 0f
        )
    }

    /** Combina dos matrices: se aplica primero [segunda] y después [primera]. */
    fun multiplicar(primera: FloatArray, segunda: FloatArray): FloatArray {
        val resultado = FloatArray(20)
        for (fila in 0..3) {
            for (columna in 0..4) {
                var suma = 0f
                for (k in 0..3) suma += primera[fila * 5 + k] * segunda[k * 5 + columna]
                if (columna == 4) suma += primera[fila * 5 + 4]
                resultado[fila * 5 + columna] = suma
            }
        }
        return resultado
    }

    /** Orden de aplicación: saturación → brillo y contraste → filtro. */
    fun componer(filtro: FiltroColor, brillo: Float, contraste: Float, saturacion: Float): FloatArray {
        val ajuste = multiplicar(brilloContraste(brillo, contraste), saturacion(saturacion))
        return multiplicar(filtro.matriz, ajuste)
    }

    /** Aplica la matriz a un color (0 a 255). Se usa en las pruebas. */
    fun aplicar(matriz: FloatArray, r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
        fun canal(fila: Int) =
            (matriz[fila * 5] * r + matriz[fila * 5 + 1] * g + matriz[fila * 5 + 2] * b + matriz[fila * 5 + 4])
                .coerceIn(0f, 255f)
        return Triple(canal(0), canal(1), canal(2))
    }
}
