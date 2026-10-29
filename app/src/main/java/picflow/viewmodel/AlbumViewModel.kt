package picflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import picflow.data.AlbumRepository
import picflow.model.Album

data class AlbumUiState(
    val mensaje: String = ""
)

class AlbumViewModel(
    private val repository: AlbumRepository
) : ViewModel() {

    val albumes: StateFlow<List<Album>> =
        repository.observarTodos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var uiState = AlbumUiState()
        private set

    fun guardar(
        album: Album,
        onGuardado: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val id = if (album.id == 0) {
                    repository.insertar(album)
                } else {
                    repository.actualizar(album)
                    album.id.toLong()
                }

                uiState = AlbumUiState(
                    mensaje = "Álbum guardado correctamente"
                )
                onGuardado(id)
            } catch (e: Exception) {
                uiState = AlbumUiState(
                    mensaje = "Error al guardar el álbum"
                )
            }
        }
    }

    fun eliminar(album: Album) {
        viewModelScope.launch {
            try {
                repository.eliminar(album)
                uiState = AlbumUiState(
                    mensaje = "Álbum eliminado correctamente"
                )
            } catch (e: Exception) {
                uiState = AlbumUiState(
                    mensaje = "Error al eliminar el álbum"
                )
            }
        }
    }

    fun limpiarMensaje() {
        uiState = AlbumUiState()
    }
}