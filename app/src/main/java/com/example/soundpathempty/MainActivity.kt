 //Ceci est le prototype 1
package com.example.soundpathempty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme
import android.location.LocationListener
import android.location.Location
import android.location.LocationManager
import android.widget.Button
import android.widget.TextView
import android.Manifest
import android.content.pm.PackageManager
import android.content.Context
import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.util.Log
import android.view.View
import android.widget.ActionMenuView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.soundpathempty.databinding.LayoutBinding
import com.google.android.material.snackbar.Snackbar
import android.app.Activity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.registerForActivityResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable
import com.google.android.gms.location.FusedLocationProviderClient

//Test de commit
class MainActivity : ComponentActivity() {
    private lateinit var layout: View
    private lateinit var binding: LayoutBinding
    private lateinit var FusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
        var button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
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
            finish();
            startActivity(getIntent())
        }

        val test: TextView = findViewById(R.id.PermissionCheck)
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            test.text = "DENIED"
        } else
            test.text = "SUCCESS"
        val routesbutton: Button = findViewById(R.id.routes)
        routesbutton.setOnClickListener {
            val intent = Intent(this@MainActivity, Routes::class.java)
            startActivity(intent)
        }
        val locationbutton: Button = findViewById(R.id.location)
        locationbutton.setOnClickListener{
            val locationpage = Intent(this@MainActivity, WhereAmI::class.java)
            startActivity(locationpage)
        }

        val markerbutton: Button = findViewById(R.id.search)
        markerbutton.setOnClickListener {
            val markerpage = Intent(this@MainActivity, Marker::class.java)
            startActivity(markerpage)
        }
    }



    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            //
        }
//    val googletest = isGooglePlayServicesAvailable(this)
//    if g
}
