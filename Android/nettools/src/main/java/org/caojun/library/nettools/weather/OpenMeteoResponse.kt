package org.caojun.library.nettools.weather

data class OpenMeteoResponse(
    val latitude: Double,//32
    val longitude: Double,//118.75
    val generationtime_ms: Double,//0.0289678573608398
    val utc_offset_seconds: Int,//0
    val timezone: String,//"GMT"
    val timezone_abbreviation: String,//"GMT"
    val elevation: Int,//22
    val current_weather_units: CurrentWeatherUnits,
    val current_weather: CurrentWeather,

    /**
     * {
     *   "latitude": 32,
     *   "longitude": 118.75,
     *   "generationtime_ms": 0.0289678573608398,
     *   "utc_offset_seconds": 0,
     *   "timezone": "GMT",
     *   "timezone_abbreviation": "GMT",
     *   "elevation": 22,
     *   "current_weather_units": {
     *     "time": "iso8601",
     *     "interval": "seconds",
     *     "temperature": "°C",
     *     "windspeed": "km/h",
     *     "winddirection": "°",
     *     "is_day": "",
     *     "weathercode": "wmo code"
     *   },
     *   "current_weather": {
     *     "time": "2025-05-28T12:15",
     *     "interval": 900,
     *     "temperature": 22.1,
     *     "windspeed": 5.4,
     *     "winddirection": 110,
     *     "is_day": 0,
     *     "weathercode": 1
     *   }
     * }
     */
)