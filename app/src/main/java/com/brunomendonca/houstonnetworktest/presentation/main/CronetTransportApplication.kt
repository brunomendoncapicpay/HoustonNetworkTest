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
package com.brunomendonca.houstonnetworktest.presentation.main

import android.app.Application
import com.brunomendonca.houstonnetworktest.infraestructure.remote.HttpClientHolder
import com.google.android.gms.tasks.Task
import com.google.common.base.Preconditions
import okhttp3.OkHttpClient

class CronetTransportApplication : Application() {
    val httpClientHolder: HttpClientHolder = HttpClientHolder()

    override fun onCreate() {
        super.onCreate()
        httpClientHolder.fastSynchronousInit()
    }

    val httpClient: OkHttpClient
        get() = Preconditions.checkNotNull(httpClientHolder.httpClient)

    fun invokeAsyncHttpClientInit(): Task<OkHttpClient>? {
        return httpClientHolder.slowAsynchronousInit(applicationContext)
    }
}