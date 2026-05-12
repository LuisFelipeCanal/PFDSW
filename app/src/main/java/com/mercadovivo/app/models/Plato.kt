package com.mercadovivo.app.models

data class Plato(
    val id: String = "",
    val name: String = "",
    val category: String = "plato",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val photoLabel: String = "",
    val videoLabel: String = ""
)

