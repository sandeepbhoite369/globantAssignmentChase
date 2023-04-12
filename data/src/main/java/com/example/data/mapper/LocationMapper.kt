package com.example.data.mapper

import com.example.data.entity.OpenLocationEntity
import com.example.data.entity.Weather
import com.example.domain.model.CurrentWeatherData
import com.example.domain.model.MainModel
import com.example.domain.model.SysModel
import com.example.domain.model.WindModel
import com.example.domain.model.WeatherModel

class LocationDataMapper() {
    fun maplocation(locationEntity: OpenLocationEntity): CurrentWeatherData =

        CurrentWeatherData(
            weather = mapWeather(locationEntity.weather ?: emptyList()),
            main = MainModel(
                temp = locationEntity.main?.temp,
                feels_like = locationEntity.main?.feels_like,
                temp_min = locationEntity.main?.temp_min,
                temp_max = locationEntity.main?.temp_max,
                pressure = locationEntity.main?.pressure,
                humidity = locationEntity.main?.humidity,
                sea_level = locationEntity.main?.sea_level,
                grnd_level = locationEntity.main?.grnd_level
            ),
            sys = SysModel(
                type = locationEntity.sys?.type,
                id = locationEntity.sys?.id,
                country = locationEntity.sys?.country,
                sunrise = locationEntity.sys?.sunrise,
                sunset = locationEntity.sys?.sunset
            ),
            wind = WindModel(
                speed = locationEntity.wind?.speed,
                deg = locationEntity.wind?.deg,
                gust = locationEntity.wind?.gust
            ),
            name = locationEntity.name
        )
}

fun mapWeather(list: List<Weather>): List<WeatherModel> {
    val weatherModalList: ArrayList<WeatherModel> = ArrayList()
    for (weather in list) {
        weatherModalList.add(
            WeatherModel(
                id = weather.id,
                main = weather.main,
                description = weather.description,
                icon = weather.icon
            )
        )
    }
    return weatherModalList
}


