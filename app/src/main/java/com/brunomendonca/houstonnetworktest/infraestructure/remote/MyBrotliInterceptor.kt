package com.brunomendonca.houstonnetworktest.infraestructure.remote

import com.brunomendonca.houstonnetworktest.presentation.main.MainActivity
import com.brunomendonca.houstonnetworktest.utils.getCurrentTime
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.brotli.BrotliInterceptor

class MyBrotliInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val unzipTimeStartAt = getCurrentTime()

        val result = BrotliInterceptor.intercept(chain)

        val unzipTimeEndAt = getCurrentTime()
        MainActivity.unzipTime = unzipTimeEndAt -unzipTimeStartAt

        return result
    }
}