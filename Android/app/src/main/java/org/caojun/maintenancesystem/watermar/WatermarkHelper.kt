package org.caojun.maintenancesystem.watermar

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.caojun.library.database.PreferencesHelper
import org.caojun.library.nettools.ip.CachedIpLocationService
import org.caojun.library.nettools.ip.IpLocationResponse
import org.caojun.library.nettools.ip.IpLocationService
import org.caojun.library.nettools.toEnum
import org.caojun.library.nettools.weather.CachedOpenMeteoService
import org.caojun.library.nettools.weather.CurrentWeather
import org.caojun.library.nettools.weather.CurrentWeatherUnits
import org.caojun.library.nettools.weather.OpenMeteoService
import org.caojun.library.nettools.weather.WeatherCode
import org.caojun.library.nettools.weather.WindDirection

object WatermarkHelper {

    interface GetListener {
        fun onResult(location: IpLocationResponse?, weather: CurrentWeather?, units: CurrentWeatherUnits?, weatherCode: WeatherCode?, wind: WindDirection?)
    }
    fun get(context: Context, lifecycleOwner: LifecycleOwner, listener: GetListener) {

        val preferencesHelper = PreferencesHelper(context)

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val location = CachedIpLocationService(IpLocationService(), preferencesHelper).getCurrentLocation()
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

            val currentWeather = CachedOpenMeteoService(OpenMeteoService(), preferencesHelper).getCurrentWeather(lat, lon)
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

    fun translate(watermarkTemplate: WatermarkTemplate,
                          location: IpLocationResponse?,
                          weather: CurrentWeather?,
                          units: CurrentWeatherUnits?,
                          weatherCode: WeatherCode?,
                          wind: WindDirection?): String {
        return when (watermarkTemplate) {
            WatermarkTemplate.PLACEHOLDER_LONGITUDE -> {
                location?.lon.toString()
            }
            WatermarkTemplate.PLACEHOLDER_LATITUDE -> {
                location?.lat.toString()
            }
            WatermarkTemplate.PLACEHOLDER_ADDRESS -> {
                "${location?.regionName} ${location?.city}"
            }
            WatermarkTemplate.PLACEHOLDER_WEATHER -> {
                val sb = StringBuilder()
                if (weatherCode != null) {
                    sb.append("${weatherCode.description} ${weatherCode.icon} ")
                }
                if (weather != null && units != null) {
                    sb.append("${weather.temperature} ${units.temperature} ")
                }
                if (wind != null) {
                    sb.append("${wind.fullName} (${wind.emoji})")
                }
                sb.toString()
            }
            WatermarkTemplate.PLACEHOLDER_DATE_TIME -> {
                WatermarkTemplate.getCurrentDateTime()
            }
        }
    }

    private const val KEY_LIST_WatermarkTemplate = "KEY_LIST_WatermarkTemplate"
    fun save(context: Context, lifecycleOwner: LifecycleOwner, list: List<WatermarkTemplate>) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val preferencesHelper = PreferencesHelper(context)
            preferencesHelper.putObject(KEY_LIST_WatermarkTemplate, list)
        }
    }
    interface LoadListener {
        fun onReset(watermark: String)
    }
    fun load(context: Context, lifecycleOwner: LifecycleOwner, listener: LoadListener) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val preferencesHelper = PreferencesHelper(context)
                val listString = preferencesHelper.getObject<List<String>>(KEY_LIST_WatermarkTemplate)
                if (listString .isNullOrEmpty()) {
                    listener.onReset("")
                    return@launch
                }
                val list = ArrayList<WatermarkTemplate>()
                for (string in listString) {
                    val enum = string.toEnum(WatermarkTemplate::class.java) ?: continue
                    list.add(enum)
                }
                if (list.isEmpty()) {
                    listener.onReset("")
                    return@launch
                }
                val location = CachedIpLocationService(IpLocationService(), preferencesHelper).getCurrentLocation()
                if (location == null) {
                    listener.onReset("")
                    return@launch
                }
                val lat = location.lat
                val lon = location.lon
                val currentWeather = CachedOpenMeteoService(OpenMeteoService(), preferencesHelper).getCurrentWeather(lat, lon)
                if (currentWeather == null) {
                    listener.onReset("")
                    return@launch
                }
                val weather = currentWeather.current_weather
                val units = currentWeather.current_weather_units
                val weatherCode = WeatherCode.fromCode(weather.weathercode)
                val windDirection = WindDirection.fromDegrees(weather.winddirection)
                val sb = StringBuilder()
                for (watermarkTemplate in list) {
                    val value = translate(watermarkTemplate, location, weather, units, weatherCode, windDirection)
                    sb.append("${watermarkTemplate.description}: $value\n")
                }
                listener.onReset(sb.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onReset("")
            }
        }
    }
}