package com.example.soundpathempty

import kotlinx.coroutines.flow.Flow

data class MarkerState(
    val markers: List<Marker_Data> = emptyList(),
    val name: String = "",
    val routeName: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isAddingMarker: Boolean = false
)
