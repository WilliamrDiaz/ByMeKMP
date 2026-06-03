package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: String = "",
    val name: String = "",
    val description: String = "",
)