package com.example.soundpathempty

import android.location.Location

sealed interface MarkerEvent{
    object SaveMarker: MarkerEvent
    object HideDialog: MarkerEvent
    object ShowDialog: MarkerEvent
    data class SetName(val name : String):MarkerEvent
    data class SetDescription(val description: String):MarkerEvent
    data class SetLatitude(val latitude: Double):MarkerEvent
    data class SetLongitude(val longitude: Double):MarkerEvent
    data class SetLocation(val location : Location): MarkerEvent
    data class DeleteMarker(val marker: Marker_Data):MarkerEvent

}