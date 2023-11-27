package com.example.soundpathempty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.soundpathempty.Route_stuff.RoutesViewModel

class RoutesOrMarkers : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes_or_markers)
        val db = MarkerDatabase.getDatabase(this@RoutesOrMarkers)
        val markerViewModel by viewModels<MarkerViewModel>(
            factoryProducer = {
                object: ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MarkerViewModel(db.dao) as T //Might need a question mark after ViewModel
                    }
                }
            }
        )
        val routeViewModel by viewModels<RoutesViewModel>(
            factoryProducer = {
                object: ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return RoutesViewModel(db.dao) as T //Might need a question mark after ViewModel
                    }
                }
            }
        )
        val RoutesButton: Button = findViewById(R.id.Routes)
        RoutesButton.setOnClickListener{
            setContent{
                val state by routeViewModel.state.collectAsState()
                RouteScreen(
                    state = state,
                    onEvent = routeViewModel::onEvent
                )
            }

        }
        val MarkersButton: Button = findViewById(R.id.Markers)
        MarkersButton.setOnClickListener{
            setContent{
                val state by markerViewModel.state.collectAsState()
                MarkerScreen(
                    state = state,
                    onEvent = markerViewModel::onEvent,
                    lat = 1.3,
                    lon = 5.6 //TODO: remove these stray parameters
                )
            }
        }
    }
}