package picflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import picflow.model.Album

@Database(
    entities = [Album::class],
    version = 1,
    exportSchema = false
)
abstract class AlbumDatabase : RoomDatabase() {

    abstract fun albumDao(): AlbumDao

    companion object {
        @Volatile
        private var INSTANCE: AlbumDatabase? = null

        fun obtenerInstancia(context: Context): AlbumDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AlbumDatabase::class.java,
                    "picflow_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
