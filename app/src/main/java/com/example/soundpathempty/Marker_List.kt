package com.example.soundpathempty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
private const val PRIORITY_HIGH_ACCURACY = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MarkerScreen(
    state: MarkerState,
    onEvent: (MarkerEvent) -> Unit
) {
    Scaffold(){ padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            items(state.markers){marker ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ){
                        Text(
                            text = "${marker.name} ${marker.description}"
                        )
                        Text(text = "lat: ${marker.latitude} lon: ${marker.longitude}")
                    }
                }

            }
        }

    }
}