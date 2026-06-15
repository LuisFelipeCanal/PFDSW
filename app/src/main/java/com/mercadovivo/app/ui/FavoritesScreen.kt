package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercadovivo.app.models.Huarique

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoriteHuariques: List<Huarique>,
    onOpenDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
    ) {
        // Header (Same as others but without gradient maybe?)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                Text("Mis Favoritos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }

        if (favoriteHuariques.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❤️", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aún no tienes favoritos", color = Color.Gray)
                    Text("¡Explora y guarda los que más te gusten!", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteHuariques) { huarique ->
                    HuariqueCardHome(huarique, onClick = { onOpenDetail(huarique.id) })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
