package com.example.soundpathempty

import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme
import com.google.android.gms.location.FusedLocationProviderClient

//Imported a bunch of classes for datastore
private const val PRIORITY_HIGH_ACCURACY = 100

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class Marker : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val db by lazy{
        Room.databaseBuilder(applicationContext,MarkerDatabase::class.java, "Markers.db").build()
    }
    private val viewModel by viewModels<MarkerViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MarkerViewModel(db.dao) as T //Might need a question mark after ViewModel
                }
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_marker)
        title = "Marker"
        //Immediately get GPS location
        val wayLatitude = intent.getDoubleExtra("latitude",10.0)
        val wayLongitude = intent.getDoubleExtra("longitude",10.0)
        setContent {
            SoundPathEmptyTheme {
                val state by viewModel.state.collectAsState()
                MarkerScreen(
                    state = state,
                    lat = wayLatitude,
                    onEvent = viewModel::onEvent,
                    lon = wayLongitude
                )
            }
        }
//        val menubutton: Button = findViewById(R.id.menu_marker)
//        menubutton.setOnClickListener {
//            val i = Intent(this@Marker, MainActivity::class.java)
//            startActivity(i)
//        }
    }
}