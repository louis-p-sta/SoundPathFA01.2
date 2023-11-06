package com.example.soundpathempty

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(
    entities = [Marker_Data::class],
    version = 1
)
abstract class MarkerDatabase: RoomDatabase() {
    abstract val dao: MarkerDao
}