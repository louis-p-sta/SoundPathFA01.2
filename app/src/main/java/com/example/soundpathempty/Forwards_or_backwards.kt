package com.example.soundpathempty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.soundpathempty.MainActivity.Companion.backwards
import com.example.soundpathempty.MainActivity.Companion.done
import com.example.soundpathempty.MainActivity.Companion.finished
import com.example.soundpathempty.MainActivity.Companion.forwards
import com.example.soundpathempty.MainActivity.Companion.routeStarted
import com.example.soundpathempty.MainActivity.Companion.running_route

class Forwards_or_backwards : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forwards_or_backwards)
        val ForwardsButton: Button = findViewById(R.id.ButtonForwards)
        ForwardsButton.setOnClickListener{
            forwards = true
            backwards = false
            done = false
            finished = false
            routeStarted = true
            val main = Intent(this@Forwards_or_backwards, MainActivity::class.java)
            startActivity(main)
        }
        val BackwardsButton: Button = findViewById(R.id.ButtonBackwards)
        BackwardsButton.setOnClickListener{
            backwards = true
            forwards = false
            done = false
            finished = false
            routeStarted = true
            val main = Intent(this@Forwards_or_backwards, MainActivity::class.java)
            startActivity(main)
        }
        val backButton: Button = findViewById(R.id.Back)
        backButton.setOnClickListener{
            running_route = ""
            val main = Intent(this@Forwards_or_backwards, MainActivity::class.java)
            startActivity(main)
        }
    }
}