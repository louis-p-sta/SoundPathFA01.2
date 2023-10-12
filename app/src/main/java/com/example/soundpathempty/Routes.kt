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
import android.widget.Button
import android.content.Intent
class Routes : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.routes)
        title = "Routes"
        val menubutton: Button = findViewById(R.id.menu)
        menubutton.setOnClickListener {
            val i = Intent(this@Routes, MainActivity::class.java)
            startActivity(i)
        }
    }
}
