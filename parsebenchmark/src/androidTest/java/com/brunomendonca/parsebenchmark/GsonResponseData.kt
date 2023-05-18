package com.brunomendonca.parsebenchmark

data class GsonResponse(
    val value: String,
    val experiment: KSerializationExperimentResponse? = null,
    val trackSources: List<String>? = null
)

data class GsonExperimentResponse(
    val name: String,
    val variant: String
)