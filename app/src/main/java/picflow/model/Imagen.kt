package picflow.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Foto dentro de un álbum. Relación ALBUM 1 — N IMAGEN.
 * Si se borra el álbum, Room borra sus filas de imágenes (CASCADE).
 * La foto original nunca se modifica: la edición se guarda en otro archivo (rutaEditada).
 */
@Entity(
    tableName = "imagenes",
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("albumId")]
)
data class Imagen(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val albumId: Int,
    val rutaOriginal: String,
    val rutaEditada: String? = null,
    val fecha: Long = System.currentTimeMillis()
) {
    /** Lo que se muestra: la versión editada si existe, si no la original. */
    val rutaVisible: String get() = rutaEditada ?: rutaOriginal
}
