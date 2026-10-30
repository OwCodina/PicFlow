
package picflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import picflow.data.ImagenRepository
import picflow.editor.FiltroColor
import picflow.editor.MatrizColor
import picflow.editor.ProcesadorImagen
import picflow.editor.Recorte
import picflow.model.Imagen

/**
 * Lógica del editor: mantiene recorte, rotación y color, y genera el archivo editado.
 * La foto original no se toca: el resultado se guarda en otro archivo (rutaEditada).
 */
class EditorViewModel(private val repository: ImagenRepository) : ViewModel() {

    var uiState by mutableStateOf(EditorUiState())
        private set

    fun cargar(imagenId: Int) {
        if (uiState.imagen != null) return
        viewModelScope.launch {
            val imagen = repository.obtenerPorId(imagenId)
            if (imagen == null) {
                uiState = uiState.copy(cargando = false, error = "No se encontró la imagen")
                return@launch
            }
            abrirOriginal(imagen)
        }
    }

    private suspend fun abrirOriginal(imagen: Imagen) {
        val bitmap = withContext(Dispatchers.Default) {
            ProcesadorImagen.decodificar(imagen.rutaOriginal)
        }
        uiState = if (bitmap == null) {
            EditorUiState(cargando = false, imagen = imagen, error = "No se pudo abrir la imagen")
        } else {
            EditorUiState(cargando = false, imagen = imagen, bitmap = bitmap)
        }
    }

    fun actualizarIzquierda(v: Float) =
        actualizarRecorte { it.copy(izquierda = v.coerceIn(0f, Recorte.MAXIMO)) }

    fun actualizarDerecha(v: Float) =
        actualizarRecorte { it.copy(derecha = v.coerceIn(0f, Recorte.MAXIMO)) }

    fun actualizarArriba(v: Float) =
        actualizarRecorte { it.copy(arriba = v.coerceIn(0f, Recorte.MAXIMO)) }

    fun actualizarAbajo(v: Float) =
        actualizarRecorte { it.copy(abajo = v.coerceIn(0f, Recorte.MAXIMO)) }

    private fun actualizarRecorte(cambio: (Recorte) -> Recorte) {
        uiState = uiState.copy(recorte = cambio(uiState.recorte))
    }

    fun rotar() {
        val actual = uiState.bitmap ?: return
        viewModelScope.launch {
            val girada = withContext(Dispatchers.Default) {
                ProcesadorImagen.rotar(actual, 90f)
            }
            uiState = uiState.copy(bitmap = girada, recorte = Recorte())
        }
    }

    fun actualizarBrillo(v: Float) {
        uiState = uiState.copy(brillo = v.coerceIn(-1f, 1f))
    }

    fun actualizarContraste(v: Float) {
        uiState = uiState.copy(contraste = v.coerceIn(0.5f, 1.5f))
    }

    fun actualizarSaturacion(v: Float) {
        uiState = uiState.copy(saturacion = v.coerceIn(0f, 2f))
    }

    fun elegirFiltro(filtro: FiltroColor) {
        uiState = uiState.copy(filtro = filtro)
    }

    fun restablecer() {
        val imagen = uiState.imagen ?: return
        uiState = EditorUiState(cargando = true, imagen = imagen)
        viewModelScope.launch { abrirOriginal(imagen) }
    }

    fun guardar(onGuardado: () -> Unit) {
        val estado = uiState
        val imagen = estado.imagen ?: return
        val bitmap = estado.bitmap ?: return
        if (estado.guardando) return

        uiState = estado.copy(guardando = true, error = null)
        viewModelScope.launch {
            try {
                val nuevaRuta = withContext(Dispatchers.Default) {
                    val rectangulo = estado.recorte.aRectangulo(bitmap.width, bitmap.height)
                    val matriz = MatrizColor.componer(
                        estado.filtro, estado.brillo, estado.contraste, estado.saturacion
                    )
                    val resultado = ProcesadorImagen.aplicar(bitmap, rectangulo, matriz)
                    val carpeta = File(imagen.rutaOriginal).parentFile
                        ?: throw IllegalStateException("Carpeta de fotos no disponible")
                    val archivo = File(
                        carpeta,
                        "editada_${imagen.id}_${System.currentTimeMillis()}.jpg"
                    )
                    ProcesadorImagen.guardarJpeg(resultado, archivo)
                    archivo.absolutePath
                }
                repository.actualizar(imagen.copy(rutaEditada = nuevaRuta))
                imagen.rutaEditada?.let { repository.borrarArchivo(it) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                uiState = uiState.copy(
                    guardando = false,
                    error = "No se pudo guardar la edición"
                )
                return@launch
            }
            uiState = uiState.copy(guardando = false)
            onGuardado()
        }
    }
}
