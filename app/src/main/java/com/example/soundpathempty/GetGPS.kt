package com.example.soundpathempty

import android.Manifest
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.lang.String
import kotlin.Exception
private const val PRIORITY_HIGH_ACCURACY = 100

fun getGPS(fusedLocationProviderClient: FusedLocationProviderClient){

}