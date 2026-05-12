package com.mercadovivo.app.models

data class Review(
    val id: String = "",
    val userName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val dateLabel: String = "",
    val userPhotoLabel: String = ""
)

