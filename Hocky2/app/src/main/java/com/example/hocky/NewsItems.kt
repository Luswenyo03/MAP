package com.example.hocky

data class NewsItem(
    val id: String = "",        // Firestore document ID
    val title: String = "",     // News title
    val content: String = "",   // News content/body
    val timestamp: Long = 0L    // When it was posted (milliseconds)
)
