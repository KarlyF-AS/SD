package com.example.iniciosimondice.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.iniciosimondice.data.Entity.RecordEntity

@Dao
interface RecordDAO {
    @Query("SELECT maxRecord FROM record WHERE id = 10")
    suspend fun getMaxRecord(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)
}
