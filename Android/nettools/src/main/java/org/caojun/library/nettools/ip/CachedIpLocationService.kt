package org.caojun.library.nettools.ip

import android.util.Log
import org.caojun.library.database.PreferencesHelper

class CachedIpLocationService(
    private val delegate: IpLocationService,
    private val preferencesHelper: PreferencesHelper
) {
    // 缓存过期时间（24小时，因为IP位置不常变化）
    private companion object {
        const val CACHE_EXPIRY_TIME_MS = 24 * 60 * 60 * 1000L
        const val IP_CACHE_KEY_PREFIX = "cached_ip_location_"
        const val CURRENT_LOCATION_CACHE_KEY = "cached_current_location"
        const val CACHE_TIMESTAMP_KEY_PREFIX = "ip_location_timestamp_"
    }

    /**
     * 获取当前IP的位置信息（带缓存）
     */
    suspend fun getCurrentLocation(): IpLocationResponse? {
        // 1. 尝试从缓存获取
        val cachedData = getCachedLocation(CURRENT_LOCATION_CACHE_KEY)
        if (cachedData != null) {
            Log.i("Cached", "getCachedLocation")
            return cachedData
        }

        // 2. 从原始服务获取
        return try {
            val freshData = delegate.getCurrentLocation()

            // 3. 缓存新数据
            freshData?.let {
                cacheLocation(CURRENT_LOCATION_CACHE_KEY, it)
                val cacheKey = "$IP_CACHE_KEY_PREFIX${freshData.query}"
                cacheLocation(cacheKey, it)
            }
            Log.i("Cached", "getCurrentLocation")
            freshData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取指定IP的位置信息（带缓存）
     * @param ip 要查询的IP地址
     */
    suspend fun getLocationByIp(ip: String): IpLocationResponse? {
        val cacheKey = "$IP_CACHE_KEY_PREFIX$ip"

        // 1. 尝试从缓存获取
        val cachedData = getCachedLocation(cacheKey)
        if (cachedData != null) {
            return cachedData
        }

        // 2. 从原始服务获取
        return try {
            val freshData = delegate.getLocationByIp(ip)

            // 3. 缓存新数据
            freshData?.let { cacheLocation(cacheKey, it) }
            freshData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getCachedLocation(cacheKey: String): IpLocationResponse? {
        // 检查缓存是否过期
        val timestampKey = "$CACHE_TIMESTAMP_KEY_PREFIX$cacheKey"
        val lastCacheTime = preferencesHelper.getLong(timestampKey, 0L)
        if (System.currentTimeMillis() - lastCacheTime > CACHE_EXPIRY_TIME_MS) {
            return null
        }

        // 从缓存获取数据
        return preferencesHelper.getObject<IpLocationResponse>(cacheKey)
    }

    private fun cacheLocation(cacheKey: String, data: IpLocationResponse) {
        val timestampKey = "$CACHE_TIMESTAMP_KEY_PREFIX$cacheKey"
        preferencesHelper.putObject(cacheKey, data)
        preferencesHelper.putLong(timestampKey, System.currentTimeMillis())
    }

    /**
     * 清除当前IP位置的缓存
     */
    fun clearCurrentLocationCache() {
        clearCache(CURRENT_LOCATION_CACHE_KEY)
    }

    /**
     * 清除指定IP位置的缓存
     * @param ip 要清除缓存的IP地址
     */
    fun clearIpCache(ip: String) {
        clearCache("$IP_CACHE_KEY_PREFIX$ip")
    }

    private fun clearCache(cacheKey: String) {
        val timestampKey = "$CACHE_TIMESTAMP_KEY_PREFIX$cacheKey"
        preferencesHelper.remove(cacheKey)
        preferencesHelper.remove(timestampKey)
    }
}