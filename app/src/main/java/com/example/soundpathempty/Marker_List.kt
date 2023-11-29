package com.example.soundpathempty

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soundpathempty.MainActivity.Companion.markerTrack
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme

private const val PRIORITY_HIGH_ACCURACY = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MarkerScreen(
    state: MarkerState,
    onEvent: (MarkerEvent) -> Unit,
    lat : Double,
    lon : Double,
) {
    val context = LocalContext.current
    val main = Intent(context, MainActivity::class.java)
    SoundPathEmptyTheme {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                TopAppBar(
                    title = { Text("Marker List") },
                )
            },
            bottomBar = {

                BottomAppBar(
                    containerColor = Color.Transparent
                ) {
                    Button(
                        onClick = { context.startActivity(main) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)

                    ) {
                        Text(text = "Back", fontSize = 30.sp, color = Color.Black,)

                    }
                }
            }
        )
        { padding ->
            if (state.isAddingMarker) {
                AddMarkerDialog(
                    state = state,
                    onEvent = onEvent,
                    lat = lat,
                    lon = lon,
                    routeName = "void"
                )
            }

            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(state.markers) { marker ->
                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .border(border = BorderStroke(1.dp, Color.White))
                            .clickable {
                                markerTrack = marker
                                context.startActivity(main)
                            }

                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Box (
                                modifier = Modifier.fillMaxWidth(),
                            ){

                                Text(
                                    text = "${marker.name}",
                                    fontSize = 30.sp, color = Color.White
                                )

                                //Text(text = "lat: ${marker.latitude} lon: ${marker.longitude}", color=Color.White)

                            }

                        }
                        Column {
                            Text(
                                text = " ${marker.description}",
                                fontSize = 15.sp, color = Color.White
                            )
                        }
                        Column {
                            IconButton(onClick = {
                                onEvent(MarkerEvent.DeleteMarker(marker))
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.deletebuttonwhite),
                                    contentDescription = "Delete Marker",
                                    modifier = Modifier.size(75.dp),
                                    tint = Color.White
                                )

                            }
                        }

                    }
                }

            }

        }
    }
}