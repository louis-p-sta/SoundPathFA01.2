package com.example.soundpathempty

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.soundpathempty.Entities.Route_Data
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme
import kotlinx.coroutines.launch

class Routes : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.routes)
//        //title = "@string/Saved_items"
//        val dao = MarkerDatabase.getDatabase(context = applicationContext).dao
//        println(dao)
//        println("Tried making route?")
//        val markers = listOf(
//            Marker_Data("Marqueur initial", "Premier marqueur", 0.0,0.0, "RouteTest"),
//            Marker_Data("Marqueur final","Dernier marqueur", 100.0, 100.0, "RouteTest")
//        )
//        val route = Route_Data("RouteTest","Route pour tester")
//        lifecycleScope.launch {
//            dao.upsertRoute(route)
//            markers.forEach{dao.upsertMarker(it)}
//            println("Did launch thing")
//        }
        val menubutton: Button = findViewById(R.id.retouraumenu)
        menubutton.setOnClickListener {
            val i = Intent(this@Routes, MainActivity::class.java)
            startActivity(i)
        }
    }
}
