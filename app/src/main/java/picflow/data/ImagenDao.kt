package picflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import picflow.model.Imagen

@Dao

interface ImagenDao {
    /* Muestra los albumes en orden */
    @Query("SELECT * FROM imagenes WHERE albumId = :albumId ORDER BY id DESC")
    fun observarPorAlbum(albumId: Int): Flow<List<Imagen>>
    
    /* obtiene un album */
    @Query("SELECT * FROM imagenes WHERE albumId = :albumId")
    suspend fun obtenerPorAlbum(albumId: Int): List<Imagen>

    @Query("SELECT * FROM imagenes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Imagen?

    @Insert
    suspend fun insertar(imagen: Imagen): Long

    @Update
    suspend fun actualizar(imagen: Imagen)

    @Delete
    suspend fun eliminar(imagen: Imagen)
    
}