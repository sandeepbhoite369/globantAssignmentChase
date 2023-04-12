package com.example.domain.model

data class CurrentWeatherData (
    val weather :List<WeatherModel>,
    val main : MainModel?,
    val sys : SysModel?,
    val wind : WindModel?,
    val name : String?
)

data class WeatherModel(
    val id : Int?,
    val main : String?,
    val description : String?,
    val icon : String?
)

data class MainModel(
 val temp : Double?,
 val feels_like : Double?,
 val temp_min : Double?,
 val temp_max : Double?,
 val pressure : Int?,
 val humidity : Int?,
 val sea_level : Int?,
 val grnd_level : Int?
)

data class SysModel(
    val type : Int?,
    val id : Int?,
    val country : String?,
    val sunrise : Int?,
    val sunset : Int?,
)

data class WindModel(
    val speed : Double?,
    val deg : Int?,
    val gust : Double?
)