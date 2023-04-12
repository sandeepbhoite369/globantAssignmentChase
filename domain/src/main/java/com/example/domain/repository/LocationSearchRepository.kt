package com.example.domain.repository

import com.example.domain.model.CurrentWeatherData

interface LocationSearchRepository {
    suspend fun getLocations(lat: Double, long: Double, apiKey: String): CurrentWeatherData
}