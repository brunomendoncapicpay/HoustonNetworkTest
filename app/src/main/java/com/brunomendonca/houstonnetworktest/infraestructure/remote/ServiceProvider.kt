package com.brunomendonca.houstonnetworktest.infraestructure.remote

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ServiceProvider {
    private const val CACHE_SIZE_BYTES : Long = 1024 * 1024 * 2
    private lateinit var retrofit: Retrofit

    fun initializeRetrofit(context: Context) {
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://6402162bab6b7399d0b3790f.mockapi.io/")
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    fun service(): Service = retrofit.create(Service::class.java)
}