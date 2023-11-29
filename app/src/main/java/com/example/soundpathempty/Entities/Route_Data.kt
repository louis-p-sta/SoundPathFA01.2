package com.example.soundpathempty.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
//import com.example.soundpathempty.Marker_Data

@Entity(tableName = "routes")
data class Route_Data(
    @PrimaryKey(autoGenerate = false)
    val routeName:String,
    val routeDescription: String
)