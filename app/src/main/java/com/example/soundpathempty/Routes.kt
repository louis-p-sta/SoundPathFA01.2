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
import androidx.room.Room
import com.example.soundpathempty.ui.theme.SoundPathEmptyTheme
class Routes : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.routes)
        title = "Saved items"

        val menubutton: Button = findViewById(R.id.retouraumenu)
        menubutton.setOnClickListener {
            val i = Intent(this@Routes, MainActivity::class.java)
            startActivity(i)
        }
    }
}
