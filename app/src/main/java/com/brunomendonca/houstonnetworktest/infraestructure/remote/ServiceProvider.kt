package com.brunomendonca.houstonnetworktest.infraestructure.remote

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ServiceProvider {
    private const val CACHE_SIZE_BYTES : Long = 1024 * 1024 * 2
    private lateinit var retrofit: Retrofit

    fun initializeRetrofit(context: Context) {
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            //.cache(Cache(context.cacheDir, CACHE_SIZE_BYTES))
            //.addInterceptor(GzipInterceptor())
            .build()

        retrofit = Retrofit.Builder()
            //.baseUrl("https://bizapi.ppay.me/api/")
            //.baseUrl("https://gateway.picpay.com/")
            //.baseUrl("https://private-03f550-houstontest.apiary-mock.com/")
            //.baseUrl("https://kwk8befbhl.execute-api.us-east-1.amazonaws.com/")
            .baseUrl("https://zdtlixu14m.execute-api.sa-east-1.amazonaws.com/")
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    fun getHoustonService(): HoustonService = retrofit.create(HoustonService::class.java)
}