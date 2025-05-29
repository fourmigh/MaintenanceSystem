package org.caojun.library.nettools.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class OpenMeteoService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    suspend fun getCurrentWeather(lat: Double, lon: Double): OpenMeteoResponse {
        return retrofit.create(OpenMeteoApi::class.java)
            .getWeather(
                latitude = lat,
                longitude = lon
            )
    }

    interface OpenMeteoApi {
        @GET("v1/forecast")
        suspend fun getWeather(
            @Query("latitude") latitude: Double,
            @Query("longitude") longitude: Double,
            @Query("current_weather") currentWeather: Boolean = true
        ): OpenMeteoResponse
    }
}