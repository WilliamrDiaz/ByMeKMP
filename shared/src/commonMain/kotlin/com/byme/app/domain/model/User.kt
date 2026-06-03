package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val isProfessional: Boolean = false,
    val role: String = "user",
    val createdAt: Long = 0L, // En KMP no usamos System.currentTimeMillis() como default aquí
    val category: String = "",
    val description: String = "",
    val experience: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val available: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)