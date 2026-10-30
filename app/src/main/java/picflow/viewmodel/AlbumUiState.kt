package picflow.viewmodel

/** Estado del formulario. editandoId != null significa que se está editando un álbum existente. */
data class AlbumUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val tipo: String = "",
    val esPrivado: Boolean = false,
    val portadaPath: String? = null,
    val editandoId: Int? = null,
    val guardando: Boolean = false,
    val errorNombre: String? = null,
    val errorDescripcion: String? = null,
    val errorTipo: String? = null,
    val errorGeneral: String? = null,
    val mensaje: String? = null
)
