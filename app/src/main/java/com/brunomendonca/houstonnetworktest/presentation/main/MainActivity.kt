package com.brunomendonca.houstonnetworktest.presentation.main

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brunomendonca.houstonnetworktest.BuildConfig
import com.brunomendonca.houstonnetworktest.databinding.ActivityMainBinding
import com.brunomendonca.houstonnetworktest.infraestructure.remote.ServiceProvider
import com.brunomendonca.houstonnetworktest.infraestructure.remote.model.FlagResponseWrapper
import com.brunomendonca.houstonnetworktest.utils.getCurrentTime
import java.net.HttpURLConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    companion object {
        var unzipTime: Long = -1
    }

    private lateinit var binding: ActivityMainBinding
    private val flagsAdapter = FlagsAdapter(listOf("Empty State"))
    private val houstonService by lazy { ServiceProvider.getHoustonService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ServiceProvider.initializeRetrofit(applicationContext)

        binding.swipeRefresh.isRefreshing = true
        refreshData()

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }

    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            val totalTimeStartAt = getCurrentTime()
            val response = houstonService.getFlags(
                appId = "com.picpay",
                consumerId = "10870309",
                deviceId = BuildConfig.APPLICATION_ID,
                requestOrigin = "app-android",
                deviceOs = "android",
                osVersion = Build.VERSION.RELEASE,
                appVersion = "11.0.110"
            )
            val eTag = response.raw().networkResponse != null &&
                    response.raw().networkResponse!!.code ==
                    HttpURLConnection.HTTP_NOT_MODIFIED

            var flags : Map<String, FlagResponseWrapper>? = null
            val parseTime = if(!eTag) {
                val parseTimeStartAt = getCurrentTime()
                flags =
                    Json.decodeFromString<Map<String, FlagResponseWrapper>>(
                        response.body()!!.string()
                    )
                val parseTimeEndAt = getCurrentTime()
                parseTimeEndAt - parseTimeStartAt
            } else 0

//            var retryTime = 0L
//            var priorResponse = response.raw().priorResponse
//            while(priorResponse != null) {
//                retryTime += priorResponse.receivedResponseAtMillis - priorResponse.sentRequestAtMillis
//                priorResponse = priorResponse.priorResponse
//            }

            val totalTimeEndAt = getCurrentTime()
            val totalTime = totalTimeEndAt - totalTimeStartAt

            val exactConnectionTime =
                response.raw().receivedResponseAtMillis - response.raw().sentRequestAtMillis

            withContext(Dispatchers.Main) {
                binding.tvRequestInfo.text = StringBuilder()
                    .append("Total time: $totalTime\n\n")
                    .append("Connection Time = ${response.raw().sentRequestAtMillis - totalTimeStartAt}\n\n")
                    .append("Request Time: $exactConnectionTime \n\n")
                    .append("Parse time: $parseTime\n\n")
                    .append("Kong Upstream Latency ${response.headers()["X-Kong-Upstream-Latency"]}\n\n")
                    .append("Kong Proxy Latency ${response.headers()["X-Kong-Proxy-Latency"]}\n\n")
                    .append("etag = $eTag\n\n")
                    .append("Flag Count ${flags?.size ?: 0}\n\n")
                    //.append("Response code: ${response.code()}\n\n")
                    //.append("Unzip time: $unzipTime\n\n")
                    //.append("Connection Time Prior: $retryTime \n\n")
                    //.append("Download started at millis $downloadTimeStartAt\n\n")
                    //.append("Sent at millis ${response.raw().sentRequestAtMillis}\n\n")
                    //.append("Time Between starts ${response.raw().sentRequestAtMillis - downloadTimeStartAt}\n\n")
                    //.append("Download Received at millis $downloadTimeEndAt\n\n")
                    //.append("Receive at millis ${response.raw().receivedResponseAtMillis}\n\n")
                    //.append("Time Between ends ${downloadTimeEndAt - response.raw().receivedResponseAtMillis}\n\n")
                    .toString()
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
}