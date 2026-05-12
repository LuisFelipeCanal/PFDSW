package com.mercadovivo.app.models

data class Huarique(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val district: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val rating: Double? = null,
    val categories: List<String> = emptyList(),
    val horario: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val photos: List<String> = emptyList(),
    val featuredPlates: List<String> = emptyList(),
    val featuredBeverages: List<String> = emptyList(),
    val featuredDesserts: List<String> = emptyList(),
    val videoPath: String? = null,
    val isVerified: Boolean = false
)

