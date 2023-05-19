package com.brunomendonca.parsebenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val BENCHMARK_WEIGHT = 1000

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ParseBenchmark {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher)

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @After
    fun tearDown() {
        testScope.cancel()
    }

    @Test
    fun decodeWithKSerialization() {
        val jsonBody = FlagGenerator.getJson(BENCHMARK_WEIGHT)
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                Json.decodeFromString<Map<String, KSerializationResponse>>(
                    jsonBody
                )
            }
        }
    }

    @Test
    fun encodeWithKSerialization() {
        val flags = FlagGenerator.getFlagsKSerialization(BENCHMARK_WEIGHT)
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                Json.encodeToJsonElement(flags)
            }
        }
    }

    @Test
    fun decodeWithGson() {
        val jsonBody = FlagGenerator.getJson(BENCHMARK_WEIGHT)
        val gson = Gson()
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                gson.fromJson(
                    jsonBody, Response::class.java
                )
            }
        }
    }

    @Test
    fun encodeWithGson() {
        val flags = FlagGenerator.getFlagsGson(BENCHMARK_WEIGHT)
        val gson = Gson()
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                gson.toJson(flags)
            }
        }
    }

    @Test
    fun decodeWithMoshi() {
        val jsonBody = FlagGenerator.getJson(BENCHMARK_WEIGHT)
        val moshi = Moshi.Builder().build()
        val type: Type = Types.newParameterizedType(
            MutableMap::class.java,
            String::class.java,
            MoshiResponse::class.java
        )

        val jsonAdapter = moshi.adapter<Map<String, MoshiResponse>>(type)
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                jsonAdapter.fromJson(jsonBody)
            }
        }
    }

    @Test
    fun encodeWithMoshi() {
        val flags = FlagGenerator.getFlagsMoshi(BENCHMARK_WEIGHT)
        val moshi = Moshi.Builder().build()
        val type: Type = Types.newParameterizedType(
            MutableMap::class.java,
            String::class.java,
            MoshiResponse::class.java
        )
        val jsonAdapter = moshi.adapter<Map<String, MoshiResponse>>(type)
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                jsonAdapter.toJson(flags)
            }
        }
    }
}