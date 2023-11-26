package com.example.soundpathempty

import com.example.soundpathempty.Entities.Route_Data

sealed interface RouteEvent{
    object SaveRoute: RouteEvent
    object HideRouteDialog: RouteEvent
    object ShowRouteDialog: RouteEvent
    data class SetRouteName(val routeName : String):RouteEvent
    data class SetRouteDescription(val routeDescription: String):RouteEvent
    data class DeleteRoute(val route: Route_Data):RouteEvent
    //data class ReturnHome(val conte):MarkerEvent
}