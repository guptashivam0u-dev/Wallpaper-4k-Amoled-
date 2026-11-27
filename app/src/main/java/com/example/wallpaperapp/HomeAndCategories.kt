package com.example.wallpaperapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    favCount: Int,
    recentCount: Int,
    favourites: List<UnsplashPhoto>,
    onToggleFav: (UnsplashPhoto) -> Unit,
    onSelectPhoto: (UnsplashPhoto) -> Unit
) {
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 6
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
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
                state = gridState,
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

                if (isLoadingMore) {
                    item(span = { GridItemSpan(3) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = NeonGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
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
            CategoryItem("Abstract", "abstract 3d dark", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQNVhebh_gt6ScAN-kRtYkaw4iY8KSRc4njDPkkk7Zy8y97m_USSFjZcSgpY_Yz9zDd-Mw-aDyyHFmlQTc2CgojO_20261127_004509.jpg"),
            CategoryItem("AI Generated", "digital art neon", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQNWeg0fMMHfGXUnzX_2OuGHo158RoL7nKcYASNzlRml1iLQZ7Ir6Cr-sAOhCueEaROWh_AOZydcb7M51bdiC8Nj_20261127_004525.jpg"),
            CategoryItem("Amoled", "amoled black dark", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQODc_7ldwHaNtUy-XHNVl6PjpsPh0LriMYPs-e2UiIEv2yf8C_iIx_O-pqkYTUwABths4nb5MRTTdpkYmB8i9po_20261127_004706.jpg"),
            CategoryItem("Animals", "wild animal dark", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQP409Ca3dkKb9IC_SjaG2oyxrUR1TBHrpXSezYdpIw4Q-1CmX6dqxf6j19jYpo2X2pwdzBks6lOLfPwGhTOWNqz_20261127_004432.jpg"),
            CategoryItem("Anime", "anime aesthetic", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQN3EKAY7Uc04sCn-FP_ZiNy1rGaiLlBFPC6KHEM0YV7TDH1JE7Dunt2p091EU1m9XwZrpqOnsCfJtzrux4Bl4uI_20261127_004831.jpg"),
            CategoryItem("Cars", "supercar dark", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQN2JUcXJ5oSpA5VB9L4IyUsMZObblnIJPKaW0oZ6DizQRczujwtWkbOKAJOOqQyaxQAU-fBPPhQQQWTNMpcuYDb_20261127_004632.jpg"),
            CategoryItem("Flowers", "flowers aesthetic", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQNkkJ-jgJLP9cd57fTZcf3x1H-IoqZW0FUKcZJyuyGbTsmrvSgcQBxEmYPyJmv8gJMoJmYwK4IBs1agI1oi8ymp_20261127_004420.jpg"),
            CategoryItem("Food", "aesthetic food drink", "https://raw.githubusercontent.com/guptashivam0u-dev/Wallpaper-4k-Amoled-/refs/heads/main/Meta_AI_AQONkVTW0EUXuGPmjbroHLJo3LvvjU9UiaA072PIgk3F4yQgNkMU2EbOHDN4oYSXms0T6Gs4I-qYObjPzSBoxKC__20261127_004621.jpg")
        )
    }
    var selectedCat by remember { mutableStateOf<CategoryItem?>(null) }
    var catPhotos by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var loadingMore by remember { mutableStateOf(false) }
    var catPage by remember { mutableStateOf(1) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val catGridState = rememberLazyGridState()

    val shouldLoadMoreCat by remember {
        derivedStateOf {
            val totalItems = catGridState.layoutInfo.totalItemsCount
            val lastVisible = catGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 6
        }
    }

    LaunchedEffect(selectedCat) {
        val currentCat = selectedCat ?: return@LaunchedEffect
        loading = true
        errorMessage = null
        catPage = 1
        try {
            if (UNSPLASH_API_KEY != "YOUR_UNSPLASH_API_KEY" && UNSPLASH_API_KEY.isNotBlank()) {
                catPhotos = api.searchPhotos(UNSPLASH_API_KEY, currentCat.query, page = 1, perPage = 30).results
            } else {
                errorMessage = "Missing Unsplash API Key"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load wallpapers."
        } finally {
            loading = false
        }
    }

    LaunchedEffect(shouldLoadMoreCat) {
        val currentCat = selectedCat ?: return@LaunchedEffect
        if (shouldLoadMoreCat && !loading && !loadingMore) {
            loadingMore = true
            try {
                val nextPage = catPage + 1
                val moreResults = api.searchPhotos(UNSPLASH_API_KEY, currentCat.query, page = nextPage, perPage = 30).results
                if (moreResults.isNotEmpty()) {
                    val existingIds = catPhotos.map { it.id }.toSet()
                    val uniqueNew = moreResults.filter { it.id !in existingIds }
                    catPhotos = catPhotos + uniqueNew
                    catPage = nextPage
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loadingMore = false
            }
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
                else -> {
                    LazyVerticalGrid(
                        state = catGridState,
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(catPhotos, key = { it.id }) { ph ->
                            WallpaperGridItem(photo = ph, isFav = false, onToggleFav = {}, onClick = { onSelectPhoto(ph) })
                        }
                        if (loadingMore) {
                            item(span = { GridItemSpan(3) }) {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = NeonGreen, modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text(text = "Categories", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(text = "Discover something made for your screen", color = TextGray, fontSize = 13.sp)
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
                            .background(CardDark)
                            .border(1.dp, BorderGreen, RoundedCornerShape(18.dp))
                            .clickable { selectedCat = cat }
                    ) {
                        AsyncImage(
                            model = cat.imageUrl,
                            contentDescription = cat.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                    )
                                )
                        )
                        Text(
                            text = cat.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
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
        modifier = Modifier.fillMaxSize().background(BgBlack).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "4K Amoled", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(text = "Ultra crisp dark wallpapers for your screen", color = TextGray, fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onOpenWallpapers,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(text = "Explore Wallpapers", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
