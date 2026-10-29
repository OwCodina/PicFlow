
package picflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import picflow.model.Album

@Dao
interface AlbumDao {

    @Query("SELECT * FROM albumes ORDER BY id DESC")
    fun observarTodos(): Flow<List<Album>>

    @Query("SELECT * FROM albumes WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Album?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(album: Album): Long

    @Update
    suspend fun actualizar(album: Album)

    @Delete
    suspend fun eliminar(album: Album)
}
