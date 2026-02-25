package com.example.iniciosimondice.data.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey val id: Int = 1,
    val maxRecord: Int
)