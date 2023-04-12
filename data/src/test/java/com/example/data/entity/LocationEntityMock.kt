package com.example.data.entity

import com.example.domain.model.*

val locationresultListMock = OpenLocationEntity(
    coord = CoordEntity(10.99, 44.34),
    weather = listOf<Weather>(Weather(803, "Clouds", "broken clouds", "04n")),
    base = "stations",
    main = MainEntity(279.64, 278.54, 279.23, 280.49, 1023, 64, 1023, 934),
    visibility = 10000,
    wind = WindEntity(1.72, 212, 1.58),
    clouds = cloudsEntity(66),
    dt = 1681153590,
    sys = sysEntity(2, 2004688, "IT", 1681101699, 1681101699),
    timezone = 7200,
    id = 3163858,
    name = "Zocca",
    cod = 200
)

val CurrentWeatherDataMock = CurrentWeatherData(
    weather = listOf<WeatherModel>(WeatherModel(803, "Clouds", "broken clouds", "04n")),
    main = MainModel(279.64, 278.54, 279.23, 280.49, 1023, 64, 1023, 934),
    sys =  SysModel(2, 2004688, "IT", 1681101699, 1681101699),
    wind = WindModel(1.72, 212, 1.58),
    name = "Zocca"
)
