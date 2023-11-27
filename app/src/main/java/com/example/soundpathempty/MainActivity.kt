package com.example.soundpathempty


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.soundpathempty.Route_stuff.RoutesViewModel
import com.example.soundpathempty.databinding.LayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.runBlocking
import java.util.Locale


//import com.example.soundpathempty.Route_stuff


private const val PRIORITY_HIGH_ACCURACY = 100

//Test de commit
class MainActivity : ComponentActivity(), Runnable { //TODO: Not sure if allowed to declare abstract class here.
    private lateinit var layout: View
    private lateinit var binding: LayoutBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var m_Text = "Enter route name and description"
    private var show_marker_dialog = false
    private var root: View? = null

    //val handler = Handler(Looper.getMainLooper())
    private var btnSpeak: Button? = null
    private var etSpeak: EditText? = null
    private val db by lazy {
        Room.databaseBuilder(applicationContext, MarkerDatabase::class.java, "Markers.db").build()
    }
    private val markerViewModel by viewModels<MarkerViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MarkerViewModel(db.dao) as T //Might need a question mark after ViewModel
                }
            }
        }
    )
    private val routeViewModel by viewModels<RoutesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RoutesViewModel(db.dao) as T //Might need a question mark after ViewModel
                }
            }
        }
    )

    companion object {
        var record_state = false
        var current_route = "void"
        var last_alert_state = "success"
        var initial_marker = false
        var running_route = ""
        var current_marker_index = 0
        var current_marker_saving = 0
        var forwards = true
        var backwards = false
        var finished = false
        var done = false
    }
    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.US
                }
            })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        println("Running route: ${running_route}")
        (markerViewModel::onEvent)(MarkerEvent.HideDialog)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            println("Fine statement entered")
        }
        super.onCreate(savedInstanceState)
        root = findViewById(android.R.id.content)
        binding = LayoutBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)

        //Debut des fonctions custom

        if (initial_marker) { //If pour ajouter marqueur initial lors de création de route - select initial marker function?
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                .addOnSuccessListener { location: Location? ->
                    //record_state = true
                    show_marker_dialog = true
                    setContent {
                        val statemarker by markerViewModel.state.collectAsState()
                        (markerViewModel::onEvent)(MarkerEvent.ShowDialog)
                        if (statemarker.isAddingMarker) {
                            if (location != null) {
                                AddMarkerDialog(
                                    state = statemarker,
                                    onEvent = markerViewModel::onEvent,
                                    lat = location.latitude,
                                    lon = location.longitude,
                                    routeName = current_route,
                                    title = "Place initial marker",
                                    stateChange = true
                                )
                            }
                        }
                    }
                    initial_marker = false
                }
        }
        val locationButton: Button = findViewById(R.id.location)
        locationButton.setOnClickListener {
            println("Location click")
            val locationPage = Intent(this@MainActivity, WhereAmI::class.java)
            startActivity(locationPage)
        }
        val markerButton: Button = findViewById(R.id.marker)
        markerButton.setOnClickListener {
            var wayLatitude:Double = 0.0
            var wayLongitude:Double = 0.0
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this) //This used to be commented out - make sure to get the updated client before calling it to initiate second position.
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
                            val state by markerViewModel.state.collectAsState()
                            (markerViewModel::onEvent)(MarkerEvent.ShowDialog)
                            if (record_state) {
                                if (state.isAddingMarker) {
                                    AddMarkerDialog(
                                        state = state,
                                        onEvent = markerViewModel::onEvent,
                                        lat = wayLatitude,
                                        lon = wayLongitude,
                                        routeName = current_route
                                    )
                                }
                            } else {
                                if (state.isAddingMarker) {
                                    AddMarkerDialog(
                                        state = state,
                                        onEvent = markerViewModel::onEvent,
                                        lat = wayLatitude,
                                        lon = wayLongitude,
                                        routeName = "void"
                                    )
                                }
                            }
                            // }
                        }
                    }
                }
        }
        val routesButton: Button = findViewById(R.id.saved)
        routesButton.setOnClickListener {
            println("We've been clicked")
            val selectPage = Intent(this@MainActivity, RoutesOrMarkers::class.java)
            startActivity(selectPage)
        }

        val settingsButton: Button = findViewById(R.id.settings)
        settingsButton.setOnClickListener {
            println("We've been clicked - settings")
            val routes = Intent(this@MainActivity, WhereAmI::class.java)
            startActivity(routes)
        }
        val recordButton: Button = findViewById(R.id.start)
        if (record_state == true) {
            recordButton.text = "STOP RECORD"
        } else if (record_state == false) {
            recordButton.text = "RECORD"
        }
        recordButton.setOnClickListener {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }) //TODO:Remove unecessary location fetch.
                .addOnSuccessListener { location: Location? ->
                    if (record_state == false) {
                        //record_state = true
                        show_marker_dialog = true
                        setContent { //TODO: Check if could remove lifecyclescope.launch here
                            val state by routeViewModel.state.collectAsState()
                            (routeViewModel::onEvent)(RouteEvent.ShowRouteDialog)
                            if (state.isAddingRoute) {
                                AddRouteDialog(
                                    state = state,
                                    onEvent = routeViewModel::onEvent,
                                    initialize = true
                                )
                            }
                        }
                    } else if (record_state == true) {
                        //record_state = false
                        show_marker_dialog = true
                        setContent {
                            // SoundPathEmptyTheme {
                            val statemarker by markerViewModel.state.collectAsState()
                            (markerViewModel::onEvent)(MarkerEvent.ShowDialog)
                            if (statemarker.isAddingMarker) {
                                if (location != null) {
                                    AddMarkerDialog(
                                        state = statemarker,
                                        onEvent = markerViewModel::onEvent,
                                        lat = location.latitude,
                                        lon = location.longitude,
                                        routeName = current_route,
                                        title = "Place final marker",
                                        stateChange = true
                                    )
                                }
                            }
                        }
                    }
                }
        }//End of start button
        //Text to speech stuff
        //val text = "Isaac is really sexy."
        //var tts = TextToSpeech(this,this)
//        tts = TextToSpeech(this){status->
//            if(status == TextToSpeech.SUCCESS && tts!=null){
//                val result = tts.setLanguage(Locale.getDefault())
//                if(result == TextToSpeech.LANG_MISSING_DATA||result == TextToSpeech.LANG_NOT_SUPPORTED){
//                    Toast.makeText(this,"Text to speech language not supported", Toast.LENGTH_LONG,).show()
//                }
//            }
//        }
//        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    } //End of onCreate

    public override fun onResume() {
        super.onResume()
        run()
    }

    public override fun onPause() {
        root!!.removeCallbacks(this)
        super.onPause()
    }

    override fun run() {
        //Get position
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(
                this@MainActivity,
                "Locations permissions not granted!",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        var current_latitude: Double = 0.0
        var current_longitude:Double = 0.0 //TODO: Clear this up (latitude longitude init)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)//TODO: Check that this improves location accuracy
        fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    current_latitude = location.latitude
                    current_longitude = location.longitude
                }
                runBlocking {
                    if (running_route != "") {
                        if (finished) {
                            done = true
                        }
                        val recordButton: Button = findViewById(R.id.start)
                        recordButton.text = "STOP RECORD"
                        val data = db.dao.getRouteWithMarkers(running_route)
                        val routeName = data[0].route.routeName
                        val markers = data[0].markers
                        var result: FloatArray = FloatArray(3)
                        //val latitude_test = 0.0
                        //val longitude_test = 0.0
                        if (forwards == true) {
                            Location.distanceBetween(
                                current_latitude,
                                current_longitude,
                                markers[current_marker_index].latitude,
                                markers[current_marker_index].longitude,
                                result
                            )
                            val distance = result[0]
                            println("Distance between you and ${markers[current_marker_index].name} : ${distance}")
                            val text = "Distance between you and ${markers[current_marker_index].name} : ${distance}."
                            if (!done) {
                                val msg = Toast.makeText(
                                    this@MainActivity,
                                    text,
                                    Toast.LENGTH_SHORT
                                )
                                msg.show()
                                textToSpeechEngine.speak(text,TextToSpeech.QUEUE_FLUSH, null)
                            }
                            if (distance < 5.0 && current_marker_index < markers.size - 1) { //Notify if within 5 metres
                                val text = "Arrived at marker ${markers[current_marker_index].name}, next marker ${markers[current_marker_index + 1].name}."
                                val msg = Toast.makeText(
                                    this@MainActivity,
                                    text,
                                    Toast.LENGTH_SHORT
                                )
                                msg.show()
                                textToSpeechEngine.speak(text,TextToSpeech.QUEUE_FLUSH, null)
                                current_marker_index = current_marker_index + 1
                            } else {
                                finished = true
                            }
                            if (done) {
                                println("Arrived at final destination")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Route finished!",
                                    Toast.LENGTH_LONG
                                ).show()
                                textToSpeechEngine.speak("Route finished",TextToSpeech.QUEUE_FLUSH, null)
                                //TODO:Fix going out of bounds exception.
                                //TODO: Screen persistence
                                running_route = ""
                                current_marker_index = 0
                                recordButton.text = "RECORD"

                            }
                        } else if (backwards == true) {
                            Location.distanceBetween(
                                current_latitude,
                                current_latitude,
                                markers[current_marker_index].latitude,
                                markers[current_marker_index].longitude,
                                result
                            )
                            val distance = result[0]
                            println("Distance between you and ${markers[current_marker_index].name} : ${distance}")
                            if (distance < 5.0 && current_marker_index != 0) { //Notify if within 5 metres
                                val msg = Toast.makeText(
                                    this@MainActivity,
                                    "Arrived at marker ${markers[current_marker_index].name}, next marker ${markers[current_marker_index - 1].name}.",
                                    Toast.LENGTH_SHORT
                                )
                                msg.show()
                                current_marker_index = current_marker_index - 1
                            } else {
                                current_marker_index = current_marker_index - 1
                            }
                            if (current_marker_index == 0) {
                                println("Arrived at final destination")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Route finished!",
                                    Toast.LENGTH_SHORT
                                )
                                running_route = ""
                                recordButton.text = "RECORD"
                            }
                        }
                    }
                }
            }
        root!!.postDelayed(this, 5000)
    }

    //    fun run(){
//        runBlocking {
//            if (MainActivity.running_route != "") {
//                val data = db.dao.getRouteWithMarkers(MainActivity.running_route)
//                val routeName = data[0].route.routeName
//                val markers = data[0].markers
//                var result: FloatArray = FloatArray(3)
//                Location.distanceBetween(
//                    markers[0].latitude,
//                    markers[0].longitude,
//                    markers[1].latitude,
//                    markers[1].longitude,
//                    result
//                )
//                val distance = result[0]
//                println("Distance between ${markers[0].name} and ${markers[1].name} : ${distance}")
//            }
//        }
//        view.postDelayed(this,5000)
//    }
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            //
        }
//    val googletest = isGooglePlayServicesAvailable(this)
//    if g
}