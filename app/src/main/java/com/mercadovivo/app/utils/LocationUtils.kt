package com.mercadovivo.app.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

object LocationUtils {
    /**
     * Calcula la distancia en metros entre dos puntos
     */
    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0]
    }

    /**
     * Formatea la distancia para mostrarla en la UI
     */
    fun formatDistance(meters: Float): String {
        return if (meters < 1000) {
            "${meters.toInt()} m"
        } else {
            String.format("%.1f km", meters / 1000f)
        }
    }
}
