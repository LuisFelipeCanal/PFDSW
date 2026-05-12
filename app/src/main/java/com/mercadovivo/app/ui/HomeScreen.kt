package com.mercadovivo.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mercadovivo.app.data.HuariqueRepository
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.ui.components.HuariqueCard
import com.mercadovivo.app.ui.components.MarketTopBar
import com.mercadovivo.app.ui.components.SectionHeader

@Composable
fun HomeScreen(
    huariques: List<Huarique> = HuariqueRepository().getHuariques(),
    onOpenDetail: (String) -> Unit = {},
    onOpenMap: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MarketTopBar(
                title = "MercadoVivo",
                subtitle = "Descubre huariques y sabores reales en Miraflores",
                trailingLabel = "Mapa",
                onTrailingClick = { onOpenMap(huariques.firstOrNull()?.id ?: "all") }
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Explora tu próxima parada gastronómica", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "La app concentra huariques, platos, videos y reseñas en una sola experiencia.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { onOpenMap(huariques.firstOrNull()?.id ?: "all") }) {
                        Text("Abrir mapa de huariques")
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Huariques destacados")
        }

        items(huariques) { huarique ->
            HuariqueCard(
                huarique = huarique,
                onClick = { onOpenDetail(huarique.id) }
            )
        }
    }
}
