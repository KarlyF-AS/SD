package com.example.iniciosimondice.data.Entity

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey val id: Int = 1,
    val maxRecord: Int
)