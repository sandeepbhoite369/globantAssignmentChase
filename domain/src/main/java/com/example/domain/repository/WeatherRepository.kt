package com.example.domain.repository

import com.example.domain.model.CurrentWeatherData

interface WeatherRepository {
    suspend fun getWeather(cityName: String, apiKey: String): CurrentWeatherData
}