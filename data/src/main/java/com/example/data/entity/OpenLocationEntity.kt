package com.example.data.entity

import com.google.gson.annotations.SerializedName

data class OpenLocationEntity(
    @SerializedName("coord")
    val coord: CoordEntity? = null,
    @SerializedName("weather")
    val weather: List<Weather>? = null,
    @SerializedName("base")
    val base: String? = "",
    @SerializedName("main")
    val main: MainEntity? = null,
    @SerializedName("visibility")
    val visibility: Int? = null,
    @SerializedName("wind")
    val wind: WindEntity? = null,
    @SerializedName("clouds")
    val clouds: cloudsEntity? = null,
    @SerializedName("dt")
    val dt: Int? = null,
    @SerializedName("sys")
    val sys: sysEntity? = null,

    @SerializedName("timezone")
    val timezone: Int? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("cod")
    val cod: Int? = null
)

data class CoordEntity(
    @SerializedName("lon")
    val lon: Double? = null,
    @SerializedName("lat")
    val lat: Double? = null
)

data class Weather(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("main")
    val main: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("icon")
    val icon: String? = null,
)

data class MainEntity(
    @SerializedName("temp")
    val temp: Double? = null,
    @SerializedName("feels_like")
    val feels_like: Double? = null,
    @SerializedName("temp_min")
    val temp_min: Double? = null,
    @SerializedName("temp_max")
    val temp_max: Double? = null,
    @SerializedName("pressure")
    val pressure: Int? = null,
    @SerializedName("humidity")
    val humidity: Int? = null,
    @SerializedName("sea_level")
    val sea_level: Int? = null,
    @SerializedName("grnd_level")
    val grnd_level: Int? = null
)

data class WindEntity(
    @SerializedName("speed")
    val speed: Double? = null,
    @SerializedName("deg")
    val deg: Int? = null,
    @SerializedName("gust")
    val gust: Double? = null
)

data class cloudsEntity(
    @SerializedName("all")
    val all: Int? = null,
)

data class sysEntity(
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("country")
    val country: String? = "",
    @SerializedName("sunrise")
    val sunrise: Int? = null,
    @SerializedName("sunset")
    val sunset: Int? = null
)