package com.mercadovivo.app.models

data class Huarique(
    val id: String = "",
    // --- Datos del Local ---
    val name: String = "",
    val description: String = "",
    val businessType: String = "",   // Tipo de negocio
    val branchesCount: Int = 1,      // Sucursales
    @get:com.google.firebase.firestore.PropertyName("isStreetFront")
    @set:com.google.firebase.firestore.PropertyName("isStreetFront")
    var isStreetFront: Boolean = true, // ¿Es un local a la calle?
    val address: String = "",
    val district: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val rating: Double? = 0.0,
    val categories: List<String> = emptyList(),
    val suggestedCategory: String = "", // Sugerencia del usuario
    val horario: String? = null,
    
    // --- Datos del Propietario/Registro ---
    val ownerFirstName: String = "",
    val ownerLastName: String = "",
    val ownerPhone: String = "",     // Teléfono personal
    val phone: String = "",          // Teléfono del local
    val email: String = "",
    
    // --- Multimedia y Otros ---
    val photos: List<String> = emptyList(),
    val menuPlates: List<Plato> = emptyList(),
    val menuBeverages: List<Plato> = emptyList(),
    val menuDesserts: List<Plato> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val videoPath: String? = null,   // URL Dropbox
    @get:com.google.firebase.firestore.PropertyName("isVerified")
    @set:com.google.firebase.firestore.PropertyName("isVerified")
    var isVerified: Boolean = false,
    val createdAt: Long = 946684800000L, // Por defecto año 2000 para locales antiguos
    val submittedBy: String = ""     // UID del usuario que lo registró
)

data class Ingredient(
    val name: String = "",
    val amount: String = "",
    val notes: String = ""
)
