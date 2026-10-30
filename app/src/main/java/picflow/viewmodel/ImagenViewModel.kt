package picflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import picflow.data.ImagenRepository
import picflow.model.Imagen

/** Maneja las fotos del álbum que se está viendo. */
@OptIn(ExperimentalCoroutinesApi::class)
class ImagenViewModel(private val repository: ImagenRepository) : ViewModel() {

    private val albumSeleccionado = MutableStateFlow<Int?>(null)

    /** Fotos del álbum seleccionado; se actualiza sola cuando cambia la tabla. */
    val imagenes: StateFlow<List<Imagen>> = albumSeleccionado
        .flatMapLatest { id ->
            if (id == null) flowOf<List<Imagen>>(emptyList()) else repository.observarPorAlbum(id)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun seleccionarAlbum(albumId: Int) {
        albumSeleccionado.value = albumId
    }

    /** Guarda en Room una foto ya almacenada en la carpeta de la app. */
    fun agregarFoto(albumId: Int, ruta: String) {
        viewModelScope.launch {
            repository.insertar(Imagen(albumId = albumId, rutaOriginal = ruta))
        }
    }

    fun eliminarImagen(imagen: Imagen) {
        viewModelScope.launch { repository.eliminar(imagen) }
    }

    /** Descarta la edición: vuelve a mostrarse la original y se borra el archivo editado. */
    fun restaurarOriginal(imagen: Imagen) {
        val rutaEditada = imagen.rutaEditada ?: return
        viewModelScope.launch {
            repository.actualizar(imagen.copy(rutaEditada = null))
            repository.borrarArchivo(rutaEditada)
        }
    }
}
