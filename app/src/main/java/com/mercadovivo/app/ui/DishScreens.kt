package com.mercadovivo.app.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Plato
import com.mercadovivo.app.ui.components.FullScreenImageDialog
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullMenuScreen(
    huarique: Huarique,
    onBack: () -> Unit,
    onDishClick: (Plato) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Platos", "Bebidas", "Postres")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carta - ${huarique.name}", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFFFBF0))) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFE27553),
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (selectedTab == index) Color(0xFFE27553) else Color(0xFFFDEEE9)
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (selectedTab == index) Color.White else Color(0xFFE27553)
                                )
                            }
                        }
                    )
                }
            }

            val currentList = when(selectedTab) {
                0 -> huarique.menuPlates
                1 -> huarique.menuBeverages
                else -> huarique.menuDesserts
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(currentList) { plato ->
                    DishListItem(
                        plato = plato, 
                        onClick = { onDishClick(plato) }
                    )
                }
            }
        }
    }
}

@Composable
fun DishListItem(plato: Plato, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            val photo = if(plato.photoLabel.isNotEmpty()) plato.photoLabel else "https://placeholder.com/400"
            AsyncImage(
                model = photo,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(plato.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Text("${plato.rating}", fontWeight = FontWeight.Bold)
                    }
                }
                Text(plato.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("S/ ${plato.price}", color = Color(0xFFE27553), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DishDetailScreen(
    huarique: Huarique,
    plato: Plato,
    isFavorite: Boolean = false,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onWatchVideo: () -> Unit
) {
    var showFullScreenImage by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFFBF0)).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            val photo = if(plato.photoLabel.isNotEmpty()) plato.photoLabel else "https://placeholder.com/400"
            AsyncImage(
                model = photo,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clickable { showFullScreenImage = true },
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
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text(huarique.name, color = Color.Gray, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(plato.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFFFF7EF), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Text("${plato.rating}", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text("S/ ${plato.price}", color = Color(0xFFE27553), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(plato.description, style = MaterialTheme.typography.bodyMedium)

            if (plato.videoLabel.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onWatchVideo,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE27553)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    val buttonText = when(plato.category.lowercase()) {
                        "bebida" -> "Ver receta de la bebida"
                        "postre" -> "Ver cómo se sirve este dulce"
                        else -> "Ver video de preparación"
                    }
                    Text(buttonText, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Ingredientes visibles", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = plato.ingredients.chunked(2)
                chunks.forEach { rowIngredients ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowIngredients.forEach { ingredient ->
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = Color(0xFFE27553)) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(ingredient.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    if (ingredient.amount.isNotEmpty()) {
                                        Text(ingredient.amount, fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                        if (rowIngredients.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFDEEE9)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tip del Chef", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(plato.chefTip.ifEmpty { "Este plato es mejor disfrutarlo recién preparado para sentir todos sus sabores." }, fontSize = 14.sp)
                }
            }
        }
    }

    if (showFullScreenImage) {
        val photo = if(plato.photoLabel.isNotEmpty()) plato.photoLabel else "https://placeholder.com/400"
        FullScreenImageDialog(imageUrl = photo, onDismiss = { showFullScreenImage = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun VideoPrepScreen(
    huarique: Huarique,
    plato: Plato,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isFullScreen by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isVideoPortrait by remember { mutableStateOf(false) }
    
    val videoUrl = remember(plato.videoLabel) {
        if (plato.videoLabel.contains("dropbox.com")) {
            plato.videoLabel
                .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                .replace("?dl=0", "")
                .replace("&dl=0", "")
                .replace("?dl=1", "")
                .replace("&dl=1", "")
        } else {
            plato.videoLabel
        }
    }

    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            if (videoUrl.isNotEmpty()) {
                val mediaItem = MediaItem.fromUri(videoUrl)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                
                addListener(object : androidx.media3.common.Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        isLoading = (state == androidx.media3.common.Player.STATE_BUFFERING)
                    }
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        hasError = true
                        isLoading = false
                    }
                    override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                        // Detectar si el video es vertical (alto > ancho)
                        isVideoPortrait = videoSize.height > videoSize.width
                    }
                })
            }
        }
    }

    // Manejo de Inmersión y Rotación Inteligente
    LaunchedEffect(isFullScreen) {
        val activity = context as? Activity ?: return@LaunchedEffect
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        
        if (isFullScreen) {
            // Si el video es vertical, mantenemos Portrait. Si es horizontal, forzamos Landscape.
            activity.requestedOrientation = if (isVideoPortrait) 
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT 
            else 
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            val activity = context as? Activity
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            val window = activity?.window ?: return@onDispose
            WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (!isFullScreen) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Video de Preparación", fontSize = 18.sp) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(Color.Black)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Card
                    Card(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(plato.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(huarique.name, color = Color.Gray, fontSize = 14.sp)
                        }
                    }

                    // REPRODUCTOR EN VISTA NORMAL
                    VideoBox(
                        exoPlayer = exoPlayer,
                        videoUrl = videoUrl,
                        isMuted = isMuted,
                        onMuteToggle = { isMuted = !isMuted },
                        onFullScreenToggle = { isFullScreen = true }
                    )

                    // Resto de la información (Sobre este video, RA, Ingredientes...)
                    // ... (resto del contenido que ya tenías)
                    VideoInfoSections(huarique, plato)
                }
            }
        } else {
            // VISTA PANTALLA COMPLETA REAL
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Controles sobre el video en Fullscreen (Movidos a la esquina superior derecha)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { isFullScreen = false },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Fullscreen", tint = Color.White)
                    }
                }
            }
        }
    }
}

@UnstableApi
@Composable
fun VideoBox(
    exoPlayer: ExoPlayer,
    videoUrl: String,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onFullScreenToggle: () -> Unit
) {
    // Inicializamos basándonos en el estado actual del player
    var isLoading by remember { 
        mutableStateOf(exoPlayer.playbackState != androidx.media3.common.Player.STATE_READY) 
    }
    var hasError by remember { mutableStateOf(false) }

    // Escuchar el estado del player
    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                // Solo cargando si está en modo BUFFERING o IDLE (sin preparar)
                isLoading = (state == androidx.media3.common.Player.STATE_BUFFERING || 
                             state == androidx.media3.common.Player.STATE_IDLE)
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                hasError = true
                isLoading = false
            }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(220.dp).background(Color.DarkGray, RoundedCornerShape(16.dp))) {
        if (videoUrl.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Video no disponible", color = Color.Gray)
            }
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp).align(Alignment.Center),
                    color = Color.White
                )
            }

            if (hasError) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
                    Text("Error al cargar video", color = Color.Red, fontSize = 12.sp)
                }
            }
            
            // Controles personalizados (Mute, Fullscreen)
            Row(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onMuteToggle,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Mute",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = onFullScreenToggle,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun VideoInfoSections(huarique: Huarique, plato: Plato) {
    Column {
        // Info Section
        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sobre este video", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("En este video podrás ver el proceso de preparación de ${plato.name}. Observa las técnicas y pasos principales que hacen especial a este plato.", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(color = Color(0xFFFDEEE9), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                        Text("ℹ️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Este video muestra el proceso general. Las cantidades exactas y técnicas específicas son parte del secreto de cada chef.", fontSize = 12.sp)
                    }
                }
            }
        }

        // RA Card
        Card(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚀", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Próximamente: Realidad Aumentada", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Pronto podrás visualizar el proceso de preparación en 3D con Realidad Aumentada. Podrás ver cada paso desde todos los ángulos.", color = Color.Gray, fontSize = 12.sp)
            }
        }

        // Ingredients
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Ingredientes del video", color = Color.Gray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
               Column(modifier = Modifier.padding(16.dp)) {
                   val chunks = plato.ingredients.chunked(2)
                   chunks.forEach { rowIngredients ->
                       Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                           rowIngredients.forEach { ingredient ->
                               Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                   Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = Color(0xFFE27553)) {}
                                   Spacer(modifier = Modifier.width(8.dp))
                                   Column {
                                       Text(ingredient.name, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                                       if (ingredient.amount.isNotEmpty()) {
                                           Text(ingredient.amount, fontSize = 12.sp, color = Color.Gray)
                                       }
                                   }
                               }
                           }
                           if (rowIngredients.size == 1) Spacer(modifier = Modifier.weight(1f))
                       }
                   }
               }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
