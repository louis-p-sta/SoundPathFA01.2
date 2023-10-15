package com.example.soundpathempty

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.location.LocationListener
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import com.google.android.gms.common.util.VisibleForTesting

class WhereAmI : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.positiongps)
        title = "Position GPS"
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val menubutton: Button = findViewById(R.id.menuButton)
        menubutton.setOnClickListener {
            val i = Intent(this@WhereAmI, MainActivity::class.java)
            startActivity(i)
        }
        val gpslocation: TextView = findViewById(R.id.textView)
        val getlocation: Button = findViewById(R.id.getLocation)
        getlocation.setOnClickListener {
            gpslocation.text = "Current location is \n" + "Lat : ${fusedLocationProviderClient.lastLocation.result.latitude}"
        }

        fun GPSPROVIDER(){
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val locationManager = fusedLocationProviderClient.lastLocation.result.latitude

        }
    }
}