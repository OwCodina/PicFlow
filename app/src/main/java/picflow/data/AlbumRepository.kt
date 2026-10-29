package picflow.data

import kotlinx.coroutines.flow.Flow
import picflow.model.Album

class AlbumRepository(
    private val albumDao: AlbumDao
) {

    fun observarTodos(): Flow<List<Album>> {
        return albumDao.observarTodos()
    }

    suspend fun obtenerPorId(id: Int): Album? {
        return albumDao.obtenerPorId(id)
    }

    suspend fun insertar(album: Album): Long {
        return albumDao.insertar(album)
    }

    suspend fun actualizar(album: Album) {
        albumDao.actualizar(album)
    }

    suspend fun eliminar(album: Album) {
        albumDao.eliminar(album)
    }
}