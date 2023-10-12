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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


//Test de commit
class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)
        title = "SplashPage"
        val routesbutton:Button = findViewById(R.id.routes)
        routesbutton.setOnClickListener {
            val intent = Intent(this@MainActivity, Routes::class.java)
            startActivity(intent)
        }
        val locationbutton:Button = findViewById(R.id.location)
        locationbutton.setOnClickListener {
            val locationpage = Intent(this@MainActivity, WhereAmI::class.java)
            startActivity(locationpage)
        }
    }
}