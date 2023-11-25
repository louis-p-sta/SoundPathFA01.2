package com.example.soundpathempty


import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.soundpathempty.databinding.LayoutBinding
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme
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
    private var m_Text = "Enter route name and description"
    private var record_state = false
    private var show_marker_dialog = false
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
        (viewModel::onEvent)(MarkerEvent.HideDialog)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            println("Fine statement entered")
        }
        super.onCreate(savedInstanceState)
        binding = LayoutBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//            println("Fine 2 statement entered")
//        }
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
        val locationButton: Button = findViewById(R.id.location)
        locationButton.setOnClickListener{
            println("Location click")
            val locationPage = Intent(this@MainActivity, WhereAmI::class.java)
            startActivity(locationPage)
        }
        val markerButton: Button = findViewById(R.id.marker)
        markerButton.setOnClickListener {
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
                            // SoundPathEmptyTheme {
                            val state by viewModel.state.collectAsState()
                            (viewModel::onEvent)(MarkerEvent.ShowDialog)
                            if (state.isAddingMarker) {
                                AddMarkerDialog(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    lat = wayLatitude,
                                    lon = wayLongitude,
                                    routeName = "void"
                                )
                            }
                            // }
                        }
                    }
                }
        }
            val routesButton: Button = findViewById(R.id.saved)
            routesButton.setOnClickListener {
                println("We've been clicked")
                setContent{
                    val state by viewModel.state.collectAsState()
                    MarkerScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        lat = 1.3,
                        lon = 5.6
                    )
                }
                //val routesPage = Intent(this@MainActivity, Routes::class.java)
                //startActivity(routesPage)
            }
            val settingsButton:Button = findViewById(R.id.settings)
            settingsButton.setOnClickListener{
                println("We've been clicked - settings")
                val routes = Intent(this@MainActivity,WhereAmI::class.java)
                startActivity(routes)
            }
            val recordButton: Button = findViewById(R.id.start)
            recordButton.setOnClickListener {
                if (record_state == false) {
                    record_state = true
                    show_marker_dialog = true
                } else if (record_state == true) {
                    record_state = false
                    show_marker_dialog = true
                    //getLocation here
                    /*  setContent{
                SoundPathEmptyTheme {
                    val state by viewModel.state.collectAsState()
                    AddMarkerDialog(
                        state = state,
                        onEvent = ,
                        lat = ,
                        lon = ,
                        routeName = routeName)

                }
            }*/
                }

//            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//            builder.setTitle("Enter route name")
//
//// Set up the input
//
//// Set up the input
//            val input = EditText(this)
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//            input.inputType = InputType.TYPE_CLASS_TEXT
//            builder.setView(input)
//            builder.show()
//            println("input")
            }//End of start button
    } //End of onCreate

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        //
        }
//    val googletest = isGooglePlayServicesAvailable(this)
//    if g
}
