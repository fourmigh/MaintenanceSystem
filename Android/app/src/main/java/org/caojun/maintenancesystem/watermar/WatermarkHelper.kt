package org.caojun.maintenancesystem.watermar

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.caojun.library.nettools.ip.IpLocationResponse
import org.caojun.library.nettools.ip.IpLocationService
import org.caojun.library.nettools.weather.CurrentWeather
import org.caojun.library.nettools.weather.CurrentWeatherUnits
import org.caojun.library.nettools.weather.OpenMeteoService
import org.caojun.library.nettools.weather.WeatherCode
import org.caojun.library.nettools.weather.WindDirection

object WatermarkHelper {

    interface Listener {
        fun onResult(location: IpLocationResponse?, weather: CurrentWeather?, units: CurrentWeatherUnits?, weatherCode: WeatherCode?, wind: WindDirection?)
    }
    fun get(lifecycleOwner: LifecycleOwner, listener: Listener) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val location = IpLocationService().getCurrentLocation()
            if (location == null) {
                listener.onResult(null, null, null, null, null)
                return@launch
            }
            val lat = location.lat
            val lon = location.lon
//            val city = location.city
//            val region = location.regionName
//            val ip = location.query
//            val isp = location.isp

            val currentWeather = OpenMeteoService().getCurrentWeather(lat, lon)
            if (currentWeather == null) {
                listener.onResult(location, null, null, null, null)
                return@launch
            }
            val weather = currentWeather.current_weather
            val units = currentWeather.current_weather_units
            val weatherCode = WeatherCode.fromCode(weather.weathercode)
//            println("天气: ${weatherCode.description} ${weatherCode.icon}")
//            println("气温: ${weather.temperature} ${units.temperature}")

            val windDirection = WindDirection.fromDegrees(weather.winddirection)

//            println("风向: ${windDirection.fullName} (${windDirection.shortName})")
//            println("符号: ${windDirection.emoji}")
//            println("角度范围: ${windDirection.degrees}")
            listener.onResult(location, weather, units, weatherCode, windDirection)
        }
    }
}