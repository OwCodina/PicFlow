package picflow.data

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import picflow.model.Album

/** Único punto de acceso a los álbumes: el ViewModel no conoce Room, solo este repositorio. */
class AlbumRepository(
    private val dao: AlbumDao,
    private val imagenDao: ImagenDao
) {

    val albumes: Flow<List<Album>> = dao.observarTodos()

    suspend fun obtenerPorId(id: Int): Album? = dao.obtenerPorId(id)

    suspend fun insertar(album: Album): Int = dao.insertar(album).toInt()

    suspend fun actualizar(album: Album) = dao.actualizar(album)

    suspend fun eliminar(album: Album) {
        // Primero obtenemos las fotos del álbum.
        val fotos = imagenDao.obtenerPorAlbum(album.id)

        // Después eliminamos el álbum.
        dao.eliminar(album)

        // Eliminamos la portada y los archivos de las fotografías.
        album.portadaPath?.let { borrarArchivo(it) }

        fotos.forEach { foto ->
            borrarArchivo(foto.rutaOriginal)
            foto.rutaEditada?.let { borrarArchivo(it) }
        }
    }

    /** Borra un archivo de foto del almacenamiento de la app. */
    suspend fun borrarArchivo(ruta: String) {
        withContext(Dispatchers.IO) {
            File(ruta).delete()
        }
    }
}
