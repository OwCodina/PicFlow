package picflow.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import picflow.model.Album

/** DAO falso en memoria: permite probar el ViewModel sin Android ni base de datos real. */
class FakeAlbumDao : AlbumDao {
    private val datos = MutableStateFlow<List<Album>>(emptyList())
    private var siguienteId = 1

    override fun observarTodos(): Flow<List<Album>> = datos

    override suspend fun obtenerPorId(id: Int): Album? = datos.value.firstOrNull { it.id == id }

    override suspend fun insertar(album: Album): Long {
        val id = siguienteId++
        datos.value = datos.value + album.copy(id = id)
        return id.toLong()
    }

    override suspend fun actualizar(album: Album) {
        datos.value = datos.value.map { if (it.id == album.id) album else it }
    }

    override suspend fun eliminar(album: Album) {
        datos.value = datos.value.filter { it.id != album.id }
    }
}
