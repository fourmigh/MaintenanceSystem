package org.caojun.library.nettools.weather

/**
 * Open-Meteo 天气代码枚举类
 * 基于 WMO 天气代码标准
 */
enum class WeatherCode(
    val code: Int,
    val description: String,
    val icon: String,
    val isPrecipitation: Boolean = false,
    val isSevere: Boolean = false
) {
    CLEAR_SKY(0, "晴天", "☀️"),
    MAINLY_CLEAR(1, "晴转多云", "🌤"),
    PARTLY_CLOUDY(2, "部分多云", "⛅"),
    OVERCAST(3, "多云", "☁️"),
    FOG(45, "雾", "🌫"),
    DEPOSITING_RIME_FOG(48, "冻雾", "❄️🌫"),

    // 降水类
    LIGHT_DRIZZLE(51, "小雨", "🌧", isPrecipitation = true),
    MODERATE_DRIZZLE(53, "中雨", "🌧", isPrecipitation = true),
    DENSE_DRIZZLE(55, "强毛毛雨", "🌧", isPrecipitation = true),
    LIGHT_FREEZING_DRIZZLE(56, "冻毛毛雨", "🌧❄️", isPrecipitation = true),
    DENSE_FREEZING_DRIZZLE(57, "强冻毛毛雨", "🌧❄️", isPrecipitation = true),
    SLIGHT_RAIN(61, "小雨", "🌦", isPrecipitation = true),
    MODERATE_RAIN(63, "中雨", "🌧", isPrecipitation = true),
    HEAVY_RAIN(65, "强降雨", "💦", isPrecipitation = true),
    LIGHT_FREEZING_RAIN(66, "冻雨", "🌧❄️", isPrecipitation = true),
    HEAVY_FREEZING_RAIN(67, "强冻雨", "💦❄️", isPrecipitation = true),

    // 降雪类
    SLIGHT_SNOWFALL(71, "小雪", "❄️", isPrecipitation = true),
    MODERATE_SNOWFALL(73, "中雪", "❄️❄️", isPrecipitation = true),
    HEAVY_SNOWFALL(75, "强降雪", "❄️❄️❄️", isPrecipitation = true),
    SNOW_GRAINS(77, "雪粒", "🌨", isPrecipitation = true),

    // 对流天气
    SLIGHT_RAIN_SHOWERS(80, "零星阵雨", "🌦", isPrecipitation = true),
    MODERATE_RAIN_SHOWERS(81, "中阵雨", "🌧", isPrecipitation = true),
    VIOLENT_RAIN_SHOWERS(82, "强阵雨", "⛈", isPrecipitation = true, isSevere = true),
    SLIGHT_SNOW_SHOWERS(85, "零星阵雪", "❄️🌨", isPrecipitation = true),
    HEAVY_SNOW_SHOWERS(86, "强阵雪", "❄️❄️🌨", isPrecipitation = true),

    // 极端天气
    THUNDERSTORM(95, "雷暴", "⚡️", isSevere = true),
    THUNDERSTORM_SLIGHT_HAIL(96, "雷暴伴小冰雹", "⚡️🧊", isSevere = true),
    THUNDERSTORM_HEAVY_HAIL(99, "雷暴伴强冰雹", "⚡️🧊🧊", isSevere = true),

    UNKNOWN(-1, "未知天气", "?");

    companion object {
        /**
         * 根据天气代码获取对应的枚举值
         */
        fun fromCode(code: Int): WeatherCode {
            return values().find { it.code == code } ?: UNKNOWN
        }

        /**
         * 获取天气类型分组
         */
        fun getWeatherType(code: Int): String {
            return when (fromCode(code)) {
                CLEAR_SKY, MAINLY_CLEAR, PARTLY_CLOUDY, OVERCAST -> "云量变化"
                FOG, DEPOSITING_RIME_FOG -> "雾"
                in LIGHT_DRIZZLE..HEAVY_FREEZING_RAIN -> "降雨"
                in SLIGHT_SNOWFALL..HEAVY_SNOW_SHOWERS -> "降雪"
                in THUNDERSTORM..THUNDERSTORM_HEAVY_HAIL -> "雷暴"
                else -> "其他"
            }
        }
    }
}