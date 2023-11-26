//package com.ayush.myapplication
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.speech.tts.TextToSpeech
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import java.util.*
//
//// Extending MainActivity TextToSpeech.OnInitListener class
//class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
//    private var tts: TextToSpeech? = null
//    private var btnSpeak: Button? = null
//    private var etSpeak: EditText? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // view binding button and edit text
//        btnSpeak = findViewById(R.id.btn_speak)
//        etSpeak = findViewById(R.id.et_input)
//
//        btnSpeak!!.isEnabled = false
//
//        // TextToSpeech(Context: this, OnInitListener: this)
//        tts = TextToSpeech(this, this)
//
//        btnSpeak!!.setOnClickListener { speakOut() }
//
//    }
//
//    override fun onInit(status: Int) {
//        if (status == TextToSpeech.SUCCESS) {
//            val result = tts!!.setLanguage(Locale.US)
//
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS","The Language not supported!")
//            } else {
//                btnSpeak!!.isEnabled = true
//            }
//        }
//    }
//    private fun speakOut() {
//        val text = etSpeak!!.text.toString()
//        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
//    }
//
//    public override fun onDestroy() {
//        // Shutdown TTS when
//        // activity is destroyed
//        if (tts != null) {
//            tts!!.stop()
//            tts!!.shutdown()
//        }
//        super.onDestroy()
//    }
//
//}