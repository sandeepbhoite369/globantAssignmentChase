package com.example.data.source.remote

import com.example.data.mapper.LocationDataMapper
import com.example.domain.model.CurrentWeatherData

class WeatherRemoteSource(private val apiService: ApiService,
                          private val locationDataMapper: LocationDataMapper
) {
    suspend fun getWeather(cityName: String, apiKey: String): CurrentWeatherData =

        locationDataMapper.maplocation(apiService.getCurrentWeatherByCity(cityName, apiKey))
}