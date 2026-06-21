package com.mercadovivo.app.models

data class UserData(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phone: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    val lastNotificationsReadAt: Long = 0,
    val readNotificationIds: List<String> = emptyList(),
    val pushNotificationsEnabled: Boolean = true,
    val nearHuariqueNotificationsEnabled: Boolean = true,
    val favorites: List<String> = emptyList(),
    val favoriteDishes: List<String> = emptyList(),
    val visitedHuariques: List<String> = emptyList(),
    val role: String = "USER"
)
