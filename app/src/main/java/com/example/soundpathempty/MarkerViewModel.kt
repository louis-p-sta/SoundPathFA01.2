package com.example.soundpathempty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarkerViewModel(
    private val dao: MarkerDao
): ViewModel(){
    private val _state = MutableStateFlow(MarkerState())
    fun onEvent(event: MarkerEvent){
        when(event){
            is MarkerEvent.DeleteMarker -> {
                viewModelScope.launch{
                dao.deleteMarker(event.marker)
                }
            }
            MarkerEvent.SaveMarker -> {
                val name = _state.value.name
                val description = _state.value.description
                val latitude = _state.value.latitude
                val longitude = _state.value.longitude
                if(name.isBlank() || description.isBlank() || latitude == 0.0 || longitude == 0.0){
                    return
                }
                val marker  = Marker_Data(
                    name = name,
                    description = description,
                    latitude = latitude,
                    longitude = longitude
                )
                viewModelScope.launch{
                    dao.upsertMarker(marker)
                }
            }
            is MarkerEvent.SetDescription -> {
                _state.update{it.copy(
                    description = event.description
                )}
            }
            is MarkerEvent.SetLatitude -> {
                _state.update{it.copy(
                    latitude = event.latitude
                )}
            }
            is MarkerEvent.SetLongitude -> {
                _state.update{it.copy(
                    longitude = event.longitude
                )}
            }
            is MarkerEvent.SetName -> {
                _state.update{it.copy(
                    name = event.name
                )}
            }
        }
    }
}

