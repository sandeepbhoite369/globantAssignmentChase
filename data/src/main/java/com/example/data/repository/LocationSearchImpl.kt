package com.example.data.repository

import com.example.data.source.remote.LocationRemoteSource
import com.example.domain.model.CurrentWeatherData
import com.example.domain.repository.LocationSearchRepository

class LocationSearchImpl(private val remoteSource: LocationRemoteSource) :
    LocationSearchRepository {
    override suspend fun getLocations(
        lat: Double,
        long: Double,
        apiKey: String
    ): CurrentWeatherData {
        return remoteSource.getLocation(lat, long, apiKey)
    }
}