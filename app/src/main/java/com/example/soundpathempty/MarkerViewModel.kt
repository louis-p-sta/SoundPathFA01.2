package com.example.soundpathempty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class MarkerViewModel(
    private val dao: MarkerDao
): ViewModel(){
    val _markers = dao.getAll()
    private val _state = MutableStateFlow(MarkerState())
    val state = combine(_state, _markers) { state, markers ->
        state.copy(
            markers = markers,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MarkerState())
    fun onEvent(event: MarkerEvent){
        when(event){
            MarkerEvent.HideDialog -> {
                _state.update {it.copy(
                    isAddingMarker = false
                ) }
            }MarkerEvent.ShowDialog -> {
            _state.update { it.copy(
                isAddingMarker = true
            ) }
        }
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
                val routeName = _state.value.routeName
//                if(name.isBlank() || description.isBlank()){
//                    return
//                }
                val marker  = Marker_Data(
                    name = name,
                    description = description,
                    latitude = latitude,
                    longitude = longitude,
                    routeName = routeName
                )
                viewModelScope.launch{
                    dao.upsertMarker(marker)
                }
                _state.update { it.copy(
                    isAddingMarker = false,
                    name = "",
                    description = "",
                    latitude = 0.0,
                    longitude = 0.0,
                    routeName = ""
                ) }
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
            is MarkerEvent.SetRoute->{
                _state.update{it.copy(
                    routeName = event.routeName
                )}
            }
        }
    }
}

