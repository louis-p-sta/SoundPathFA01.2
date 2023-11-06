package com.example.soundpathempty

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.prefs.Preferences
//Imported a bunch of classes for datastore
private const val PRIORITY_HIGH_ACCURACY = 100

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class Marker : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)
        title = "Marker"
        //Immediately get GPS location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? -> //This has to be null safe
                if (location != null) {
                    var wayLatitude = location.latitude
                    var wayLongitude = location.longitude
                    println("Acquired marker location")
                }
            }
        val menubutton: Button = findViewById(R.id.menu_marker)
        menubutton.setOnClickListener {
            val i = Intent(this@Marker, MainActivity::class.java)
            startActivity(i)
        }
    }
}