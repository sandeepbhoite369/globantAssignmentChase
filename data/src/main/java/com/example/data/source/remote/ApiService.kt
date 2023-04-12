package com.example.data.source.remote

import com.example.data.entity.OpenLocationEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") appId: String
    ): OpenLocationEntity

    @GET("data/2.5/weather")
    suspend fun getLocations(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String
    ):  OpenLocationEntity

}