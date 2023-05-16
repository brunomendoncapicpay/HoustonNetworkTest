package com.brunomendonca.houstonnetworktest.infraestructure.remote

import com.brunomendonca.houstonnetworktest.presentation.main.MainActivity
import com.brunomendonca.houstonnetworktest.utils.getCurrentTime
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.RealResponseBody
import okio.GzipSource
import okio.IOException
import okio.buffer


internal class GzipInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Accept-Encoding", "gzip")
            .build()

        val response = chain.proceed(request)
        return unzip(chain.request(), response)
    }

    @Throws(IOException::class)
    private fun unzip(request: Request, response: Response): Response {
        val responseBuilder = response.newBuilder().request(request)
        val responseBody = response.body
        return if (responseBody != null) {
            val unzipTimeStartAt = getCurrentTime()

            val gzipSource = GzipSource(responseBody.source())
            val strippedHeaders = response.headers.newBuilder()
                .removeAll("Content-Encoding")
                .removeAll("Content-Length")
                .build()
            responseBuilder.headers(strippedHeaders)
            val contentType = response.header("Content-Type")
            responseBuilder.body(RealResponseBody(contentType, -1L, gzipSource.buffer()))
            val result = responseBuilder.build()

            val unzipTimeEndAt = getCurrentTime()
            MainActivity.unzipTime = unzipTimeEndAt -unzipTimeStartAt

            result
        } else {
            response
        }
    }
}