package com.mercadovivo.app.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun isStoreOpen(horario: String?): Boolean {
        if (horario.isNullOrBlank()) return false

        return try {
            // Normalizamos el formato: pasamos a mayúsculas y quitamos espacios extra
            val normalized = horario.uppercase().replace(" ", "")
            
            // Intentamos separar por el guión
            val parts = normalized.split("-")
            if (parts.size != 2) return false

            val sdf = SimpleDateFormat("hh:mma", Locale.US)
            
            // Hora actual
            val nowCalendar = Calendar.getInstance()
            val currentTime = sdf.parse(sdf.format(nowCalendar.time)) ?: return false

            // Horas del local
            val startTime = sdf.parse(parts[0]) ?: return false
            val endTime = sdf.parse(parts[1]) ?: return false

            if (endTime.before(startTime)) {
                // Caso nocturno: 07:00PM - 03:00AM
                currentTime.after(startTime) || currentTime.before(endTime)
            } else {
                // Caso normal: 08:00AM - 06:00PM
                currentTime.after(startTime) && currentTime.before(endTime)
            }
        } catch (e: Exception) {
            false
        }
    }
}
