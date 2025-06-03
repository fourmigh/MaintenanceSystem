package org.caojun.library.nettools.weather

import android.util.Log
import org.caojun.library.database.PreferencesHelper

class CachedOpenMeteoService(
    private val delegate: OpenMeteoService,
    private val preferencesHelper: PreferencesHelper
) {
    // 缓存过期时间（1小时）
    private companion object {
        const val CACHE_EXPIRY_TIME_MS = 60 * 60 * 1000L
        const val WEATHER_CACHE_KEY = "cached_weather_data"
        const val CACHE_TIMESTAMP_KEY = "weather_cache_timestamp"
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): OpenMeteoResponse? {
        // 1. 先尝试从缓存获取
        val cachedData = getCachedWeather(lat, lon)
        if (cachedData != null) {
            Log.i("Cached", "getCachedWeather")
            return cachedData
        }

        // 2. 缓存中没有或已过期，从原始服务获取
        return try {
            val freshData = delegate.getCurrentWeather(lat, lon)

            // 3. 将新数据保存到缓存
            freshData?.let { cacheWeather(lat, lon, it) }
            Log.i("Cached", "getCurrentWeather")
            freshData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getCachedWeather(lat: Double, lon: Double): OpenMeteoResponse? {
        // 检查缓存是否过期
        val lastCacheTime = preferencesHelper.getLong(CACHE_TIMESTAMP_KEY, 0L)
        if (System.currentTimeMillis() - lastCacheTime > CACHE_EXPIRY_TIME_MS) {
            return null
        }

        // 从缓存获取数据
        val cacheKey = getCacheKey(lat, lon)
        return preferencesHelper.getObject<OpenMeteoResponse>(cacheKey)
    }

    private fun cacheWeather(lat: Double, lon: Double, data: OpenMeteoResponse) {
        val cacheKey = getCacheKey(lat, lon)
        preferencesHelper.putObject(cacheKey, data)
        preferencesHelper.putLong(CACHE_TIMESTAMP_KEY, System.currentTimeMillis())
    }

    private fun getCacheKey(lat: Double, lon: Double): String {
        return "${WEATHER_CACHE_KEY}_${lat}_$lon"
    }

    // 清除特定位置的缓存
    fun clearCache(lat: Double, lon: Double) {
        val cacheKey = getCacheKey(lat, lon)
        preferencesHelper.remove(cacheKey)
    }

    // 清除所有天气缓存
    fun clearAllCache() {
        preferencesHelper.remove(CACHE_TIMESTAMP_KEY)
        // 这里需要知道所有缓存键才能完全清除，实际应用中可能需要更复杂的实现
    }
}