package com.example.wallpaperapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val BgBlack = Color(0xFF050805)
val CardDark = Color(0xFF101510)
val NeonGreen = Color(0xFF8CE62E)
val BorderGreen = Color(0xFF284214)
val TextGray = Color(0xFF9EA69E)

const val UNSPLASH_API_KEY = "j6I6E53KGj8Wbeq7A17r195_-iTyO8ypaSQEfh-KTcM"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = BgBlack,
                    surface = CardDark,
                    primary = NeonGreen
                )
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
    var showWelcome by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(1) }
    var previewPhoto by remember { mutableStateOf<UnsplashPhoto?>(null) }

    val favourites = remember { mutableStateListOf<UnsplashPhoto>() }
    val recentlyApplied = remember { mutableStateListOf<UnsplashPhoto>() }
    var homePhotos by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    val coroutineScope = rememberCoroutineScope()

    // --- BACK BUTTON / SIDE SWIPE FIX ---
    BackHandler(enabled = previewPhoto != null || (!showWelcome && selectedTab != 1)) {
        if (previewPhoto != null) {
            previewPhoto = null
        } else if (selectedTab != 1) {
            selectedTab = 1
        }
    }
    // ------------------------------------

    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://unsplash.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnsplashApiService::class.java)
    }

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
                    val uniqueNew = newPhotos.filter { it.id !in existingIds }
                    homePhotos = homePhotos + uniqueNew
                    currentPage = nextPage
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingMore = false
            }
        }
    }

    if (previewPhoto != null) {
        WallpaperPreviewScreen(
            photo = previewPhoto!!,
            isFav = favourites.any { it.id == previewPhoto!!.id },
            onToggleFav = {
                if (favourites.any { it.id == previewPhoto!!.id }) {
                    favourites.removeAll { it.id == previewPhoto!!.id }
                } else {
                    favourites.add(previewPhoto!!)
                }
            },
            onApplied = {
                if (recentlyApplied.none { it.id == previewPhoto!!.id }) {
                    recentlyApplied.add(0, previewPhoto!!)
                }
            },
            onBack = { previewPhoto = null }
        )
    } else if (showWelcome) {
        WelcomeScreen(onOpenWallpapers = { showWelcome = false })
    } else {
        Scaffold(
            containerColor = BgBlack,
            bottomBar = {
                FloatingBottomNav(
                    selectedTab = selectedTab,
                    onSelectTab = { selectedTab = it }
                )
            }
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
                        onToggleFav = { photo ->
                            if (favourites.any { it.id == photo.id }) favourites.removeAll { it.id == photo.id }
                            else favourites.add(photo)
                        },
                        onSelectPhoto = { previewPhoto = it }
                    )
                    2 -> OfflineScreen(onSelectPhoto = { previewPhoto = it })
                    3 -> MoreScreen(
                        favCount = favourites.size,
                        recentCount = recentlyApplied.size
                    )
                }
            }
        }
    }
}
