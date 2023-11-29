package com.example.soundpathempty

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soundpathempty.MainActivity.Companion.running_route
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme

private const val PRIORITY_HIGH_ACCURACY = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun RouteScreen(
    state: RouteState,
    onEvent: (RouteEvent) -> Unit
) {
    val context = LocalContext.current
    val main = Intent(context, MainActivity::class.java)

    SoundPathEmptyTheme {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                TopAppBar(
                    title = {Text("Route List")},
                )
            },
            bottomBar = {

                BottomAppBar (
                    containerColor = Color.Transparent
                ){
                    Button(onClick = { context.startActivity(main) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)

                    ) {
                        Text(text = "Back", fontSize = 30.sp, color = Color.Black,)

                    }
                }
            }
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
                        modifier = Modifier
                            .fillMaxSize()
                            .border(border = BorderStroke(1.dp, Color.White))
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
                                    text = "${route.routeName} \n ${route.routeDescription}",fontSize = 30.sp, color = Color.White
                                )
                            }
                        }
                        IconButton(onClick = {
                            onEvent(RouteEvent.DeleteRoute(route))
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
//                item{
//                    Row(
//                        modifier = Modifier.fillMaxWidth()
//                    ){
//                        Box(
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Button(onClick = {
//                                val main = Intent(context, MainActivity::class.java)
//                                context.startActivity(main)
//                            }) {
//                                Text(text = "Back")
//                            }
//                        }
//                    }
//                }

            }

        }
    }
}