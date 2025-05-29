package org.caojun.library.nettools.weather

data class CurrentWeatherUnits(
    val time: String,//"iso8601"
    val interval: String,//"seconds"
    val temperature: String,//"°C"
    val windspeed: String,//"km/h"
    val winddirection: String,//"°"
    val is_day: String,//""
    val weathercode: String,//"wmo code"
)