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
    val menuPlates: List<Plato> = emptyList(),
    val menuBeverages: List<Plato> = emptyList(),
    val menuDesserts: List<Plato> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val videoPath: String? = null,
    val audioPath: String? = null,
    val isVerified: Boolean = false
)

data class Ingredient(
    val name: String = "",
    val amount: String = "",
    val notes: String = ""
)
