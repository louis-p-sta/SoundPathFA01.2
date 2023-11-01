package com.example.soundpathempty

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.lang.String
import kotlin.Exception
private const val PRIORITY_HIGH_ACCURACY = 100
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
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //gpslocation.text =
                    // "Current location is \n" + "Lat : ${fusedLocationProviderClient.lastLocation.result.latitude}"
                    //println("${fusedLocationProviderClient.getCurrentLocation(1,)}")
                    //println("${fusedLocationProviderClient.lastLocation.result.latitude}")
                    //var wayLatitude : Double = 10.0
                    //var wayLongitude: Double = 10.0
                    fusedLocationProviderClient.getCurrentLocation(
                        PRIORITY_HIGH_ACCURACY,
                        object : CancellationToken() {
                            override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                                CancellationTokenSource().token

                            override fun isCancellationRequested() = false
                        })
                        .addOnSuccessListener { location: Location? ->
                            if (location == null) {
                                Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT)
                                    .show()
                                println("Failed to get location.")
                            } else {
                                println("We went here.")
                                println("Woot? ${location.latitude}")
                                var wayLatitude = location.latitude
                                var wayLongitude = location.longitude
                                println("Woot2? ${location.longitude}")
                                println("$wayLatitude")
                                gpslocation.text =
                                    "Current location is \n" + "Lat : ${wayLatitude}\n" + "Long : ${wayLongitude}"
                            }

                        }
                }
                else
                    gpslocation.text = "Location Permissions DENIED"

           /* }
                fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                    .addOnSuccessListener { location: Location? ->
                        if (location == null)
                            Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                        else {
                            val lat = location.latitude
                            val lon = location.longitude
                        }

                    }

            }*/


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