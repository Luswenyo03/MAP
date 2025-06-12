package com.example.myhockey

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val idNumber: String = "",
    val phone: String = "",
    val gender: String = "",
    val role: String = "coach",
    val imageUrl: String? = null,
    val approved: Boolean = false,
    val adminMessage: String? = null
)







