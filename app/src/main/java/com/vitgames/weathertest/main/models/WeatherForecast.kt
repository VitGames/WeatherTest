package com.vitgames.weathertest.main.models

import com.google.gson.annotations.SerializedName

class WeatherForecast {
    @SerializedName("list")
    private var items: List<TodayWeather>? = null



    fun WeatherForecast(items: List<TodayWeather>?) {
        this.items = items
    }

    fun getItems(): List<TodayWeather>? {
        return items
    }


}