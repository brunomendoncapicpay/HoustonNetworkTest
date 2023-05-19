package com.brunomendonca.parsebenchmark

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoshiResponse(
    val value: String,
    val experiment: MoshiExperimentResponse? = null,
    val trackSources: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class MoshiExperimentResponse(
    val name: String,
    val variant: String
)