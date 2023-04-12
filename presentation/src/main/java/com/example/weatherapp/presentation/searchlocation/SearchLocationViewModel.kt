package com.example.weatherapp.presentation.searchlocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CurrentWeatherData
import com.example.domain.usecase.LocationSearchUseCase
import com.example.domain.usecase.WeatherDisplayUseCase
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchLocationViewModel constructor(
    private val locationSearchUseCase: LocationSearchUseCase,
    private val weatherDisplayUseCase: WeatherDisplayUseCase
) : ViewModel() {

    private val _locationListState = MutableLiveData<Resource<CurrentWeatherData>>()
    val locationListState: LiveData<Resource<CurrentWeatherData>> = _locationListState

    fun getCurrentWeatherData(latitude: Double, longitude: Double, apiKey: String) {
        viewModelScope.launch {
            locationSearchUseCase(latitude, longitude, apiKey).onStart {
                _locationListState.value = Resource.loading(null)
            }.catch {
                _locationListState.value = Resource.error(it.message ?: "", null)
            }.collect {
                _locationListState.value = Resource.success(data = it)
            }
        }
    }

    fun getCityWeather(cityName: String, apiKey: String) {
        viewModelScope.launch {
            weatherDisplayUseCase(cityName, apiKey).onStart {
                _locationListState.value = Resource.loading(null)
            }.catch {
                _locationListState.value = Resource.error(it.message ?: "", null)
            }.collect {
                _locationListState.value = Resource.success(data = it)
            }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}