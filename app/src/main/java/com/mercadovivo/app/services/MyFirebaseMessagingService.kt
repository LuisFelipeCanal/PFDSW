package com.mercadovivo.app.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mercadovivo.app.utils.NotificationHelper

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            NotificationHelper.showNotification(
                this,
                it.title ?: "MercadoVivo",
                it.body ?: ""
            )
        }
    }

    override fun onNewToken(token: String) {
        // Enviar token al servidor/Firestore si es necesario para enviar notificaciones personalizadas
    }
}
