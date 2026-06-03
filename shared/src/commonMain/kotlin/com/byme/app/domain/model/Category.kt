package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String = "",
    val name: String = ""
)