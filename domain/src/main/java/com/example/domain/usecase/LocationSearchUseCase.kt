package com.example.domain.usecase

import com.example.domain.model.CurrentWeatherData
import com.example.domain.repository.LocationSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LocationSearchUseCase constructor(private val locationSearchRepository: LocationSearchRepository){

    suspend operator fun invoke(lat:Double, long: Double,apiKey:String) : Flow<CurrentWeatherData>{
        return flowOf(locationSearchRepository.getLocations(lat,long,apiKey))
    }
}