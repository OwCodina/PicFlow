
package picflow.viewmodel

import android.graphics.Bitmap
import picflow.editor.FiltroColor
import picflow.editor.Recorte
import picflow.model.Imagen

/** Estado del editor de imágenes. */
data class EditorUiState(
    val cargando: Boolean = true,
    val imagen: Imagen? = null,
    val bitmap: Bitmap? = null, // imagen de trabajo (ya rotada si el usuario la giró)
    val recorte: Recorte = Recorte(),
    val brillo: Float = 0f,       // -1 a 1
    val contraste: Float = 1f,    // 0.5 a 1.5
    val saturacion: Float = 1f,   // 0 a 2
    val filtro: FiltroColor = FiltroColor.ORIGINAL,
    val guardando: Boolean = false,
    val error: String? = null
)
