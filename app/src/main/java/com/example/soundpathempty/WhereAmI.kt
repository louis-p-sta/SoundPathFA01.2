package com.example.soundpathempty
import kotlin.math.*
import android.Manifest
import android.content.Context
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.ColumnInfo
import androidx.room.Query
import com.example.soundpathempty.MarkerDatabase.Companion.getDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.String
import kotlin.Exception
private const val PRIORITY_HIGH_ACCURACY = 100
class WhereAmI : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
 fun getAllMarkers():List<Marker_Data> = runBlocking {
        withContext(Dispatchers.IO) {
            val context = applicationContext
            val db = getDatabase(context = context)
            val ids = db.dao.getMarkers()
            return@withContext ids
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.positiongps)
        data class markers(
            @ColumnInfo(name="Latitude") val latitude: String?,
            @ColumnInfo(name="Longtitude") val longitude: String?
        )
        title = "@string/Saved_items"
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //Need a location request, since location is null
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            println("Fine statement entered")
        }
        val menubutton: Button = findViewById(R.id.menuButton)
        menubutton.setOnClickListener {
            val i = Intent(this@WhereAmI, MainActivity::class.java)
            startActivity(i)
        }
        val gpslocation: TextView = findViewById(R.id.textView)
        val getlocation: Button = findViewById(R.id.getLocation)
        //var wayLongitude = 0.0
        //var wayLatitude = 0.0
        getlocation.setOnClickListener {

                println("Fetching location...")
                //gpslocation.text =
                   // "Current location is \n" + "Lat : ${fusedLocationProviderClient.lastLocation.result.latitude}"
                //println("${fusedLocationProviderClient.getCurrentLocation(1,)}")
                //println("${fusedLocationProviderClient.lastLocation.result.latitude}")
                //var wayLatitude : Double = 10.0
                //var wayLongitude: Double = 10.0
                fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
                    override fun isCancellationRequested() = false
                })
                    .addOnSuccessListener { location: Location? ->
                        if (location == null) {
                            Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                            println("Failed to get location.")
                        }else {
                            println("We went here.")
                            println("Woot? ${location.latitude}")
                            var wayLatitude = location.latitude
                            var wayLongitude = location.longitude
                            println("Woot2? ${location.longitude}")
                            println("$wayLatitude")
                            if(wayLatitude is Double){
                                println("These are doubles")
                            }
                            println("Trying to get markers")
                            val markerList = getAllMarkers()
                            val lat = wayLatitude
                            val lon = wayLongitude //Test with a different position
                            val lat1 = 33.850018
                            val lon1 = -84.37207
                            val lon2 = markerList[0].longitude
                            val lat2 = markerList[0].latitude
                            val loc = markerList[0].location
                            val lat1rad = Math.toRadians(lat1)
                            val lon1rad = Math.toRadians(lon1)
                            val lat2rad = Math.toRadians(lat2)
                            val lon2rad = Math.toRadians(lon2)
                            val name = markerList[0].name
                            var result: FloatArray = FloatArray(3)
                            val distance =acos(sin(lat1rad)*sin(lat2rad)+cos(lat1rad)*cos(lat2rad)*cos(lon2rad-lon1rad))*6371
                            Location.distanceBetween(lat1,lon1,lat2,lon2,result)
                            println("${result[0]} ${result[1]} ${result[2]}")
                            val dist3 = location.distanceTo(loc)
                            gpslocation.text = "Current location is \n" + "Lat : ${wayLatitude}\n" + "Long : ${wayLongitude} \n Distance obtained to marker ${name}: ${distance} \n Dist2 : ${result[0]} \n Dist3 :${dist3}"

                        }

                    }




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


        }

    }
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            //
        }
}