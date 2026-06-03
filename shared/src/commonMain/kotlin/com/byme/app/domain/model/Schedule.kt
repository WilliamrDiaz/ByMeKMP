package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val id: String = "",
    val day: String = "",
    val hours: String = "",
)