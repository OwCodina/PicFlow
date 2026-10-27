package picflow.data

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import picflow.model.Album

/** Único punto de acceso a los datos: el ViewModel no conoce Room, solo este repositorio. */
class AlbumRepository(private val dao: AlbumDao) {

    val albumes: Flow<List<Album>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Int): Album? = dao.obtenerPorId(id)

    suspend fun insertar(album: Album): Int = dao.insertar(album).toInt()

    suspend fun actualizar(album: Album) = dao.actualizar(album)

    suspend fun eliminar(album: Album) {
        dao.eliminar(album)
        // Si el álbum tenía foto, también se borra el archivo para no dejar basura
        album.portadaPath?.let { ruta ->
            withContext(Dispatchers.IO) { File(ruta).delete() }
        }
    }
}
