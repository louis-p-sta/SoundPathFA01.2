package com.example.soundpathempty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class Marker : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)
        title = "Marker"
        val menubutton: Button = findViewById(R.id.menu_marker)
        menubutton.setOnClickListener {
            val i = Intent(this@Marker, MainActivity::class.java)
            startActivity(i)
        }
    }
}