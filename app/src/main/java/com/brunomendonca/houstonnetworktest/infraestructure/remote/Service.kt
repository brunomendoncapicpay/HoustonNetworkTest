package com.brunomendonca.houstonnetworktest.infraestructure.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface Service {
    @Suppress("LongParameterList")
    //@GET("flags/api/v2/app/{appId}")
    //@GET("flags")
    @GET("teste/")
    suspend fun getFlags(
        //@Path("appId") appId: String,
        @Header("appId") appId: String,
        @Header("x-consumer-id") consumerId: String?,
        @Header("device_id") deviceId: String?,
        @Header("x-request-origin") requestOrigin: String,
        @Header("device_os") deviceOs: String,
        @Header("os_version") osVersion: String?,
        @Header("app_version") appVersion: String?
    ): Response<ResponseBody>


    @GET("flags")
    suspend fun test(): Response<List<TestResponseWrapper>>
}