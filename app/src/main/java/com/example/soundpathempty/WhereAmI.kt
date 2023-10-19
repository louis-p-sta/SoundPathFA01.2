package com.example.soundpathempty

import android.Manifest
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.String
import kotlin.Exception

class WhereAmI : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.positiongps)
        title = "Position GPS"
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //Need a location request, since location is null
        val menubutton: Button = findViewById(R.id.menuButton)
        menubutton.setOnClickListener {
            val i = Intent(this@WhereAmI, MainActivity::class.java)
            startActivity(i)
        }
        val gpslocation: TextView = findViewById(R.id.textView)
        val getlocation: Button = findViewById(R.id.getLocation)
        getlocation.setOnClickListener {
            try {
                //gpslocation.text =
                   // "Current location is \n" + "Lat : ${fusedLocationProviderClient.lastLocation.result.latitude}"
                //println("${fusedLocationProviderClient.getCurrentLocation(1,)}")
                //println("${fusedLocationProviderClient.lastLocation.result.latitude}")
                var wayLatitude : Double = 10.0
                var wayLongitude: Double = 10.0
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this) { location ->
                    println("Location : $location")
                    if (location != null) {
                        wayLatitude = location.getLatitude()
                        wayLongitude = location.getLongitude()
                    }
                }

                println("$wayLatitude")
                gpslocation.text = "Current location is \n" + "Lat : ${wayLatitude}"
            }catch(e:Exception) {
                println("Oops")
            }
        }

        fun GPSPROVIDER(){
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val locationManager = fusedLocationProviderClient.lastLocation.result.latitude

        }
    }
}