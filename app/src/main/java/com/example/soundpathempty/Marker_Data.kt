package com.example.soundpathempty
import androidx.room.Entity
import androidx.room.PrimaryKey
//import androidx.room.vo.PrimaryKey
@Entity(tableName = "markers")
data class Marker_Data(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val description: String,
    val longitude: Double,
    val latitude: Double
)
