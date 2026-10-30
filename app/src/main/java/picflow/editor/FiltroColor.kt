
package picflow.editor

/** Filtros de color predefinidos. Cada uno es una matriz de color 4x5. */
enum class FiltroColor(val etiqueta: String, val matriz: FloatArray) {
    ORIGINAL("Original", MatrizColor.IDENTIDAD),
    BLANCO_Y_NEGRO("Blanco y negro", MatrizColor.saturacion(0f)),
    SEPIA(
        "Sepia",
        floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    CALIDO(
        "Cálido",
        floatArrayOf(
            1.10f, 0f, 0f, 0f, 10f,
            0f, 1.00f, 0f, 0f, 0f,
            0f, 0f, 0.90f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    ),
    FRIO(
        "Frío",
        floatArrayOf(
            0.90f, 0f, 0f, 0f, 0f,
            0f, 1.00f, 0f, 0f, 0f,
            0f, 0f, 1.10f, 0f, 10f,
            0f, 0f, 0f, 1f, 0f
        )
    )
}
