package com.example.wallpaperapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun CategoriesScreen(api: UnsplashApiService, onSelectPhoto: (UnsplashPhoto) -> Unit) {
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

    LaunchedEffect(selectedCat) {
        selectedCat?.let { c ->
            loading = true
            try {
                if (UNSPLASH_API_KEY != "YOUR_UNSPLASH_API_KEY") {
                    catPhotos = api.searchPhotos(UNSPLASH_API_KEY, c.query).results
                }
            } catch (_: Exception) {}
            loading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        if (selectedCat != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { selectedCat = null }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Text(selectedCat!!.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = NeonGreen) }
            } else {
                LazyVerticalGrid(GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(catPhotos) { ph -> WallpaperGridItem(ph, false, {}, { onSelectPhoto(ph) }) }
                }
            }
        } else {
            Text("Categories", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Discover something made for your screen", color = TextGray, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(cats) { cat ->
                    Box(
                        Modifier.height(150.dp).clip(RoundedCornerShape(18.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF1F3310), CardDark)))
                            .border(1.dp, BorderGreen, RoundedCornerShape(18.dp))
                            .clickable { selectedCat = cat }
                    ) {
                        Text(cat.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
                    }
                }
            }
        }
    }
}
