package com.example.soundpathempty

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

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
        setContentView(R.layout.activity_marker)
        title = "Marker"
        val loading: TextView = findViewById(R.id.loadingLocation)
        //Immediately get GPS location
        var wayLatitude = 0.0
        var wayLongitude = 0.0
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? -> //This has to be null safe
                if (location != null) {
                    wayLatitude = location.latitude
                    wayLongitude = location.longitude
                    println("Acquired marker location")
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
                }

            }
        fun getDatabase(): MarkerDatabase? {
            return db
        }

//        val receiveIntent = this.getIntent()
//        var wayLatitude = intent.getDoubleExtra("latitude",10.0)
//        var wayLongitude = intent.getDoubleExtra("longitude",10.0)

//        val menubutton: Button = findViewById(R.id.menu_marker)
//        menubutton.setOnClickListener {
//            val i = Intent(this@Marker, MainActivity::class.java)
//            startActivity(i)
//        }
    }
}