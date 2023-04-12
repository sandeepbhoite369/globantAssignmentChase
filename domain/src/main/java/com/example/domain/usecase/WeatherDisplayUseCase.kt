package com.example.domain.usecase

import com.example.domain.model.CurrentWeatherData
import com.example.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherDisplayUseCase constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(cityName: String, apiKey: String): Flow<CurrentWeatherData> {
        return flowOf(weatherRepository.getWeather(cityName, apiKey))
    }
}