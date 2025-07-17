package com.example.labexercise7

import kotlinx.serialization.Serializable

@Serializable
data class Record(
    val id: Int = 0,
    val name: String,
    val email: String
)
