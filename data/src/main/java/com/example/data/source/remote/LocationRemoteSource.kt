package com.example.data.source.remote

import com.example.data.mapper.LocationDataMapper
import com.example.domain.model.CurrentWeatherData

class LocationRemoteSource(
    private val apiService: ApiService,
    private val locationDataMapper: LocationDataMapper
) {
    suspend fun getLocation(lat: Double, long: Double, apiKey: String): CurrentWeatherData =

        locationDataMapper.maplocation(apiService.getLocations(lat, long, apiKey))
}