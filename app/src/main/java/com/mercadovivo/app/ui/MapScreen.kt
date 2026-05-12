package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.ui.components.HuariqueCard
import com.mercadovivo.app.ui.components.MarketTopBar
import com.mercadovivo.app.ui.components.SectionHeader

@Composable
fun MapScreen(
    huariques: List<Huarique>,
    selectedId: String = "all",
    onOpenDetail: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val selected = huariques.firstOrNull { it.id == selectedId } ?: huariques.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MarketTopBar(
                title = "Mapa de huariques",
                subtitle = "Vista demo centrada en Miraflores",
                onBack = onBack
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Mapa mock de referencia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFAE7D4), Color(0xFFF9B36D), Color(0xFFD2423B).copy(alpha = 0.55f))
                                )
                            )
                    ) {
                        Box(modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                            MarkerBubble(label = "Miraflores")
                        }
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            MarkerBubble(label = selected?.name ?: "Selecciona un huarique", selected = true)
                        }
                        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
                            MarkerBubble(label = "Parque Kennedy")
                        }
                        Box(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                            MarkerBubble(label = "Benavides")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = selected?.let { "Seleccionado: ${it.name} · ${it.address}" } ?: "Sin selección",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { selected?.let { onOpenDetail(it.id) } ?: onOpenDetail(huariques.firstOrNull()?.id ?: "") }) {
                        Text("Ver detalle del huarique")
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Huariques cercanos")
        }

        items(huariques) { huarique ->
            HuariqueCard(
                huarique = huarique,
                onClick = { onOpenDetail(huarique.id) },
                selected = huarique.id == selectedId
            )
        }
    }
}

@Composable
private fun MarkerBubble(label: String, selected: Boolean = false) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.92f),
        shape = RoundedCornerShape(999.dp),
        tonalElevation = if (selected) 6.dp else 2.dp,
        shadowElevation = if (selected) 4.dp else 1.dp,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) Color.White else MaterialTheme.colorScheme.primary)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) Color.White else MaterialTheme.colorScheme.primary
            )
        }
    }
}

