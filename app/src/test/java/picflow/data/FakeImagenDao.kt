
package picflow.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import picflow.model.Imagen

/** DAO falso en memoria para probar sin Android ni base de datos real. */
class FakeImagenDao : ImagenDao {
    private val datos = MutableStateFlow<List<Imagen>>(emptyList())
    private var siguienteId = 1

    override fun observarPorAlbum(albumId: Int): Flow<List<Imagen>> =
        datos.map { lista -> lista.filter { it.albumId == albumId }.sortedByDescending { it.id } }

    override suspend fun obtenerPorAlbum(albumId: Int): List<Imagen> =
        datos.value.filter { it.albumId == albumId }

    override suspend fun obtenerPorId(id: Int): Imagen? = datos.value.firstOrNull { it.id == id }

    override suspend fun insertar(imagen: Imagen): Long {
        val id = siguienteId++
        datos.value = datos.value + imagen.copy(id = id)
        return id.toLong()
    }

    override suspend fun actualizar(imagen: Imagen) {
        datos.value = datos.value.map { if (it.id == imagen.id) imagen else it }
    }

    override suspend fun eliminar(imagen: Imagen) {
        datos.value = datos.value.filter { it.id != imagen.id }
    }
}
