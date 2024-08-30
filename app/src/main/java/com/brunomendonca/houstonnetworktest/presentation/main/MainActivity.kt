package com.brunomendonca.houstonnetworktest.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.brunomendonca.houstonnetworktest.databinding.ActivityMainBinding
import com.brunomendonca.houstonnetworktest.infraestructure.remote.ServiceProvider
import com.brunomendonca.houstonnetworktest.utils.getCurrentTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val service by lazy { ServiceProvider.service() }

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
            runCatching {
                val totalTimeStartAt = getCurrentTime()

                val response = service.test()
                val totalTimeEndAt = getCurrentTime()
                val totalTime = totalTimeEndAt - totalTimeStartAt

                val exactConnectionTime =
                    response.raw().receivedResponseAtMillis - response.raw().sentRequestAtMillis

                withContext(Dispatchers.Main) {
                    val requestInfo = StringBuilder()
                        .append("Total time: $totalTime\n\n")
                        .append("Connection Time = ${response.raw().sentRequestAtMillis - totalTimeStartAt}\n\n")
                        .append("Request Time: $exactConnectionTime \n\n")
                        .append("Response code: ${response.code()}\n\n")
//                        .append("Sent at millis ${response.raw().sentRequestAtMillis}\n\n")
//                        .append("Receive at millis ${response.raw().receivedResponseAtMillis}\n\n")
                        .toString()
                    Log.d("RequestInfo", requestInfo.replace("\n", " "))
                    binding.tvRequestInfo.text = requestInfo
                    binding.swipeRefresh.isRefreshing = false
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Log.e("RequestInfo", it.message.toString())
                    binding.tvRequestInfo.text = it.message
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }
}