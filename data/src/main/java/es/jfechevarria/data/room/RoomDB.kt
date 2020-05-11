package es.jfechevarria.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.jfechevarria.domain.Channel

@Database(entities = [Channel::class], version = 1)
abstract class RoomDB: RoomDatabase() {
    abstract fun dataDao(): DataDao

    companion object {
        private const val DATABASE_NAME = "spain_tv_db"
        @Volatile
        private var INSTANCE: RoomDB? = null
        fun getInstance(context: Context): RoomDB? {
            INSTANCE ?: synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    DATABASE_NAME
                ).build()
            }
            return INSTANCE
        }
    }
}