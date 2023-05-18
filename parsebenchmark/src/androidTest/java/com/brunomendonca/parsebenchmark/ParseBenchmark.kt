package com.brunomendonca.parsebenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.encodeToJsonElement
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
@RunWith(AndroidJUnit4::class)
class ParseBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun decodeWithKSerialization() {
        val jsonBody = FlagGenerator.getJson(BENCHMARK_WEIGHT)
        benchmarkRule.measureRepeated {
            Json.decodeFromString<Map<String, KSerializationResponse>>(
                jsonBody
            )
        }
    }

    @Test
    fun encodeWithKSerialization() {
        val flags = FlagGenerator.getFlagsKSerialization(BENCHMARK_WEIGHT)
        benchmarkRule.measureRepeated {
            Json.encodeToJsonElement(flags)
        }
    }

    @Test
    fun decodeWithGson() {
        val jsonBody = FlagGenerator.getJson(BENCHMARK_WEIGHT)
        benchmarkRule.measureRepeated {
            Gson().fromJson(
                jsonBody, GsonResponse::class.java
            )
        }
    }

    @Test
    fun encodeWithGson() {
        val flags = FlagGenerator.getFlagsGson(BENCHMARK_WEIGHT)
        benchmarkRule.measureRepeated {
            Gson().toJson(flags)
        }
    }
}