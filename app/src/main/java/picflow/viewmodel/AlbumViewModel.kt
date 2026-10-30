
package picflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import picflow.data.AlbumRepository
import picflow.model.Album

class AlbumViewModel(private val repository: AlbumRepository) : ViewModel() {

    var uiState by mutableStateOf(AlbumUiState())
        private set

    val albumes: StateFlow<List<Album>> = repository.albumes
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ---------- Eventos del formulario ----------

    fun actualizarNombre(valor: String) {
        uiState = uiState.copy(
            nombre = valor,
            errorNombre = null,
            mensaje = null
        )
    }

    fun actualizarDescripcion(valor: String) {
        uiState = uiState.copy(
            descripcion = valor,
            errorDescripcion = null,
            mensaje = null
        )
    }

    fun actualizarTipo(valor: String) {
        uiState = uiState.copy(
            tipo = valor,
            errorTipo = null,
            mensaje = null
        )
    }

    fun actualizarPrivado(valor: Boolean) {
        uiState = uiState.copy(esPrivado = valor, mensaje = null)
    }

    fun actualizarPortada(ruta: String?) {
        uiState = uiState.copy(portadaPath = ruta, mensaje = null)
    }

    fun prepararNuevo() {
        uiState = AlbumUiState()
    }

    fun descartarFormulario() {
        uiState = AlbumUiState()
    }

    /** Carga un álbum existente para editarlo. */
    fun cargarParaEditar(id: Int) {
        uiState = AlbumUiState(editandoId = id)

        viewModelScope.launch {
            repository.obtenerPorId(id)?.let { album ->
                uiState = AlbumUiState(
                    nombre = album.nombre,
                    descripcion = album.descripcion,
                    tipo = album.tipo,
                    esPrivado = album.esPrivado,
                    portadaPath = album.portadaPath,
                    editandoId = album.id
                )
            }
        }
    }

    // ---------- Guardar ----------

    fun guardar(onGuardado: (Int) -> Unit) {

        if (uiState.guardando) return

        val nombre = uiState.nombre.trim()
        val editandoId = uiState.editandoId

        // Validaciones
        val errorNombre = when {
            nombre.isBlank() ->
                "El nombre es obligatorio"

            nombre.length < 3 ->
                "Usa al menos 3 caracteres"

            nombre.length > 60 ->
                "Usa máximo 60 caracteres"

            albumes.value.any {
                it.id != editandoId &&
                it.nombre.equals(nombre, ignoreCase = true)
            } ->
                "Ya existe un álbum con ese nombre"

            else -> null
        }

        val errorDescripcion =
            if (uiState.descripcion.length > 240) {
                "Usa máximo 240 caracteres"
            } else null

        val errorTipo =
            if (uiState.tipo.isBlank()) {
                "Selecciona un tipo de álbum"
            } else null

        // Si existen errores, no guarda.
        if (errorNombre != null ||
            errorDescripcion != null ||
            errorTipo != null
        ) {
            uiState = uiState.copy(
                errorNombre = errorNombre,
                errorDescripcion = errorDescripcion,
                errorTipo = errorTipo,
                errorGeneral = null,
                mensaje = null
            )
            return
        }

        // Construcción del álbum.
        val album = Album(
            id = editandoId ?: 0,
            nombre = nombre,
            descripcion = uiState.descripcion.trim(),
            tipo = uiState.tipo,
            esPrivado = uiState.esPrivado,
            portadaPath = uiState.portadaPath
        )

        uiState = uiState.copy(
            guardando = true,
            errorGeneral = null,
            mensaje = null
        )

        // Guardado en Room.
        viewModelScope.launch {
            try {
                val id = if (editandoId == null) {
                    repository.insertar(album)
                } else {
                    repository.actualizar(album)
                    editandoId
                }

                uiState = AlbumUiState(
                    mensaje = if (editandoId == null) {
                        "Álbum creado correctamente"
                    } else {
                        "Álbum actualizado correctamente"
                    }
                )

                onGuardado(id)

            } catch (e: Exception) {
                uiState = uiState.copy(
                    guardando = false,
                    errorGeneral = "No se pudo guardar el álbum. Inténtalo nuevamente."
                )
            }
        }
    }

    // ---------- Eliminar ----------

    fun eliminar(album: Album, onEliminado: () -> Unit) {
        viewModelScope.launch {
            repository.eliminar(album)
            onEliminado()
        }
    }

    fun consumirMensaje() {
        uiState = uiState.copy(mensaje = null)
    }
}

