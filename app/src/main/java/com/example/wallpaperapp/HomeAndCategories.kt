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
    api: UnsplashApiService,
    favourites: List<UnsplashPhoto>,
    onToggleFav: (UnsplashPhoto) -> Unit,
    onSelectPhoto: (UnsplashPhoto) -> Unit
) {
    val tabs = remember { listOf("Trending", "4K Amoled", "Dark", "Minimal", "Nature", "Neon") }
    var selectedTab by remember { mutableStateOf("Trending") }
    var searchQuery by remember { mutableStateOf("") }
    var photos by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedTab) {
        loading = true
        try {
            if (UNSPLASH_API_KEY != "YOUR_UNSPLASH_API_KEY") {
                val query = if (selectedTab == "Trending") "amoled 4k dark wallpaper" else "$selectedTab wallpaper"
                photos = api.searchPhotos(UNSPLASH_API_KEY, query).results
            }
        } catch (_: Exception) {
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
        Text(
            text = "4K Amoled",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ultra crisp dark wallpapers for your display",
            color = TextGray,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search wallpapers...", color = TextGray, fontSize = 14.sp) },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    if (searchQuery.isNotBlank() && UNSPLASH_API_KEY != "YOUR_UNSPLASH_API_KEY") {
                        selectedTab = searchQuery
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonGreen)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = BorderGreen,
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tabs) { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) NeonGreen else CardDark)
                        .border(1.dp, if (isSelected) NeonGreen else BorderGreen, RoundedCornerShape(20.dp))
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        if (loading) {
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
                  
