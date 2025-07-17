package com.example.labexercise7

import kotlinx.serialization.Serializable

@Serializable
data class AddRecordRequest(
    val name: String,
    val email: String
)