package com.example.wallpaperapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val BgBlack = Color(0xFF050805)
val CardDark = Color(0xFF101510)
val NeonGreen = Color(0xFF8CE62E)
val BorderGreen = Color(0xFF284214)
val TextGray = Color(0xFF9EA69E)

const val UNSPLASH_API_KEY = "j6I6E53KGj8Wbeq7A17r195_-iTyO8ypaSQEfh-KTcM"

fun loadSavedList(ctx: Context, key: String): List<UnsplashPhoto> {
    val prefs = ctx.getSharedPreferences("wall_prefs", Context.MODE_PRIVATE)
    val raw = prefs.getString(key, "") ?: ""
    if (raw.isBlank()) return emptyList()
    return raw.split(";;").mapNotNull { item ->
        val p = item.split("|")
        if (p.size >= 4) UnsplashPhoto(p[0], PhotoUrls(p[1], p[2], p[3])) else null
    }.distinctBy { it.id }
}

fun savePhotoList(ctx: Context, key: String, list: List<UnsplashPhoto>) {
    val prefs = ctx.getSharedPreferences("wall_prefs", Context.MODE_PRIVATE)
    val encoded = list.distinctBy { it.id }.joinToString(";;") {
        "${it.id}|${it.urls.regular}|${it.urls.full}|${it.urls.small}"
    }
    prefs.edit().putString(key, encoded).apply()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(background = BgBlack, surface = CardDark, primary = NeonGreen)
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = BgBlack) {
                    WallpaperAppRoot()
                }
            }
        }
    }
}

@Composable
fun WallpaperAppRoot() {
    val ctx = LocalContext.current
    var showWelcome by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(1) }
    var previewPhoto by remember { mutableStateOf<UnsplashPhoto?>(null) }

    val favourites = remember { mutableStateListOf<UnsplashPhoto>().apply { addAll(loadSavedList(ctx, "favs")) } }
    val recentlyApplied = remember { mutableStateListOf<UnsplashPhoto>().apply { addAll(loadSavedList(ctx, "recents")) } }

    var homePhotos by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = previewPhoto != null || (!showWelcome && selectedTab != 1)) {
        if (previewPhoto != null) previewPhoto = null
        else if (selectedTab != 1) selectedTab = 1
    }

    // 1. पहले api को सही URL के साथ बनाएं
    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnsplashApiService::class.java)
    }

    // 2. उसके बाद ही LaunchedEffect में api का उपयोग करें
    LaunchedEffect(Unit) {
        try {
            if (UNSPLASH_API_KEY != "YOUR_UNSPLASH_API_KEY") {
                homePhotos = api.getPhotos(UNSPLASH_API_KEY, page = 1, perPage = 30)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    fun loadMoreHomePhotos() {
        if (isLoadingMore || isLoading) return
        isLoadingMore = true
        coroutineScope.launch {
            try {
                val nextPage = currentPage + 1
                val newPhotos = api.getPhotos(UNSPLASH_API_KEY, page = nextPage, perPage = 30)
                if (newPhotos.isNotEmpty()) {
                    val existingIds = homePhotos.map { it.id }.toSet()
                    homePhotos = homePhotos + newPhotos.filter { it.id !in existingIds }
                    currentPage = nextPage
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun toggleFavPhoto(photo: UnsplashPhoto) {
        if (favourites.any { it.id == photo.id }) {
            favourites.removeAll { it.id == photo.id }
        } else {
            favourites.add(0, photo)
        }
        savePhotoList(ctx, "favs", favourites)
    }

    if (showWelcome) {
        WelcomeScreen(onOpenWallpapers = { showWelcome = false })
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = BgBlack,
                bottomBar = { FloatingBottomNav(selectedTab = selectedTab, onSelectTab = { selectedTab = it }) }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (selectedTab) {
                        0 -> CategoriesScreen(api = api, onSelectPhoto = { previewPhoto = it })
                        1 -> HomeScreen(
                            photos = homePhotos,
                            isLoading = isLoading,
                            isLoadingMore = isLoadingMore,
                            onLoadMore = { loadMoreHomePhotos() },
                            favCount = favourites.size,
                            recentCount = recentlyApplied.size,
                            favourites = favourites,
                            onToggleFav = { toggleFavPhoto(it) },
                            onSelectPhoto = { previewPhoto = it }
                        )
                        2 -> OfflineScreen(onSelectPhoto = { previewPhoto = it })
                        3 -> MoreScreen(
                            favourites = favourites,
                            recentlyApplied = recentlyApplied,
                            onSelectPhoto = { previewPhoto = it }
                        )
                    }
                }
            }

            if (previewPhoto != null) {
                WallpaperPreviewScreen(
                    photo = previewPhoto!!,
                    isFav = favourites.any { it.id == previewPhoto!!.id },
                    onToggleFav = { toggleFavPhoto(previewPhoto!!) },
                    onApplied = {
                        recentlyApplied.removeAll { it.id == previewPhoto!!.id }
                        recentlyApplied.add(0, previewPhoto!!)
                        savePhotoList(ctx, "recents", recentlyApplied)
                    },
                    onBack = { previewPhoto = null }
                )
            }
        }
    }
}
