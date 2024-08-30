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
package com.brunomendonca.houstonnetworktest.infraestructure.remote

import android.content.Context
import android.util.Log
import com.google.android.gms.net.CronetProviderInstaller
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.net.cronet.okhttptransport.CronetInterceptor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.chromium.net.CronetEngine
import org.chromium.net.CronetProvider

class HttpClientHolder {
    private val client = AtomicReference<OkHttpClient>()
    private val syncInitInvoked = AtomicBoolean()
    private val asyncInitInvoked = AtomicBoolean()

    @Volatile
    private var asyncInitTask: Task<OkHttpClient>? = null

    val httpClient: OkHttpClient?
        get() = client.get()

    /**
     * Initializes the vanilla OkHttp client. This should generally be done as soon as practical in
     * the application's lifecycle to ensure that an HTTP client is always available.
     */
    fun fastSynchronousInit() {
        if (syncInitInvoked.getAndSet(true)) {
            // Already invoked
            return
        }
        client.set(createOkHttpBuilder().build())
    }

    /**
     * Attempts to initialize the Cronet transport layer and an OkHttp client using it.
     *
     *
     * While it's not necessary to invoke this method early on, it's desirable to begin the
     * initialization early in the application's lifecycle process to make the most of performance
     * benefits of the transport layer.
     */
    fun slowAsynchronousInit(context: Context?): Task<OkHttpClient>? {
        if (asyncInitInvoked.getAndSet(true)) {
            // Already invoked
            return asyncInitTask
        }
        asyncInitTask =
            CronetProviderInstaller.installProvider(context!!)
                .continueWith { task: Task<Void?> ->
                    // Call to propagate any errors from the first invocation. Don't attempt to
                    // recover from the error as it involves installing updates or other UX-disturbing
                    // activities with a lot of friction. We're better off just using plain OkHttp
                    // in such a case.
                    task.result

                    for (provider in CronetProvider.getAllProviders(context)) {
                        // We're not interested in using the fallback, we're better off sticking with
                        // the default OkHttp client in that case.
                        if (!provider.isEnabled || provider.name == CronetProvider.PROVIDER_NAME_FALLBACK
                        ) {
                            continue
                        }
                        return@continueWith setupCronetEngine(provider.createBuilder()).build()
                    }
                    throw IllegalStateException("No enabled Cronet providers found!")
                }
                .continueWith(
                    Continuation<CronetEngine, OkHttpClient> { task: Task<CronetEngine> ->
                        val engine = task.result
                        val cronetInterceptor =
                            CronetInterceptor.newBuilder(engine).build()
                        createOkHttpBuilder().addInterceptor(cronetInterceptor).build()
                    })
                .addOnCompleteListener { task: Task<OkHttpClient> ->
                    if (task.isSuccessful) {
                        client.set(task.result)
                    } else if (!syncInitInvoked.get()) {
                        Log.i(
                            TAG,
                            "Async HTTP engine initialization finished unsuccessfully but sync init "
                                    + "wasn't finished yet! Prefer to perform the fast sync init early on "
                                    + "to have a HTTP client available at all times."
                        )
                        fastSynchronousInit()
                    } // Else we just use the vanilla OkHttp client we already have.
                }
        return asyncInitTask
    }

    /**
     * Customizes the Cronet engine parameters in the provided builder.
     *
     *
     * The application should alter this method to match its needs. For demonstration purposes, we
     * enable Brotli.
     *
     * @return the received parameter for chaining
     */
    private fun setupCronetEngine(engineBuilder: CronetEngine.Builder): CronetEngine.Builder {
        return engineBuilder.enableBrotli(true)
    }

    /**
     * Creates and returns a new customized OkHttp client builder.
     *
     *
     * The application should alter this method to match its needs. For demonstration purposes we
     * set the call timeout and add a simple logging interceptor.
     */
    private fun createOkHttpBuilder(): OkHttpClient.Builder {
        // Set up your OkHttp parameters here
        return OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                    Log.i(TAG, chain.request().url.toString())
                    chain.proceed(chain.request())
                })
    }

    companion object {
        private const val TAG = "HttpClientHolder"
    }
}