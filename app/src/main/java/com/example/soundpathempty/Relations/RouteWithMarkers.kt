package com.example.soundpathempty.Relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.soundpathempty.Entities.Route_Data
import com.example.soundpathempty.Marker_Data

data class RouteWithMarkers (
    @Embedded val route: Route_Data,
    @Relation(
        parentColumn = "routeName",
        entityColumn = "routeName"
    )
    val markers: List<Marker_Data>
)