package com.example.soundpathempty

import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.soundpathempty.MainActivity.Companion.current_route
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.compose.foundation.layout.Box as Box1

private lateinit var locationManager: LocationManager
private lateinit var locationListener: LocationListener
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private const val PRIORITY_HIGH_ACCURACY = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MiniRouteDialog(
    state:RouteState,
    onEvent:(RouteEvent) -> Unit,
    initialize: Boolean = false
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            onEvent(RouteEvent.HideRouteDialog)
        },
        //confirmButton = { /*TODO*/ },
        title = { Text(text = "Create route") },
        //Still need to add save button
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                TextField(
                    value = state.routeName,
                    onValueChange = {
                        //onEvent(RouteEvent.SetRouteName(it))
                        current_route = it
                    },
                    placeholder = {
                        Text(text = "Route Name")
                    }
                )
                TextField(
                    value = state.routeDescription,
                    onValueChange = {
                        onEvent(RouteEvent.SetRouteDescription(it))
                        val route_description = it
                    },
                    placeholder = {
                        Text(text = "Route Description")
                    }
                )
            }
        },
        confirmButton = {
            Box1(
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    val main = Intent(context, MainActivity::class.java)
                    context.startActivity(main)
                }) {
                    Text(text = "Back")
                }
            }
            Box1(
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    onEvent(RouteEvent.SaveRoute)
                    if(initialize){

                    }
                    val main = Intent(context, MainActivity::class.java)
                    context.startActivity(main)
                }) {
                    Text(text = "Save")
                }
            }
        }
    )
}
//fun AlertDialog(onDismissRequest: () -> Unit, title: () -> Unit, text: () -> Unit) {
