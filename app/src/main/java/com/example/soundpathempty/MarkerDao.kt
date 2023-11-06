package com.example.soundpathempty
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface MarkerDao {
    @Upsert
    suspend fun upsertMarker(marker:Marker_Data)
    @Delete
    suspend fun deleteMarker(marker: Marker_Data)
}