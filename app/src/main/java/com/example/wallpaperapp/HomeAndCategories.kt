package com.example.wallpaperapp

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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

@Composable
fun WelcomeScreen(onOpenWallpapers: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A1405), BgBlack, Color(0xFF0D1F06), BgBlack)))
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(22.dp).background(NeonGreen, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Wallpaper 4K AMOLED", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Creative", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Text("Visuals", color = NeonGreen, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Text("For You", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Modern textures | Soft glow | Premium wallpaper feel", color = TextGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))

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
                        modifier = Modifier.size(44.dp).background(Color(0xFF3A5C18), RoundedCornerShape(12.dp)),
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(Modifier.weight(1f), Icons.Outlined.StarOutline, "Rate App", "Support us") {
                Toast.makeText(context, "Thank you for rating!", Toast.LENGTH_SHORT).show()
            }
            ActionCard(Modifier.weight(1f), Icons.Outlined.GridView, "More Apps", "Explore more") {
                onOpenWallpapers()
            }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = TextGray, fontSize = 11.sp)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
    }
}

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
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Home", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Fresh wallpapers, ready for your screen.", color = TextGray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF1A2910), Color(0xFF0D140A))))
                .border(1.dp, BorderGreen, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                Text("EDITORS' CHOICE", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Handpicked\nwallpapers for\nyour screen", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
                Text("Explore picks ->", color = NeonGreen, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatChip(Modifier.weight(1f), Icons.Filled.Favorite, Color(0xFFFF4081), "Favourites", favCount)
            StatChip(Modifier.weight(1f), Icons.Outlined.History, Color.White, "Recently applied", recentCount)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("All Wallpapers", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = NeonGreen) }
        } else if (photos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Add your Unsplash API key in MainActivity.kt", color = TextGray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(photos) { photo ->
                    WallpaperGridItem(photo, favourites.any { it.id == photo.id }, { onToggleFav(photo) }, { onSelectPhoto(photo) })
                }
            }
        }
    }
}

@Composable
fun StatChip(modifier: Modifier, icon: ImageVector, tint: Color, label: String, count: Int) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, BorderGreen, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = Color.White, fontSize = 12.sp, maxLines = 1)
        }
        Text(count.toString(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WallpaperGridItem(photo: UnsplashPhoto, isFav: Boolean, onToggleFav: () -> Unit, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.58f)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .clickable { onClick() }
    ) {
        AsyncImage(model = photo.urls.small, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onToggleFav() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
              
