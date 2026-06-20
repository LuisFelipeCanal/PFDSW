package com.mercadovivo.app.models

data class Plato(
    val id: String = "",
    val name: String = "",
    val category: String = "plato",
    val description: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val photoLabel: String = "",
    val videoLabel: String = "", // Link Dropbox Video
    val audioLabel: String = "", // Link Dropbox Audio
    val chefTip: String = ""
)
