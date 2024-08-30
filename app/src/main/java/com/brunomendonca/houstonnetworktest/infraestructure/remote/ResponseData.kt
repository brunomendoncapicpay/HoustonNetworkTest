package com.brunomendonca.houstonnetworktest.infraestructure.remote

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

@Serializable
data class TestResponseWrapper(
    @SerialName("name")
    val name: String,
    @SerialName("value")
    val value: Boolean,
    @SerialName("id")
    val id: String
)