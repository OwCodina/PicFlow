package picflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import picflow.model.Album
import picflow.model.Imagen

@Database(entities = [Album::class, Imagen::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun albumDao(): AlbumDao
    abstract fun ImagenDao(): ImagenDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        // Una sola base de datos para toda la app (singleton)
        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "picflow.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { instancia = it }
            }
    }
}
