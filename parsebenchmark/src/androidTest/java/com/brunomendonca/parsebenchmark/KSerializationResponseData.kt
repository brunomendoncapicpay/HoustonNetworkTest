package com.brunomendonca.parsebenchmark

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KSerializationResponse(
    @SerialName("value")
    val value: String,
    @SerialName("experiment")
    val experiment: KSerializationExperimentResponse? = null,
    @SerialName("track")
    val trackSources: List<String>? = null
)

@Serializable
data class KSerializationExperimentResponse(
    @SerialName("name")
    val name: String,
    @SerialName("variant")
    val variant: String
)