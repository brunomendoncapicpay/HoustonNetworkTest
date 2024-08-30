package com.brunomendonca.parsebenchmark

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object FlagJsonDecoder {

    fun decode(json: String): Map<String, Response> = JSONObject(json).toFlagSet()

    private val mutex: Mutex = Mutex()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @Throws(JSONException::class)
    private fun JSONObject.toFlagSet(): Map<String, Response> {
        val map: MutableMap<String, Response> = mutableMapOf()
        val keys = this.keys()
        while (keys.hasNext()) {

            val key = keys.next()
            val value = this[key]
            map[key] = (value as JSONObject).toResponse()
        }
        return map
    }

    @Throws(JSONException::class)
    private fun JSONObject.asyncToFlagSet(): Map<String, Response> {
        val map: MutableMap<String, Response> = mutableMapOf()
        val keys = this.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            coroutineScope.launch {
                val value = (this@asyncToFlagSet[key] as JSONObject).toResponse()
                mutex.withLock {
                    map[key] = value
                }
            }
        }
        return map
    }

    private fun JSONObject.toResponse() = Response(
        value = getString("value"),
        experiment = getExperimentOrNul(),
        track = getTrackToggles()
    )

    private fun JSONObject.getExperimentOrNul(): ExperimentResponse? =
        runCatching { getJSONObject("experiment").toExperiment() }.getOrNull()

    private fun JSONObject.toExperiment() = ExperimentResponse(
        name = getString("name"),
        variant = getString("variant")
    )

    private fun JSONObject.getTrackToggles(): List<String>? =
        runCatching { getJSONArray("track").toList() }.getOrNull()

    private fun JSONArray.toList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until this.length()) {
            list.add(getString(i))
        }
        return list
    }

}
