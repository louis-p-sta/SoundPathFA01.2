package com.example.soundpathempty

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.compose.foundation.layout.Box as Box1

private lateinit var locationManager: LocationManager
private lateinit var locationListener: LocationListener
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private const val PRIORITY_HIGH_ACCURACY = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AddMarkerDialog(
    state:MarkerState,
    onEvent:(MarkerEvent) -> Unit,
    modifier: Modifier = Modifier,
    lat: Double,
    lon: Double
) {
    MarkerEvent.SetLatitude(lat)
    MarkerEvent.SetLongitude(lon)
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = { /*TODO*/ },
        title = { Text(text = "Create marker") },
        //Still need to add save button
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                TextField(
                    value = state.name,
                    onValueChange = {
                        onEvent(MarkerEvent.SetName(it))
                    },
                    placeholder = {
                        Text(text = "Marker name")
                    }
                )
                TextField(
                    value = state.description,
                    onValueChange = {
                        onEvent(MarkerEvent.SetDescription(it))
                    },
                    placeholder = {
                        Text(text = "Marker name")
                    }
                )
            }
        }
    )
}
//fun AlertDialog(onDismissRequest: () -> Unit, title: () -> Unit, text: () -> Unit) {
