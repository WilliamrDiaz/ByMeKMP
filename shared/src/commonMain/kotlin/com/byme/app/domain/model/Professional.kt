package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Professional(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val phone: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val imageUrl: String = "", // En Android se llamaba imageUrl, en User photoUrl.
    val available: Boolean = true,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)