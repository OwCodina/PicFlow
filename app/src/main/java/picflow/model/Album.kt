package picflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Entidad de Room: cada Album es una fila de la tabla "albumes". */
@Entity(tableName = "albumes")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val esPrivado: Boolean,
    val portadaPath: String? = null // ruta de la foto tomada con la cámara (opcional)
)
