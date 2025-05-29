package org.caojun.library.nettools.weather

data class CurrentWeather(
    val time: String,//"2025-05-28T12:15"
    val interval: Int,//900
    val temperature: Double,//22.1
    val windspeed: Double,//5.4
    val winddirection: Int,//110
    val is_day: Int,//0
    val weathercode: Int,//1
)