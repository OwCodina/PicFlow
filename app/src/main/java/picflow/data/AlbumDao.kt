package picflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import picflow.model.Album

/** CRUD sobre la tabla "albumes". */
@Dao
interface AlbumDao {

    @Query("SELECT * FROM albumes ORDER BY id DESC")
    fun observarTodos(): Flow<List<Album>>

    @Query("SELECT * FROM albumes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Album?

    //devuelve el id generado
    @Insert
    suspend fun insertar(album: Album): Long

    //usa el id para saber qué fila reemplazar
    @Update
    suspend fun actualizar(album: Album)

    //borra :v
    @Delete
    suspend fun eliminar(album: Album)
}
