package com.brunomendonca.parsebenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun parseWithKSerialization() {
//        val jsonBody = "{\n" +
//                "  \"3_dias_vencimento_1_boleto\": {\n" +
//                "    \"value\": \"false\"\n" +
//                "  } }"
        val jsonBody = this.javaClass.classLoader?.getResource("flags_pf.json")?.readText() ?: "{}"
        benchmarkRule.measureRepeated {
            Json.decodeFromString<Map<String, FlagResponseWrapper>>(
                jsonBody
            )
        }
    }
}