package com.example.soundpathempty


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.runBlocking
import java.util.Locale


//import com.example.soundpathempty.Route_stuff


private const val PRIORITY_HIGH_ACCURACY = 100
private const val threshold = 15 //Threshold in meters for marker detection
private const val TIMEOUT:Long = 2000 //Interval at which application polls user location.

//Test de commit
class MainActivity : ComponentActivity(), Runnable { //TODO: Not sure if allowed to declare abstract class here.
    private lateinit var layout: View
    private lateinit var binding: LayoutBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var m_Text = "Enter route name and description"
    private var show_marker_dialog = false
    private var root: View? = null
    //private val threshold = 15
    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.US
                }
            })
    }
    //val handler = Handler(Looper.getMainLooper())
    private var btnSpeak: Button? = null
    private var etSpeak: EditText? = null
    private val db by lazy {
        Room.databaseBuilder(applicationContext, MarkerDatabase::class.java, "Markers.db").allowMainThreadQueries().build()//TODO: Potentially harmful.
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

//    fun setup() {
//        if (textToSpeechEngine == null) {
//            textToSpeechEngine = TextToSpeech(
//                MyAppUtils.getApplicationContext()
//            ) { status -> if (status == TextToSpeech.SUCCESS) speaker.setLanguage(Locale.getDefault()) }
//        }
//    }
    companion object {
        var record_state = false
        val noRouteName = "No Route"
        var current_route = noRouteName
        var last_alert_state = "success"
        var initial_marker = false
        var running_route = ""
        var current_marker_index = 0
        var current_marker_saving = 0
        var forwards = true
        var backwards = false
        var finished = false
        var done = false
        var routeStarted = false
        var reminder = false
        var markerTrack:Marker_Data = Marker_Data(name = "", description = "", latitude = 0.0, longitude = 0.0, routeName = noRouteName)
        var current_direction = 0.0f
        var point1:Marker_Data = Marker_Data(name = "", description = "", latitude = 0.0, longitude = 0.0, routeName = noRouteName)
        var point2: Marker_Data = Marker_Data(name = "", description = "", latitude = 0.0, longitude = 0.0, routeName = noRouteName)
        var resultPoints = FloatArray(3)
        var nearby_marker:Array<String> = arrayOf("")
        var backwardsinitindex = false
        var forwardsinitindex = false
        //Status flags
        var distanceReminders = true
        var autoMarkerDetect = true
        //var reminder_twentyfive = false
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
                        SoundPathEmptyTheme {
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

                    }
                    initial_marker = false
                }
        }
        val locationButton: Button = findViewById(R.id.location)
        locationButton.setOnClickListener {
            nearestMarker()
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
                            SoundPathEmptyTheme {
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
                                        routeName = noRouteName
                                    )
                                }
                            }
                            }
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
            //val routes = Intent(this@MainActivity, WhereAmI::class.java)
            //startActivity(routes) TODO:Settings/customisation
        }
        val recordButton: Button = findViewById(R.id.start)
        if (record_state == true) {
            recordButton.text = "STOP RECORD"
            recordButton.setTextSize(65.0f)
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
                    if (record_state == false && running_route == "") {
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
                    } else if (record_state == true && running_route == "") {
                        //record_state = false
                        show_marker_dialog = true
                        setContent {
                            SoundPathEmptyTheme {
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
                    } else if(running_route != ""){
                        running_route = ""
                        current_marker_index = 0
                        Toast.makeText(this@MainActivity,
                            "Route terminated",
                            Toast.LENGTH_LONG).show()
                        recordButton.text = "RECORD"
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
//        tts!!.speak(text, TextToSpeech.QUEUE_ADD, null,"")
    } //End of onCreate
    public fun onInit(textToSpeechEngine:TextToSpeech){
        val textToSpeechEngine: TextToSpeech by lazy {
            TextToSpeech(this,
                TextToSpeech.OnInitListener { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeechEngine.language = Locale.US
                    }
                })
        }
    }
    public override fun onResume() {
        super.onResume()
        run()
    }

    public override fun onPause() {
        root!!.removeCallbacks(this)
        textToSpeechEngine.stop()
        super.onPause()
    }
    override fun run() {
//        val textToSpeechEngine: TextToSpeech by lazy {
//            TextToSpeech(this,
//                TextToSpeech.OnInitListener { status ->
//                    if (status == TextToSpeech.SUCCESS) {
//                        textToSpeechEngine.language = Locale.US
//                    }
//                })
//        }
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
        //var current_latitude: Double = 0.0
        //var current_longitude:Double = 0.0 //TODO: Clear this up (latitude longitude init)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)//TODO: Check that this improves location accuracy
        fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token
                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                runBlocking {
                    var current_latitude:Double = 0.0
                    var current_longitude:Double = 0.0
                    var accuracy: Float = 0.0f
                    var intAccuracy:Int = 0
                    if (location != null) {
                        current_latitude = location.latitude
                        current_longitude = location.longitude
                        accuracy = location.accuracy
                        intAccuracy = accuracy.toInt()
                    } else{
                        Toast.makeText(this@MainActivity,"Null location!", Toast.LENGTH_LONG).show()
                    }
                    //Marker check track
                    if(markerTrack.name !=""){
                        var result = FloatArray(3)
                        Location.distanceBetween(current_latitude,current_longitude,markerTrack.latitude,markerTrack.longitude,result)
                        val text = "Distance to ${markerTrack.name} is ${result[0].toInt()} meters ${convertClockPosition(current_direction,result[1])}."
                        Toast.makeText(this@MainActivity,text,Toast.LENGTH_LONG).show()
                        textToSpeechEngine.speak(text,TextToSpeech.QUEUE_ADD, null)//TODO: Make sure queue ADD is the correct thing
                        textToSpeechEngine.speak(text,TextToSpeech.QUEUE_ADD, null)
                        markerTrack.name = "" //TODO: Double check queue flush
                    }
                    //Update tracking vector
                    if(point1.latitude == 0.0){
                        point1.latitude = current_latitude
                        point1.longitude = current_longitude
                    } else if (point2.latitude == 0.0){
                        point2.latitude = current_latitude
                        point2.longitude = current_longitude
                    }
                    if (resultPoints[0]<5){
                        //Ne pas mettre a jour le premier point si trop proche.
                        point2.latitude = current_latitude
                        point2.longitude= current_longitude
                    }else if(resultPoints[0]>=5){
                        point1 = point2
                        point2.latitude = current_latitude
                        point2.longitude= current_longitude
                    }
                    //Get current bearing
                    Location.distanceBetween(
                        point1.latitude,
                        point1.longitude,
                        point2.latitude,
                        point2.latitude,
                        resultPoints
                    )
                    if(resultPoints[0]>5) {
                        current_direction = resultPoints[1]
                    }
                    //Check for nearby markers
                    var (nearestMarkerDistance,nearestMarker,nearestMarkerBearing) = nearestMarkerNoLocation(current_latitude,current_longitude)
                    if(nearestMarkerDistance<15 && nearestMarker.routeName!=running_route && !(nearestMarker.name in nearby_marker) && autoMarkerDetect){
                        val text = "Nearby marker ${nearestMarker.name} is ${nearestMarkerDistance.toInt()} meters ${convertClockPosition(current_direction,nearestMarkerBearing)}"
                        Toast.makeText(this@MainActivity,text,Toast.LENGTH_SHORT)
                        textToSpeechEngine.speak(text,TextToSpeech.QUEUE_ADD,null)
                        //Flag
                        nearby_marker += nearestMarker.name //Added this for nearby markers...
                    }
                    if (running_route != "") {
                        if (finished) {
                            done = true
                        }
                        val recordButton: Button = findViewById(R.id.start)
                        recordButton.text = "STOP"
                        val data = db.dao.getRouteWithMarkers(running_route)
                        //val routeName = data[0].route.routeName
                        val markers = data[0].markers
                        val result: FloatArray = FloatArray(3)
                        if(forwardsinitindex){
                            current_marker_index = 0
                            forwardsinitindex = false
                        }else if(backwardsinitindex){
                            current_marker_index = markers.size - 1
                            backwardsinitindex = false
                        }
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
                            val distance_int = result[0].toInt()
                            val bearing = result[1].toInt()
                            //val distance_ten:Int = ((Math.ceil(distance/10.0))*10).toInt()
                            if(routeStarted){
                                val text = "${running_route} started. Next marker ${markers[current_marker_index].name} is  ${distance_int} meters ${convertClockPosition(current_direction,result[1])}."
                                val ret = textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null)
                                println(ret)
                                //val ret = textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null)
                                Toast.makeText(this@MainActivity,text, Toast.LENGTH_SHORT).show()
                                routeStarted = false
                            }
                            if((distance_int % 100) < 5 && distanceReminders ){ //Add
                                reminder = true
                            }
                            if((distance_int % 50) < 5 && distance_int<100 && distanceReminders){
                                reminder = true
                            }
                            println("Distance between you and ${markers[current_marker_index].name} : ${distance_int}, ${bearing} , accuracy(%): ${accuracy} ")
                            val text = "Distance to ${markers[current_marker_index].name} is  ${distance_int} meters ${convertClockPosition(current_direction,result[1])}."
                            if (!done) {
                                val msg = Toast.makeText(
                                    this@MainActivity,
                                    text,
                                    Toast.LENGTH_SHORT
                                )
                                msg.show()
                                if(reminder) {
                                    textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null)
                                    reminder = false
                                    Toast.makeText(this@MainActivity,"Distance reminder in effect (${distance_int}) meters, ${markers[current_marker_index].name}.",Toast.LENGTH_SHORT)
                                }
                            }
                            if (distance < threshold && current_marker_index < markers.size - 1) { //Notify if within 5 metres
                                //val nextLocation = getLocation()
                                var resultnext = FloatArray(3)
                                Location.distanceBetween(
                                    current_latitude,
                                    current_longitude,
                                    markers[current_marker_index + 1].latitude,
                                    markers[current_marker_index + 1].longitude,
                                    resultnext
                                )
                                val text = "Arrived at marker ${markers[current_marker_index].name}. Marker comment : ${markers[current_marker_index].description}. Next marker ${markers[current_marker_index + 1].name} is ${resultnext[0].toInt()} meters ${convertClockPosition(current_direction,resultnext[1])}."
                                val msg = Toast.makeText(
                                    this@MainActivity,
                                    text,
                                    Toast.LENGTH_SHORT
                                )
                                msg.show()
                                textToSpeechEngine.speak(text,TextToSpeech.QUEUE_ADD, null)
                                current_marker_index = current_marker_index + 1
                            } else if(distance<threshold && current_marker_index == markers.size - 1){
                                finished = true
                            }
                            if (done == true) {
                                println("Arrived at final destination")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Arrived at ${markers[current_marker_index].name}. Route finished!",
                                    Toast.LENGTH_LONG
                                ).show()
                                textToSpeechEngine.speak("Arrived at ${markers[current_marker_index].name}. Route finished!",TextToSpeech.QUEUE_ADD, null)
                                //TODO:Fix going out of bounds exception.
                                //TODO: Screen persistence
                                running_route = ""
                                current_marker_index = 0
                                recordButton.text = "RECORD"
                                done = false
                                finished = false

                            }
                        } else if (backwards == true) {
                            Location.distanceBetween(
                                current_latitude,
                                current_latitude,
                                markers[current_marker_index].latitude,
                                markers[current_marker_index].longitude,
                                result
                            )
                            val distance = result[0].toInt()
                            val bearing = result[1].toInt() //Par rapport au nord
                            println("Distance between you and ${markers[current_marker_index].name} : ${distance} , ${bearing}")
                            val text = "Distance between you and ${markers[current_marker_index].name} : ${distance}, ${bearing}."
                            if (!done) {
                                val msg = Toast.makeText(
                                    this@MainActivity,
                                    text,
                                    Toast.LENGTH_SHORT
                                )
                                msg.show()
                                //textToSpeechEngine.speak(text,TextToSpeech.QUEUE_ADD, null)
                            }
                            if(distance < threshold && current_marker_index != 0) { //Notify if within 5 metres
                                val msg = Toast.makeText(
                                    this@MainActivity,
                                    "Arrived at marker ${markers[current_marker_index].name}. Marker comment : ${markers[current_marker_index]}. Next marker ${markers[current_marker_index - 1].name}, ${distance} meters away.",
                                    Toast.LENGTH_SHORT
                                )
                                msg.show()
                                current_marker_index = current_marker_index - 1
                            } else {
                                finished = true //TODO: This used to be current_marker decrement
                                //finished = true //TODO: This used to be current_marker decrement
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
        root!!.postDelayed(this, TIMEOUT)
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

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }
    fun nearestMarker(){
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
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)//TODO: Check that this improves location accuracy
        fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token
                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                runBlocking {
                    var myLatitude = 0.0
                    var myLongitude = 0.0
                    if (location != null) {
                        myLatitude = location.latitude
                        myLongitude = location.longitude
                    }
                    val markers = db.dao.getMarkers()
                    var result = FloatArray(3)
                    var closestMarker = "None"
                    var closestDistance = 1.0e10f
                    var closestBearing = 0.0f
                    for (marker in markers) {
                        Location.distanceBetween(
                            myLatitude,
                            myLongitude,
                            marker.latitude,
                            marker.longitude,
                            result
                        )
                        val distance = result[0]
                        val bearing = result[1]
                        if (distance < closestDistance) {
                            closestDistance = distance
                            closestMarker = marker.name
                            closestBearing = bearing
                        }
                    }
                    if (closestMarker == "None") {
                        val text = "No nearby markers."
                        textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null)
                        Toast.makeText(
                            this@MainActivity,
                            text,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val text =
                            "Nearest marker is ${closestMarker}, ${closestDistance.toInt()} meters ${convertClockPosition(
                                current_direction,closestBearing)}"
                        textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null)
                        Toast.makeText(
                            this@MainActivity,
                            text,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
    fun nearestMarkerNotInRoute():Triple<Float,String,Float>{
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
        }
        var closestMarker = "None"
        var closestDistance = 1.0e10f
        var closestBearing = 0.0f
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)//TODO: Check that this improves location accuracy
        fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token
                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                runBlocking {
                    var myLatitude = 0.0
                    var myLongitude = 0.0
                    if (location != null) {
                        myLatitude = location.latitude
                        myLongitude = location.longitude
                    }
                    val markers = db.dao.getMarkers()
                    var result = FloatArray(3)
                    for (marker in markers) {
                        Location.distanceBetween(
                            myLatitude,
                            myLongitude,
                            marker.latitude,
                            marker.longitude,
                            result
                        )
                        val distance = result[0]
                        val bearing = result[1]
                        if (distance < closestDistance && marker.routeName != running_route) {
                            closestDistance = distance
                            closestMarker = marker.name
                            closestBearing = bearing
                        }
                    }
                }
                //val (distance,marker) = Pair(closestDistance,closestMarker)
            }
        return Triple(closestDistance, closestMarker, closestBearing)
    }
    fun nearestMarkerNoLocation(latitude:Double,longitude:Double):Triple<Float,Marker_Data,Float>{
        var closestMarker = Marker_Data(name = "", description = "", latitude = 0.0, longitude = 0.0, routeName = "")
        var closestDistance = 1.0e10f
        var closestBearing = 0.0f
        var myLatitude = latitude
        var myLongitude = longitude
        runBlocking {
            val markers = db.dao.getMarkers()
            var result = FloatArray(3)
            for (marker in markers) {
                Location.distanceBetween(
                    myLatitude,
                    myLongitude,
                    marker.latitude,
                    marker.longitude,
                    result
                )
                val distance = result[0]
                val bearing = result[1]
                if (distance < closestDistance && marker.routeName != running_route) {
                    closestDistance = distance
                    closestMarker = marker
                    closestBearing = bearing
                }
            }
        }
                //val (distance,marker) = Pair(closestDistance,closestMarker)
        return Triple(closestDistance, closestMarker, closestBearing)
    }
    fun getLocation():Location?{
        var myLoc:Location? = null
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
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)//TODO: Check that this improves location accuracy
        fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token
                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                myLoc = location
            }
        return myLoc
    }
    fun convertClockPosition(user_bearing:Float,marker_bearing:Float):String{
        val real_bearing = marker_bearing - user_bearing
        var clock_position = ""
        if(real_bearing > -45 && real_bearing <= 45){
            clock_position = "forwards"
        }else if (real_bearing > -45 && real_bearing <= 135){
            clock_position = "to the right"
        } else if((real_bearing > 135 && real_bearing < 180 )||(real_bearing<-135 && real_bearing>-180)){
            clock_position = "backwards"
        } else if(real_bearing<-45 && real_bearing>-135){
            clock_position = "to the left"
        }
        return clock_position
    }
//    val googletest = isGooglePlayServicesAvailable(this)
//    if g
    fun textDisplay(){
        //1 message per loop?
    }
}