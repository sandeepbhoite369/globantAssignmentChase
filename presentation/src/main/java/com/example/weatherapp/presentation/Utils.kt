package com.example.weatherapp.presentation

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.weatherapp.R
import com.example.weatherapp.presentation.MainActivity.Companion.MY_PERMISSIONS_REQUEST_LOCATION
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.*

fun checkLocationPermission(context: Activity) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.text_location_permission_needed))
                .setMessage(context.getString(R.string.text_location_permission_needed_desc))
                .setPositiveButton(
                    context.getString(R.string.text_ok)
                ) { _, _ ->
                    //Prompt the user once explanation has been shown
                    requestLocationPermission(context)
                }
                .create()
                .show()
        } else {
            // No explanation needed, we can request the permission.
            requestLocationPermission(context)
        }
    } else {
        checkBackgroundLocation(context)
    }
}

fun requestLocationPermission(context: Activity) {
    ActivityCompat.requestPermissions(
        context,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
        MY_PERMISSIONS_REQUEST_LOCATION
    )
}

fun checkBackgroundLocation(context: Activity) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestBackgroundLocationPermission(context)
    }
}

fun requestBackgroundLocationPermission(context: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            MainActivity.MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
        )
    } else {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }
}