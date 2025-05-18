package com.example.hocky

data class Player(
    val id: Int = 0,
    val name: String,
    val dob: String,
    val jerseyNumber: String,
    val contact: String,
    val email: String?,
    val team: String,
    val gender: String,
    val position: String
)