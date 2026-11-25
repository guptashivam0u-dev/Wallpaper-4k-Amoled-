package com.example.wallpaperapp

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
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
import java.net.URL

@Composable
fun OfflineScreen(onSelectPhoto: (UnsplashPhoto) -> Unit) {
    val filters = listOf("All Wallpapers", "Editors Choice", "AMOLED", "Abstract")
    var selectedFilter by remember { mutableStateOf("All Wallpapers") }

    val p1 = "https://unsplash.com"
    val p2 = "https://unsplash.com"
    val p3 = "https://unsplash.com"
    val p4 = "https://unsplash.com"
    val p5 = "https://unsplash.com"
    val p6 = "https://unsplash.com"

    val allList = remember {
        listOf(
            UnsplashPhoto("1", PhotoUrls(p1, p1, p1)),
            UnsplashPhoto("2", PhotoUrls(p2, p2, p2)),
            UnsplashPhoto("3", PhotoUrls(p3, p3, p3)),
            UnsplashPhoto("4", PhotoUrls(p4, p4, p4)),
            UnsplashPhoto("5", PhotoUrls(p5, p5, p5)),
            UnsplashPhoto("6", PhotoUrls(p6, p6, p6))
        )
    }

    val filteredList = when (selectedFilter) {
        "Editors Choice" -> allList.take(3)
        "AMOLED" -> listOf(allList, allList, allList)
        "Abstract" -> listOf(allList, allList)
        else -> allList
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Offline Wallpapers", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Wallpapers ready without internet.", color = TextGray, fontSize = 13.sp)
        Spacer(Modifier.height(14.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { f ->
                val sel = f == selectedFilter
                Box(
                    Modifier.clip(RoundedCornerShape(20.dp)).background(if (sel) Color(0xFF1F3310) else CardDark)
                        .border(1.dp, if (sel) NeonGreen else BorderGreen, RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = f }.padding(horizontal = 14.dp, vertical = 8.dp)
                ) { Text(f, color = if (sel) NeonGreen else Color.White, fontSize = 13.sp) }
            }
        }
        Spacer(Modifier.height(14.dp))
        LazyVerticalGrid(
            GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredList) { ph ->
                Box(Modifier.aspectRatio(0.58f).clip(RoundedCornerShape(16.dp)).clickable { onSelectPhoto(ph) }) {
                    AsyncImage(
                        model = ph.urls.small,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MoreScreen(favCount: Int, recentCount: Int) {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("More", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Manage your experience.", color = TextGray, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))
        MoreRow(Icons.Outlined.FavoriteBorder, "My wallpapers", "$favCount favourites • $recentCount recently applied") {}
        Spacer(Modifier.height(10.dp))
        MoreRow(Icons.Outlined.Language, "Visit our website", null) {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com")))
        }
        Spacer(Modifier.height(10.dp))
        MoreRow(Icons.Outlined.StarOutline, "Rate Us", null) {
            Toast.makeText(ctx, "Thanks for your support!", Toast.LENGTH_SHORT).show()
        }
        Spacer(Modifier.height(10.dp))
        MoreRow(Icons.Outlined.Share, "Share App", null) {
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out 4K AMOLED Wallpapers!")
            }
            ctx.startActivity(Intent.createChooser(i, "Share"))
        }
        Spacer(Modifier.height(10.dp))
        MoreRow(Icons.Outlined.Shield, "Privacy Policy", null) {}
        Spacer(Modifier.height(10.dp))
        MoreRow(Icons.Outlined.Info, "About", "Version 1.0 • 4K AMOLED Wallpapers") {}
    }
}

@Composable
fun MoreRow(icon: ImageVector, title: String, sub: String?, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardDark).clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                if (sub != null) Text(sub, color = TextGray, fontSize = 12.sp)
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextGray)
    }
}

@Composable
fun FloatingBottomNav(selectedTab: Int, onSelectTab: (Int) -> Unit) {
    val items = listOf(
        "Categories" to Icons.Outlined.GridView,
        "Home" to Icons.Outlined.Home,
        "Offline" to Icons.Outlined.Image,
        "More" to Icons.Outlined.MoreHoriz
    )
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF131A13)).border(1.dp, BorderGreen, RoundedCornerShape(32.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { idx, item ->
            val sel = selectedTab == idx
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onSelectTab(idx) }) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(if (sel) NeonGreen else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.second, item.first, tint = if (sel) Color.Black else TextGray)
                }
                Spacer(Modifier.height(2.dp))
                Text(item.first, color = if (sel) Color.White else TextGray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun WallpaperPreviewScreen(
    photo: UnsplashPhoto,
    isFav: Boolean,
    onToggleFav: () -> Unit,
    onApplied: () -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var applying by remember { mutableStateOf(false) }
    var downloading by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }

    // Wallpaper apply karne ka helper function
    fun applyWallpaper(flag: Int) {
        scope.launch {
            applying = true
            try {
                withContext(Dispatchers.IO) {
                    val bmp = BitmapFactory.decodeStream(URL(photo.urls.regular).openStream())
                    val wm = WallpaperManager.getInstance(ctx)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wm.setBitmap(bmp, null, true, flag)
                    } else {
                        wm.setBitmap(bmp)
                    }
                }
                onApplied()
                Toast.makeText(ctx, "Wallpaper Applied!", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
                Toast.makeText(ctx, "Failed to apply", Toast.LENGTH_SHORT).show()
            }
            applying = false
        }
    }

    // Gallery mein Download karne ka helper function
    fun downloadToGallery() {
        scope.launch {
            downloading = true
            try {
                withContext(Dispatchers.IO) {
                    val bmp = BitmapFactory.decodeStream(URL(photo.urls.full).openStream())
                    val filename = "Wallpaper_${System.currentTimeMillis()}.jpg"
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/4K_AMOLED_Wallpapers")
                        }
                    }
                    val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    uri?.let {
                        ctx.contentResolver.openOutputStream(it)?.use { out ->
                            bmp.compress(Bitmap.CompressFormat.JPEG, 95, out)
                        }
                    }
                }
                Toast.makeText(ctx, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
                Toast.makeText(ctx, "Download Failed", Toast.LENGTH_SHORT).show()
            }
            downloading = false
        }
    }

    Box(Modifier.fillMaxSize().background(BgBlack)) {
        AsyncImage(
            model = photo.urls.regular,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top Bar: Back Button + Share Button
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.Black.copy(0.6f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }

            IconButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Check out this awesome 4K Wallpaper: ${photo.urls.regular}")
                    }
                    ctx.startActivity(Intent.createChooser(shareIntent, "Share Wallpaper"))
                },
                modifier = Modifier.background(Color.Black.copy(0.6f), CircleShape)
            ) {
                Icon(Icons.Default.Share, null, tint = Color.White)
            }
        }

        // Bottom Bar: Favourite + Download + Set Wallpaper
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favourite Button
            IconButton(
                onClick = onToggleFav,
                modifier = Modifier.size(54.dp).background(CardDark, CircleShape).border(1.dp, BorderGreen, CircleShape)
            ) {
                Icon(
                    if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    null,
                    tint = if (isFav) Color(0xFFFF4081) else Color.White
                )
            }

            // Download Button
            IconButton(
                onClick = { if (!downloading) downloadToGallery() },
                modifier = Modifier.size(54.dp).background(CardDark, CircleShape).border(1.dp, BorderGreen, CircleShape)
            ) {
                if (downloading) {
                    CircularProgressIndicator(color = NeonGreen, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Download, null, tint = Color.White)
                }
            }

            // Set Wallpaper Button
            Button(
                onClick = { showApplyDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(27.dp),
                modifier = Modifier.weight(1f).height(54.dp)
            ) {
                Text(
                    if (applying) "Applying..." else "Set Wallpaper",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Home / Lock / Both Selection Dialog
        if (showApplyDialog) {
            AlertDialog(
                onDismissRequest = { showApplyDialog = false },
                containerColor = CardDark,
                title = { Text("Set Wallpaper As", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(onClick = {
                            showApplyDialog = false
                            applyWallpaper(WallpaperManager.FLAG_SYSTEM)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("Home Screen", color = NeonGreen, fontSize = 16.sp)
                        }
                        TextButton(onClick = {
                            showApplyDialog = false
                            applyWallpaper(WallpaperManager.FLAG_LOCK)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("Lock Screen", color = NeonGreen, fontSize = 16.sp)
                        }
                        TextButton(onClick = {
                            showApplyDialog = false
                            applyWallpaper(WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("Both Screens", color = NeonGreen, fontSize = 16.sp)
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}
