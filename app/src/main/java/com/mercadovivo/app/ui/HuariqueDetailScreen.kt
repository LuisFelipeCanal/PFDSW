package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Plato
import com.mercadovivo.app.models.Review
import com.mercadovivo.app.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HuariqueDetailScreen(
    huarique: Huarique,
    isFavorite: Boolean = false,
    authViewModel: com.mercadovivo.app.auth.AuthViewModel? = null,
    onBack: () -> Unit,
    onOpenMap: (String) -> Unit,
    onSeeAllMenu: () -> Unit,
    onDishClick: (Plato) -> Unit,
    onToggleFavorite: () -> Unit,
    onAddReview: (Review) -> Unit = {},
    onDeleteReview: (String) -> Unit = {}
) {
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewToEdit by remember { mutableStateOf<Review?>(null) }
    var reviewText by remember { mutableStateOf("") }
    var reviewRating by remember { mutableStateOf(5f) }
    
    val currentUser = authViewModel?.currentUser
    val userData = authViewModel?.userData
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    // Al abrir el diálogo para editar, cargamos los datos previos
    LaunchedEffect(showReviewDialog) {
        if (showReviewDialog && reviewToEdit != null) {
            reviewText = reviewToEdit!!.comment
            reviewRating = reviewToEdit!!.rating.toFloat()
        } else if (!showReviewDialog) {
            reviewToEdit = null
            reviewText = ""
            reviewRating = 5f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image
        Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
            val mainPhoto = if (huarique.photos.isNotEmpty()) huarique.photos.first() else "https://placeholder.com/600"
            AsyncImage(
                model = mainPhoto,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clickable { selectedImageUrl = mainPhoto },
                contentScale = ContentScale.Crop
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Row {
                    IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggleFavorite, 
                        modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                            contentDescription = null,
                            tint = if (isFavorite) Color.Red else Color.Black
                        )
                    }
                }
            }

            val isOpen = com.mercadovivo.app.utils.TimeUtils.isStoreOpen(huarique.horario)
            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                color = if (isOpen) Color(0xFF4CAF50) else Color.Gray,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    if (isOpen) "Abierto ahora" else "Cerrado", 
                    color = Color.White, 
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
                    fontSize = 12.sp
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(huarique.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFFDEEE9), shape = RoundedCornerShape(20.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        val ratingValue = huarique.rating ?: 0.0
                        Text(if(ratingValue > 0) ratingValue.toString() else "0.0", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            
            if (huarique.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    huarique.categories.forEach { category ->
                        CategoryPill(label = category)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(huarique.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))
            InfoRow(label = "📍 Dirección", value = huarique.address)
            InfoRow(label = "🕒 Horario", value = huarique.horario ?: "Horario no disponible")
            InfoRow(label = "📞 Teléfono", value = huarique.phone)

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onOpenMap(huarique.id) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE27553)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ver en mapa", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Carta Digital", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            val categories = listOf(
                "Platos principales" to huarique.menuPlates,
                "Bebidas y refrescos" to huarique.menuBeverages,
                "Postres y dulces" to huarique.menuDesserts
            )

            categories.forEach { (title, items) ->
                if (items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(title, color = Color.Gray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(items) { item ->
                            PlatoMiniCard(item, onClick = { onDishClick(item) })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Ver carta completa →", 
                color = Color(0xFFE27553), 
                fontSize = 14.sp,
                modifier = Modifier.clickable { onSeeAllMenu() }.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Reseñas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showReviewDialog = true }) {
                    Text("Escribir reseña", color = Color(0xFFE27553))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (huarique.reviews.isEmpty()) {
                Text("Aún no hay reseñas. ¡Sé el primero en comentar!", color = Color.Gray)
            } else {
                huarique.reviews.forEach { review ->
                    ReviewCard(
                        review = review,
                        currentUserId = authViewModel?.userId ?: "",
                        onEdit = { 
                            reviewToEdit = review
                            showReviewDialog = true 
                        },
                        onDelete = { onDeleteReview(review.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    selectedImageUrl?.let { url ->
        FullScreenImageDialog(imageUrl = url, onDismiss = { selectedImageUrl = null })
    }

    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text(if (reviewToEdit != null) "Editar Reseña" else "Escribir Reseña") },
            text = {
                Column {
                    Slider(
                        value = reviewRating,
                        onValueChange = { reviewRating = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Text("Puntuación: ${reviewRating.toInt()} ⭐")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Tu comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newReview = Review(
                        id = reviewToEdit?.id ?: UUID.randomUUID().toString(),
                        userId = authViewModel?.userId ?: "",
                        userName = userData?.displayName ?: currentUser?.displayName ?: "Anónimo",
                        rating = reviewRating.toDouble(),
                        comment = reviewText,
                        userPhotoLabel = userData?.photoUrl ?: "",
                        dateLabel = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    )
                    onAddReview(newReview)
                    showReviewDialog = false
                }) {
                    Text(if (reviewToEdit != null) "Actualizar" else "Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
