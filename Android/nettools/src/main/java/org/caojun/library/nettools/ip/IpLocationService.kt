package org.caojun.library.nettools.ip

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * IP 定位服务封装
 */
class IpLocationService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://ip-api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(IpApi::class.java)

    /**
     * 获取当前IP的位置信息
     */
    suspend fun getCurrentLocation(): IpLocationResponse {
        return api.getLocation()
    }

    /**
     * 获取指定IP的位置信息
     * @param ip 要查询的IP地址
     */
    suspend fun getLocationByIp(ip: String): IpLocationResponse {
        return api.getLocation(ip)
    }

    /**
     * IP-API 接口定义
     */
    private interface IpApi {
        @GET("json/")
        suspend fun getLocation(
            @Query("query") ip: String? = null,
            @Query("lang") lang: String = "zh-CN",
        ): IpLocationResponse
    }
}