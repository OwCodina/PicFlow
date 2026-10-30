package picflow.data

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import picflow.model.Imagen

/** Acceso a las fotos de los álbumes. El ViewModel no conoce Room, solo este repositorio. */
class ImagenRepository(private val dao: ImagenDao) {

    fun observarPorAlbum(albumId: Int): Flow<List<Imagen>> =
        dao.observarPorAlbum(albumId)

    suspend fun obtenerPorId(id: Int): Imagen? =
        dao.obtenerPorId(id)

    suspend fun insertar(imagen: Imagen): Int =
        dao.insertar(imagen).toInt()

    suspend fun actualizar(imagen: Imagen) =
        dao.actualizar(imagen)

    /** Borra la fila y también los archivos (original y editada) para no dejar basura. */
    suspend fun eliminar(imagen: Imagen) {
        dao.eliminar(imagen)
        borrarArchivo(imagen.rutaOriginal)
        imagen.rutaEditada?.let { borrarArchivo(it) }
    }

    suspend fun borrarArchivo(ruta: String) {
        withContext(Dispatchers.IO) {
            File(ruta).delete()
        }
    }
}
