package com.brunomendonca.houstonnetworktest.infraestructure.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlagResponseWrapper(
    @SerialName("value")
    val value: String,
    @SerialName("experiment")
    val experiment: ExperimentResponseWrapper? = null,
    @SerialName("track")
    val trackSources: List<String>? = null
)

@Serializable
data class ExperimentResponseWrapper(
    @SerialName("name")
    val name: String,
    @SerialName("variant")
    val variant: String
)