package com.example.data.repository

import com.example.data.source.remote.WeatherRemoteSource
import com.example.domain.model.CurrentWeatherData
import com.example.domain.repository.WeatherRepository

class WeatherImpl(private val weatherRemoteSource: WeatherRemoteSource) : WeatherRepository {
    override suspend fun getWeather(cityName: String, apiKey: String): CurrentWeatherData {
        return weatherRemoteSource.getWeather(cityName, apiKey)
    }
}