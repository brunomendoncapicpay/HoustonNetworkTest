package com.brunomendonca.parsebenchmark

data class Response(
    val value: String,
    val experiment: ExperimentResponse? = null,
    val track: List<String>? = null
)

data class ExperimentResponse(
    val name: String,
    val variant: String
)