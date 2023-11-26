package com.example.soundpathempty

import com.example.soundpathempty.Entities.Route_Data

data class RouteState(
    val routes: List<Route_Data> = emptyList(),
    val routeName: String = "",
    val routeDescription: String = "",
    val isAddingRoute: Boolean = false
)
