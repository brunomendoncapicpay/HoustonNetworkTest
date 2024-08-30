/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.cronet.okhttptransport

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brunomendonca.houstonnetworktest.databinding.ActivityOkHttpBinding
import com.brunomendonca.houstonnetworktest.presentation.main.CronetTransportApplication
import com.brunomendonca.houstonnetworktest.infraestructure.remote.ImageRepository
import com.google.android.gms.tasks.Task
import com.google.common.base.Stopwatch
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OkHttpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOkHttpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOkHttpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    fun onRequestButtonClicked(view: View?) {
        val client = castedApplication.httpClient

        val totalSize = AtomicInteger()
        val finishedRequests = AtomicInteger()
        val failed = AtomicBoolean()

        val stopwatch = Stopwatch.createUnstarted()

        val callback: Callback =
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (failed.getAndSet(true)) {
                        return
                    }
                    runOnUiThread {
                        binding.displayMessage.text =
                            "Failed : " + e.message
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (failed.get()) {
                        return
                    }

                    val total = totalSize.addAndGet(response.body!!.bytes().size)
                    val finished = finishedRequests.incrementAndGet()
                    if (finished == ImageRepository.numberOfImages()) {
                        stopwatch.stop()
                        runOnUiThread {
                            binding.displayMessage.text =
                                String.format(
                                    Locale.ENGLISH,
                                    "%s fetched %d bytes in %s millis",
                                    client,
                                    total,
                                    stopwatch.elapsed(TimeUnit.MILLISECONDS)
                                )
                        }
                    }
                }
            }

        stopwatch.start()
        for (i in 0 until ImageRepository.numberOfImages()) {
            val request: Request = Request.Builder().url(ImageRepository.getImage(i)).build()
            client.newCall(request).enqueue(callback)
        }
    }

    fun onInitButtonClicked(v: View?) {
        val task: Task<OkHttpClient>? = castedApplication.invokeAsyncHttpClientInit()
        task!!.addOnCompleteListener { lambdaTask: Task<OkHttpClient?> ->

            val message = if (lambdaTask.isSuccessful) {
                "Successfully initialized Cronet transport"
            } else {
                "An error has occurred: " + lambdaTask.exception!!.message
            }
            runOnUiThread {
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private val castedApplication: CronetTransportApplication
        // correct by definition
        get() = application as CronetTransportApplication
}