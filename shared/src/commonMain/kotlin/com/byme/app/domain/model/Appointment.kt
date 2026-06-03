package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Appointment (
    val id: String = "",
    val userId: String = "",
    val professionalId: String = "",
    val professionalName: String = "",
    val serviceName: String = "",
    val date: Long = 0L,
    val status: String = "pending",
    val notes: String = "",
)