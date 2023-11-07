package com.example.soundpathempty


import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.content.Intent
import android.location.Location
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.soundpathempty.databinding.LayoutBinding
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
private const val PRIORITY_HIGH_ACCURACY = 100
//Test de commit
class MainActivity : ComponentActivity() {
    private lateinit var layout: View
    private lateinit var binding: LayoutBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        //requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        //requestPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        super.onCreate(savedInstanceState)
        binding = LayoutBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
        //var button = findViewById<Button>(R.id.button)
        /*
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            requestPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        finish(); */
        //startActivity(getIntent())

        //val test: TextView = findViewById(R.id.PermissionCheck)
        /*
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            test.text = "DENIED"
        } else
            test.text = "SUCCESS"
        val routesbutton: Button = findViewById(R.id.routes)
        routesbutton.setOnClickListener {
            val intent = Intent(this@MainActivity, Routes::class.java)
            startActivity(intent)
        }
        */
//        println(R.id.location)
//        println(findViewById(R.id.location))
        val locationbutton: Button = findViewById(R.id.location)
        locationbutton.setOnClickListener{
            val locationpage = Intent(this@MainActivity, WhereAmI::class.java)
            startActivity(locationpage)
        }
        val markerbutton: Button = findViewById(R.id.marker)
        markerbutton.setOnClickListener {
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
                        val markerpage = Intent(this@MainActivity, Marker::class.java)
                        intent.putExtra("latitude", wayLatitude);
                        intent.putExtra("longitude",wayLongitude);
                        startActivity(markerpage)
                    }
                }
        }
        val routesbutton: Button = findViewById(R.id.routes)
        routesbutton.setOnClickListener {
            val routespage = Intent(this@MainActivity, Routes::class.java)
            startActivity(routespage)
        }
    }


    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        //
        }
//    val googletest = isGooglePlayServicesAvailable(this)
//    if g
}
