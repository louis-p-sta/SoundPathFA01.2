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





//Test de commit
class MainActivity : ComponentActivity(){
    private lateinit var layout: View
    private lateinit var binding: LayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
        val test:TextView = findViewById(R.id.PermissionCheck)
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_DENIED){
            test.text = "DENIED"
        }
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
fun View.showSnackbar(
    view: View,
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}

private val requestPermissionLauncher =
    registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted: Boolean->
        if (isGranted) {
            Log.i("Permission:", "Granted")
        } else {
            Log.i("Permission: ", "Denied")
        }
    }

fun onClickRequestPermission(view: View) {
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            layout.showSnackbar(
                view,
                getString(R.string.permission_granted),
                Snackbar.LENGTH_INDEFINITE,
                null
            ) {}
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.CAMERA
        ) -> {
            layout.showSnackbar(
                view,
                getString(R.string.permission_required),
                Snackbar.LENGTH_INDEFINITE,
                getString(R.string.ok)
            ) {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }

        else -> {
            requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }
}