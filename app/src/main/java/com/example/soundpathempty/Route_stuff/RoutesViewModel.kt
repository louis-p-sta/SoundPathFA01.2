package com.example.soundpathempty.Route_stuff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundpathempty.Entities.Route_Data
import com.example.soundpathempty.MarkerDao
import com.example.soundpathempty.RouteEvent
import com.example.soundpathempty.RouteState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class RoutesViewModel(
    private val dao: MarkerDao
): ViewModel(){
    val _routes = dao.getAllRoutesDisplay()
    private val _state = MutableStateFlow(RouteState())
    val state = combine(_state, _routes) { state, routes ->
        state.copy(
            routes = routes,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RouteState())
    fun onEvent(event: RouteEvent){
        when(event){
            is RouteEvent.HideRouteDialog -> {
                _state.update {it.copy(
                    isAddingRoute = false
                ) }
            }
            is RouteEvent.ShowRouteDialog -> {
            _state.update { it.copy(
                isAddingRoute = true
            ) }
            }
            is RouteEvent.DeleteRoute -> {
                viewModelScope.launch{
                dao.deleteRoute(event.route)
                }
            }
            is RouteEvent.SaveRoute -> {
                val routeName = _state.value.routeName
                val routeDescription = _state.value.routeDescription
//                if(name.isBlank() || description.isBlank()){
//                    return
//                }
                val route  = Route_Data(
                    routeName = routeName,
                    routeDescription = routeDescription
                )
                viewModelScope.launch{
                    dao.upsertRoute(route)
                }
                _state.update { it.copy(
                    isAddingRoute = false,
                    routeName = "",
                    routeDescription = ""
                ) }
            }
            is RouteEvent.SetRouteDescription -> {
                _state.update{it.copy(
                    routeDescription = event.routeDescription
                )}
            }
            is RouteEvent.SetRouteName -> {
                _state.update{it.copy(
                    routeName = event.routeName
                )}
            }

            else -> {}//TODO: Fix this when thing
        }
    }
}

