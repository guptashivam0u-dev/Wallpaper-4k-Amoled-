package com.example.wallpaperapp

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

// थीम के मुख्य रंग (AMOLED Black + Neon Green)
val BgBlack = Color(0xFF050805)
val CardDark = Color(0xFF101510)
val NeonGreen = Color(0xFF8CE62E)
val BorderGreen = Color(0xFF284214)
val TextGray = Color(0xFF9EA69E)

// अपनी Unsplash API Key यहाँ डालें
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
    var selectedTab by remember { mutableStateOf(1) } // 0=Categories, 1=Home, 2=Offline, 3=More
    var previewPhoto by remember { mutableStateOf<UnsplashPhoto?>(null) }

    val favourites = remember { mutableStateListOf<UnsplashPhoto>() }
    val recentlyApplied = remember { mutableStateListOf<UnsplashPhoto>() }
    var homePhotos by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://unsplash.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnsplashApiService::class.java)
    }

    // इंटरनेट से वॉलपेपर लोड करें
    LaunchedEffect(Unit) {
        try {
            if (UNSPLASH_API_KEY != "YOUR_UNSPLASH_API_KEY") {
                homePhotos = api.getPhotos(UNSPLASH_API_KEY)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
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

// 1. वेलकम स्क्रीन (पाँचवीं फोटो जैसा डिज़ाइन)
@Composable
fun WelcomeScreen(onOpenWallpapers: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A1405), BgBlack, Color(0xFF0D1F06), BgBlack)
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(22.dp)
                    .background(NeonGreen, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Wallpaper 4K AMOLED", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Creative", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Text("Visuals", color = NeonGreen, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Text("For You", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Modern textures | Soft glow | Premium wallpaper feel",
            color = TextGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Open Wallpapers मुख्य बटन
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .clip(RoundedCornerShape(37.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF284210), Color(0xFF142108))))
                .border(1.5.dp, NeonGreen, RoundedCornerShape(37.dp))
                .clickable { onOpenWallpapers() }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF3A5C18), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Open Wallpapers", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rate App और More Apps कार्ड
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.StarOutline,
                title = "Rate App",
                subtitle = "Support us",
                onClick = { Toast.makeText(context, "Thank you for rating!", Toast.LENGTH_SHORT).show() }
            )
            ActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.GridView,
                title = "More Apps",
                subtitle = "Explore more",
                onClick = { onOpenWallpapers() }
            )
        }
    }
}

@Composable
fun ActionCard(modifier: Modifier, icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CardDark)
            .border(1.dp, BorderGreen, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp),
      
