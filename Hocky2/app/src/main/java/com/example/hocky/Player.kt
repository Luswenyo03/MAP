package com.example.hocky

data class Player(
    val id: Long = 0,
    val name: String,
    val position: String,
    val team: String,
    val jerseyNumber: Int,
    val dob: String? = null,
    val contact: String? = null,
    val email: String? = null,
    val gender: String? = null
)