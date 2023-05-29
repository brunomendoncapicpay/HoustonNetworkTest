package com.brunomendonca.parsebenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

private const val BENCHMARK_WEIGHT = 4000

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
                val result: Map<String, KSerializationResponse> = Json.decodeFromString<Map<String, KSerializationResponse>>(
                    jsonBody
                )
                //runWithTimingDisabled { println("RESULT $result") }
            }
        }
    }

    @Test
    fun encodeWithKSerialization() {
        val flags = FlagGenerator.getFlagsKSerialization(BENCHMARK_WEIGHT)
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                val result: String = Json.encodeToJsonElement(flags).toString()
                //runWithTimingDisabled { println("RESULT $result") }
            }
        }
    }

    @Test
    fun decodeWithGson() {
        val jsonBody = FlagGenerator.getJson(BENCHMARK_WEIGHT)
        val type = object : TypeToken<Map<String, Response>>() {}.type
        val gson = Gson()
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                val result: Map<String, Response> = gson.fromJson(
                    jsonBody, type
                )
                //runWithTimingDisabled { println("RESULT $result") }
            }
        }
    }

    @Test
    fun encodeWithGson() {
        val flags = FlagGenerator.getFlagsGson(BENCHMARK_WEIGHT)
        val gson = Gson()
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                val result: String = gson.toJson(flags)
                //runWithTimingDisabled { println("RESULT $result") }
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
                val result: Map<String, MoshiResponse> = jsonAdapter.fromJson(jsonBody)!!
                //runWithTimingDisabled { println("RESULT $result") }
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
                val result: String = jsonAdapter.toJson(flags)
                //runWithTimingDisabled { println("RESULT $result") }
            }
        }
    }

    @Test
    fun decodeWithJackson() {
        val jsonBody = FlagGenerator.getJson(BENCHMARK_WEIGHT)
        val jackson = jacksonObjectMapper()
        val type = jackson.typeFactory.constructMapType(
            Map::class.java,
            String::class.java,
            Response::class.java
        )
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                val result: Map<String, Response> = jackson.readValue(
                    jsonBody, type
                )
                //runWithTimingDisabled { println("RESULT $result") }
            }
        }
    }

    @Test
    fun encodeWithJackson() {
        val flags = FlagGenerator.getFlagsGson(BENCHMARK_WEIGHT)
        val jackson = jacksonObjectMapper()
        benchmarkRule.measureRepeated {
            runTest(testDispatcher) {
                val result: String = jackson.writeValueAsString(flags)
                runWithTimingDisabled { println("RESULT $result") }
            }
        }
    }
}