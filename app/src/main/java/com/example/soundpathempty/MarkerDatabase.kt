package com.example.soundpathempty

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.soundpathempty.Entities.Route_Data

@Database(
    entities = [Marker_Data::class,
               Route_Data::class],
    version = 1
)
abstract class MarkerDatabase: RoomDatabase() {

    abstract val dao: MarkerDao
    companion object {
        @Volatile
        private var INSTANCE: MarkerDatabase? = null //TODO: Read a singleton article.

        fun getDatabase(context: Context): MarkerDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }
        private fun buildDatabase(context: Context): MarkerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MarkerDatabase::class.java,
                "Markers.db"
            )
                //.addMigrations(MIGRATION_1_2)
                .build().also{
                    INSTANCE = it
                }
        }
        }
    }