package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.ui.components.CategoryPill
import com.mercadovivo.app.ui.components.InfoRow
import com.mercadovivo.app.ui.components.MarketTopBar
import com.mercadovivo.app.ui.components.SectionHeader

@Composable
fun HuariqueDetailScreen(
    huarique: Huarique,
    onBack: () -> Unit = {},
    onOpenMap: (String) -> Unit = {}
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Menú", "Reseñas", "Videos")

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        MarketTopBar(
            title = huarique.name,
            subtitle = "Detalle del huarique · ${huarique.district}",
            onBack = onBack,
            trailingLabel = "Mapa",
            onTrailingClick = { onOpenMap(huarique.id) }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (huarique.photos.isNotEmpty()) "Galería de imágenes (mock)" else "Sin imágenes todavía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = huarique.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "★ ${huarique.rating ?: 0.0} · ${if (huarique.isVerified) "Verificado" else "Sin verificar"}")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                SectionHeader(title = "Información")
                InfoRow(label = "Dirección", value = huarique.address)
                InfoRow(label = "Horario", value = huarique.horario ?: "Por confirmar")
                InfoRow(label = "Teléfono", value = huarique.phone ?: "No disponible")
                InfoRow(label = "Correo", value = huarique.email ?: "No disponible")

                Spacer(modifier = Modifier.height(12.dp))
                SectionHeader(title = "Categorías")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(huarique.categories) { category ->
                        CategoryPill(label = category)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { onOpenMap(huarique.id) }) {
                    Text("Ver en mapa")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, label ->
                Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(label) })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        when (tabIndex) {
            0 -> MenuTab(huarique)
            1 -> ReviewsTab(huarique)
            else -> VideosTab(huarique)
        }
    }
}

@Composable
private fun MenuTab(huarique: Huarique) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Platos destacados")
        if (huarique.featuredPlates.isEmpty()) {
            Text("Todavía no hay platos cargados.")
        } else {
            huarique.featuredPlates.forEach { item ->
                DetailItemCard(item, "Plato")
            }
        }

        SectionHeader(title = "Bebidas")
        if (huarique.featuredBeverages.isEmpty()) {
            Text("Todavía no hay bebidas cargadas.")
        } else {
            huarique.featuredBeverages.forEach { item ->
                DetailItemCard(item, "Bebida")
            }
        }

        SectionHeader(title = "Postres")
        if (huarique.featuredDesserts.isEmpty()) {
            Text("Todavía no hay postres cargados.")
        } else {
            huarique.featuredDesserts.forEach { item ->
                DetailItemCard(item, "Postre")
            }
        }
    }
}

@Composable
private fun ReviewsTab(huarique: Huarique) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Reseñas recientes")
        listOf(
            "María: Excelente atención y ceviche fresco.",
            "José: Ambiente acogedor y buena relación calidad/precio.",
            "Ana: Quiero volver por los postres caseros."
        ).forEach { review ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(text = review, modifier = Modifier.padding(16.dp))
            }
        }
        Button(onClick = { /* futuro */ }) {
            Text("Agregar reseña")
        }
    }
}

@Composable
private fun VideosTab(huarique: Huarique) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Video de preparación")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Video demostrativo (mock)", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Aquí irá el reproductor de video en la siguiente fase del proyecto.")
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { /* futuro */ }) {
                    Text("Ver video")
                }
            }
        }
    }
}

@Composable
private fun DetailItemCard(title: String, type: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7EF))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = type, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
