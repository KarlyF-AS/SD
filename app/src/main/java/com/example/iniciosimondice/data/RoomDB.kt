package com.example.iniciosimondice.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.iniciosimondice.data.DAO.RecordDAO
import com.example.iniciosimondice.data.Entity.RecordEntity

@Database(entities = [RecordEntity::class], version = 1)
abstract class RoomDB : RoomDatabase() {

    abstract fun recordDAO(): RecordDAO

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "record_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
