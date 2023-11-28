package com.example.soundpathempty

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.soundpathempty.MainActivity.Companion.running_route

private const val PRIORITY_HIGH_ACCURACY = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun RouteScreen(
    state: RouteState,
    onEvent: (RouteEvent) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
    ){ padding ->
        if(state.isAddingRoute) {
            AddRouteDialog(state = state, onEvent = onEvent)
        }
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

            items(state.routes){route ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ){
                        Box(
                            modifier = Modifier.clickable{
                                running_route = route.routeName
                                val direction = Intent(context, Forwards_or_backwards::class.java)
                                context.startActivity(direction)
                            }
                        ){
                            Text(
                                text = "${route.routeName} \n ${route.routeDescription}"
                            )
                        }
                    }
                    IconButton(onClick = {
                        onEvent(RouteEvent.DeleteRoute(route))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Marker"
                        )
                    }
                }

            }
            item{
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            val main = Intent(context, MainActivity::class.java)
                            context.startActivity(main)
                        }) {
                            Text(text = "Back")
                        }
                    }
                }
            }

        }

    }
}