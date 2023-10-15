package com.example.soundpathempty

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.content.Intent
class Routes : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.routes)
        title = "Routes"
        val menubutton: Button = findViewById(R.id.retouraumenu)
        menubutton.setOnClickListener {
            val i = Intent(this@Routes, MainActivity::class.java)
            startActivity(i)
        }
    }
}
