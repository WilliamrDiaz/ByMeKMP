package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String = "",
    val professionalId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val createdAt: Long = 0L,
)