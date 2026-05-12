package com.mercadovivo.app.data

import com.mercadovivo.app.models.Huarique

// Repositorio mock para huariques con ubicaciones útiles para la demo en Miraflores.
class HuariqueRepository {

    private val huariques = listOf(
        Huarique(
            id = "1",
            name = "Huarique El Buen Sabor",
            description = "Ceviches frescos, menú criollo y atención familiar con ambiente tradicional.",
            address = "Av. Benavides 1234, Miraflores, Lima",
            district = "Miraflores",
            lat = -12.1208,
            lng = -77.0307,
            rating = 4.8,
            categories = listOf("ceviche", "mariscos", "criolla"),
            horario = "08:00 - 22:00",
            phone = "+51 987 654 321",
            email = "contacto@buensabor.pe",
            photos = listOf("foto1", "foto2"),
            featuredPlates = listOf("Ceviche clásico", "Arroz con mariscos", "Leche de tigre"),
            featuredBeverages = listOf("Chicha morada", "Maracuyá natural"),
            featuredDesserts = listOf("Suspiro a la limeña"),
            isVerified = true
        ),
        Huarique(
            id = "2",
            name = "La Esquina del Anticucho",
            description = "Anticuchos tradicionales, papas doradas y salsas caseras en una esquina clásica de Miraflores.",
            address = "Jr. Domeyer 255, Miraflores, Lima",
            district = "Miraflores",
            lat = -12.1221,
            lng = -77.0279,
            rating = 4.6,
            categories = listOf("anticuchos", "criolla"),
            horario = "12:00 - 23:00",
            phone = "+51 966 555 444",
            email = "hola@anticuchosmiraflores.pe",
            photos = listOf("foto3", "foto4"),
            featuredPlates = listOf("Anticuchos de corazón", "Papas con huancaína"),
            featuredBeverages = listOf("Emoliente", "Inca Kola"),
            featuredDesserts = listOf("Mazamorra morada"),
            isVerified = true
        ),
        Huarique(
            id = "3",
            name = "Sazón de Barrio",
            description = "Sopas, segundos y postres hechos al estilo casero para almuerzos abundantes.",
            address = "Calle Schell 588, Miraflores, Lima",
            district = "Miraflores",
            lat = -12.1193,
            lng = -77.0294,
            rating = 4.5,
            categories = listOf("sopas", "criolla", "menú"),
            horario = "07:30 - 20:30",
            phone = "+51 944 222 111",
            email = "reservas@sazondebarrio.pe",
            photos = listOf("foto5"),
            featuredPlates = listOf("Sopa criolla", "Lomo saltado"),
            featuredBeverages = listOf("Té de hierbas"),
            featuredDesserts = listOf("Arroz con leche"),
            isVerified = true
        )
    )

    fun getHuariques(): List<Huarique> = huariques

    fun findById(id: String): Huarique? = huariques.firstOrNull { it.id == id }
}
