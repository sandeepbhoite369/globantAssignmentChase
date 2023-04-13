package com.example.weatherapp.presentation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.domain.model.CurrentWeatherData
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.presentation.searchlocation.SearchLocationViewModel
import com.example.weatherapp.utils.Status
import com.google.android.gms.location.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: SearchLocationViewModel by viewModel()
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var isLocationDetected = false
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var lastCityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        sharedPref = getSharedPreferences(SHARED_PREFERANCE, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        checkLocationPermission(this)
    }

    override fun onResume() {
        super.onResume()
        isLocationDetected = false
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
        lastCityName = sharedPref.getString(LAST_CITY_NAME, null)
        lastCityName?.let {
            getCityWeather(it)
        }
        getCityName()
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                if (location != null && !isLocationDetected) {
                    fetchCurrentLocationWeather(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                    isLocationDetected = true
                }
            }
        }
    }

    private fun getCityName() {
        binding.etGetCityName.setOnEditorActionListener { v, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                editor.apply {
                    putString(LAST_CITY_NAME, binding.etGetCityName.text.toString())
                    apply()
                }
                getCityWeather(binding.etGetCityName.text.toString())
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                  //  binding.etGetCityName.clearFocus()
                }
                true
            } else false

        }
    }

    private fun getCityWeather(cityName: String) {
        binding.progressBar.visibility = View.VISIBLE
        subscribeToObserver()
        viewModel.getCityWeather(cityName, OPEN_WEATHER_API_KEY)
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {
        binding.progressBar.visibility = View.VISIBLE
        subscribeToObserver()
        viewModel.getCurrentWeatherData(
            latitude.toDouble(),
            longitude.toDouble(),
            OPEN_WEATHER_API_KEY
        )
    }

    private fun subscribeToObserver() {
        viewModel.locationListState.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    setpUI(it.data as CurrentWeatherData)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setpUI(data: CurrentWeatherData) {
        with(binding) {
            clMainLayout.visibility = View.VISIBLE
            val sdf = SimpleDateFormat(DATE_FORMAT)
            val currentDate = sdf.format(Date())
            tvDateAndTime.text = currentDate

            tvDayMaxTemp.text =
                "Day " + data.main?.temp_max?.let { kalvinToCelcius(it) } + "°"
            tvDayMinTemp.text =
                "Day " + data.main?.temp_min?.let { kalvinToCelcius(it) } + "°"
            tvTemp.text = "" + data.main?.temp?.let { kalvinToCelcius(it) } + "°"
            tvFeelsLike.text =
                "Feel like " + data.main?.feels_like?.let { kalvinToCelcius(it) } + "°"
            tvWeatherType.text = data.weather[0].main
            tvSunrise.text = data.sys?.sunrise?.let { timeStampTolocalDate(it.toLong()) }
            tvSunset.text = data.sys?.sunset?.let { timeStampTolocalDate(it.toLong()) }
            tvPressure.text = data.main?.pressure.toString()
            tvHumidity.text = data.main?.humidity.toString() + " %"
            tvWindSpeed.text = data.wind?.speed.toString() + " m/s"
            tvTempFarenhite.text =
                "" + data.main?.temp?.let { kalvinToCelcius(it).times(1.8).plus(32).roundToInt() }
            etGetCityName.setText(data.name)
        }
    }

    private fun timeStampTolocalDate(timeStamp: Long): String {
        val localTime = timeStamp.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        }
        return localTime.toString()
    }

    private fun kalvinToCelcius(temp: Double): Double {
        var intTemp = temp
        intTemp = intTemp.minus(NEW_TEMP)
        return intTemp.toBigDecimal().setScale(NEW_SCALE, RoundingMode.UP).toDouble()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                        // Now check background location
                        checkBackgroundLocation(this@MainActivity)
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        this,
                        "permission_denied",
                        Toast.LENGTH_LONG
                    ).show()
                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        Toast.makeText(
                            this, getString(R.string.text_granted_background_location_permission),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        this,
                        getString(R.string.text_permission_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
            .setMaxUpdateDelayMillis(LOCATION_MAX_WAIT_TIME)
            .build()

    companion object {
        private const val OPEN_WEATHER_API_KEY = "ed73305365a0d4d64cd534a24846c812"
        private const val DATE_FORMAT = "dd/MM/yyyy hh:mm"
        private const val NEW_SCALE = 1
        private const val NEW_TEMP = 273
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
        private const val LOCATION_INTERVAL = 30000L
        private const val LOCATION_FASTEST_INTERVAL = 50L
        private const val LOCATION_MAX_WAIT_TIME = 100L
        private const val SHARED_PREFERANCE = "myPreferance"
        private const val LAST_CITY_NAME = "lastCityName"
    }
}