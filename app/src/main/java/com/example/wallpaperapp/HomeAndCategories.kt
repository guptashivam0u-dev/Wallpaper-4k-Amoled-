package com.example.wallpaperapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    photos: List<UnsplashPhoto>,
    isLoading: Boolean,
    favCount: Int,
    recentCount: Int,
    favourites: List<UnsplashPhoto>,
    onToggleFav: (UnsplashPhoto) -> Unit,
    onSelectPhoto: (UnsplashPhoto) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "4K Amoled",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Saved: $favCount  •  Applied: $recentCount",
            color = TextGray,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonGreen)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(photos, key = { it.id }) { ph ->
                    val isFav = favourites.any { it.id == ph.id }
                    WallpaperGridItem(
                        photo = ph,
                        isFav = isFav,
                        onToggleFav = { onToggleFav(ph) },
                        onClick = { onSelectPhoto(ph) }
                    )
                }
            }
        }
    }
}

@Composable
fun WallpaperGridItem(
    photo: UnsplashPhoto,
    isFav: Boolean,
    onToggleFav: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.58f)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = photo.urls.small,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onToggleFav() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFav) NeonGreen else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CategoriesScreen(
    api: UnsplashApiService,
    onSelectPhoto: (UnsplashPhoto) -> Unit
) {
    val cats = remember {
        listOf(
            CategoryItem("Abstract", "abstract 3d dark", ""),
            CategoryItem("AI Generated", "digital art neon", ""),
            CategoryItem("Amoled", "amoled black dark", ""),
            CategoryItem("Animals", "wild animal dark", ""),
            CategoryItem("Anime", "anime aesthetic", ""),
            CategoryItem("Cars", "supercar dark", ""),
            CategoryItem("Flowers", "flowers aesthetic", ""),
            CategoryItem("Food", "aesthetic food drink", "")
        )
    }

    var selectedCat by remember { mutableStateOf<CategoryItem?>(null) }
    var catPhotos by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedCat) {
        val currentCat = selectedCat ?: return@LaunchedEffect
        loading = true
        errorMessage = null
        try {
            if (UNSPLASH_API_KEY != "YOUR_UNSPLASH_API_KEY") {
                catPhotos = api.searchPhotos(UNSPLASH_API_KEY, currentCat.query).results
            } else {
                errorMessage = "Missing Unsplash API Key"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load wallpapers."
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        if (selectedCat != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    selectedCat = null
                    catPhotos = emptyList()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = selectedCat!!.name,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))

            when {
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonGreen)
                    }
                }
                errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage!!, color = TextGray, fontSize = 14.sp)
                    }
                }
                catPhotos.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No wallpapers found.", color = TextGray, fontSize = 14.sp)
                    }
                }
                catPhotos.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No wallpapers found.", color = TextGray, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(catPhotos, key = { it.id }) { ph ->
                            WallpaperGridItem(
                                photo = ph,
                                isFav = false,
                                onToggleFav = {},
                                onClick = { onSelectPhoto(ph) }
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Categories",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Discover something made for your screen",
                color = TextGray,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(cats, key = { it.name }) { cat ->
                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF1F3310), CardDark)))
                            .border(1.dp, BorderGreen, RoundedCornerShape(18.dp))
                            .clickable { selectedCat = cat }
                    ) {
                        Text(
                            text = cat.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onOpenWallpapers: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "4K Amoled",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Ultra crisp dark wallpapers for your screen",
                color = TextGray,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onOpenWallpapers,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Explore Wallpapers",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
