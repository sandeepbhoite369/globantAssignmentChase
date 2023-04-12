package com.example.weatherapp.presentation


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.domain.model.CurrentWeatherData
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.presentation.searchlocation.SearchLocationViewModel
import com.example.weatherapp.utils.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private val viewModel: SearchLocationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.clMainLayout.visibility = View.GONE

        getCurrentLocation()
        getCityName()
    }

    /*This function is to get latitude and langitude for first time */
    private fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationEnable()) {

                if (ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "NULL  ", Toast.LENGTH_LONG).show()
                    } else {
                        // On successful lat and long fetch data for current location
                        fetchCurrentLocationWeather(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                    }
                }

            } else {
                //setting Open here
                Toast.makeText(this, "Turn On Location..", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // Request permission here
            requestPermission()
        }
    }

    private fun getCityName() {
        binding.etGetCityName.setOnEditorActionListener{ v, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getCityWeather(binding.etGetCityName.text.toString())
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    binding.etGetCityName.clearFocus()
                }
                true
            } else false

        }
    }

    private fun getCityWeather(cityName: String) {
        binding.progressBar.visibility = View.VISIBLE
        subscribeToObserver()
        viewModel.getCityWeather(cityName, BuildConfig.OPEN_WEATHER_API_KEY)
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {
        binding.progressBar.visibility = View.VISIBLE
        subscribeToObserver()
        viewModel.getCurrentWeatherData(
            latitude.toDouble(),
            longitude.toDouble(),
            BuildConfig.OPEN_WEATHER_API_KEY
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

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted..", Toast.LENGTH_LONG).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        private const val DATE_FORMAT = "dd/MM/yyyy hh:mm"
        private const val NEW_SCALE = 1
        private const val NEW_TEMP = 273
    }
}