package com.example.wallpaperapp

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import java.io.File
import java.io.FileOutputStream
import java.net.URL

fun getSavedOfflinePhotos(ctx: Context): List<UnsplashPhoto> {
    val dir = File(ctx.filesDir, "offline_wallpapers")
    if (!dir.exists()) return emptyList()
    val files = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: return emptyList()
    return files.map { file ->
        val path = file.absolutePath
        UnsplashPhoto(file.nameWithoutExtension, PhotoUrls(path, path, path))
    }
}

@Composable
fun OfflineScreen(onSelectPhoto: (UnsplashPhoto) -> Unit) {
    val ctx = LocalContext.current
    var savedList by remember { mutableStateOf(getSavedOfflinePhotos(ctx)) }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Offline Wallpapers", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("${savedList.size} wallpapers saved for offline use.", color = TextGray, fontSize = 13.sp)
            }
            IconButton(onClick = { savedList = getSavedOfflinePhotos(ctx) }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = NeonGreen)
            }
        }
        Spacer(Modifier.height(16.dp))

        if (savedList.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(bottom = 80.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = TextGray, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No Offline Wallpapers Yet", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Open any online wallpaper and tap the\nSave Offline (+) icon to view without internet.",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(savedList) { ph ->
                    Box(Modifier.aspectRatio(0.58f).clip(RoundedCornerShape(16.dp)).clickable { onSelectPhoto(ph) }) {
                        AsyncImage(
                            model = File(ph.urls.small),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
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
fun MoreScreen(
    favourites: List<UnsplashPhoto>,
    recentlyApplied: List<UnsplashPhoto>,
    onSelectPhoto: (UnsplashPhoto) -> Unit
) {
    val ctx = LocalContext.current
    var showMyWallpapers by remember { mutableStateOf(false) }
    var subTab by remember { mutableStateOf(0) }

    if (showMyWallpapers) {
        val currentList = if (subTab == 0) favourites else recentlyApplied
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showMyWallpapers = false }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("My Wallpapers", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { subTab = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (subTab == 0) NeonGreen else CardDark),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Favourites (${favourites.size})", color = if (subTab == 0) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { subTab = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (subTab == 1) NeonGreen else CardDark),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Applied (${recentlyApplied.size})", color = if (subTab == 1) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            if (currentList.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(bottom = 80.dp), contentAlignment = Alignment.Center) {
                    Text(
                        if (subTab == 0) "No favourite wallpapers yet." else "No applied wallpapers yet.",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(currentList, key = { it.id }) { ph ->
                        val isLocal = !ph.urls.small.startsWith("http")
                        Box(Modifier.aspectRatio(0.58f).clip(RoundedCornerShape(16.dp)).clickable { onSelectPhoto(ph) }) {
                            AsyncImage(
                                model = if (isLocal) File(ph.urls.small) else ph.urls.small,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
    else {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("More", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Manage your experience.", color = TextGray, fontSize = 13.sp)
            Spacer(Modifier.height(20.dp))
            MoreRow(Icons.Outlined.FavoriteBorder, "My wallpapers", "${favourites.size} favourites • ${recentlyApplied.size} recently applied") {
                showMyWallpapers = true
            }
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
    var savingOffline by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }

    val isLocalFile = !photo.urls.regular.startsWith("http")
    val offlineDir = remember { File(ctx.filesDir, "offline_wallpapers") }
    var isSavedOffline by remember {
        mutableStateOf(isLocalFile || File(offlineDir, "${photo.id}.jpg").exists())
    }

    fun loadBitmap(pathOrUrl: String): Bitmap? {
        return if (pathOrUrl.startsWith("http")) {
            BitmapFactory.decodeStream(URL(pathOrUrl).openStream())
        } else {
            BitmapFactory.decodeFile(pathOrUrl)
        }
    }

    fun toggleSaveOffline() {
        scope.launch {
            savingOffline = true
            try {
                withContext(Dispatchers.IO) {
                    if (!offlineDir.exists()) offlineDir.mkdirs()
                    val targetFile = if (isLocalFile) File(photo.urls.regular) else File(offlineDir, "${photo.id}.jpg")

                    if (isSavedOffline) {
                        if (targetFile.exists()) targetFile.delete()
                        isSavedOffline = false
                    } else {
                        val bmp = BitmapFactory.decodeStream(URL(photo.urls.regular).openStream())
                        FileOutputStream(targetFile).use { out ->
                            bmp.compress(Bitmap.CompressFormat.JPEG, 92, out)
                        }
                        isSavedOffline = true
                    }
                }
                val msg = if (isSavedOffline) "Saved to Offline Tab!" else "Removed from Offline!"
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                if (isLocalFile && !isSavedOffline) onBack()
            } catch (_: Exception) {
                Toast.makeText(ctx, "Failed to save offline", Toast.LENGTH_SHORT).show()
            }
            savingOffline = false
        }
    }

    fun downloadToGallery() {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val bmp = loadBitmap(photo.urls.regular)
                    if (bmp != null) {
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
                }
                Toast.makeText(ctx, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
                Toast.makeText(ctx, "Download Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun applyWallpaper(flag: Int) {
        scope.launch {
            applying = true
            try {
                withContext(Dispatchers.IO) {
                    val bmp = loadBitmap(photo.urls.regular)
                    if (bmp != null) {
                        val wm = WallpaperManager.getInstance(ctx)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wm.setBitmap(bmp, null, true, flag)
                        } else {
                            wm.setBitmap(bmp)
                        }
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

    Box(Modifier.fillMaxSize().background(BgBlack)) {
        AsyncImage(
            model = if (isLocalFile) File(photo.urls.regular) else photo.urls.regular,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconButton(
                    onClick = { downloadToGallery() },
                    modifier = Modifier.background(Color.Black.copy(0.6f), CircleShape)
                ) {
                    Icon(Icons.Outlined.FileDownload, contentDescription = "Save to Gallery", tint = Color.White)
                }

                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Check out this awesome 4K Wallpaper!")
                        }
                        ctx.startActivity(Intent.createChooser(shareIntent, "Share Wallpaper"))
                    },
                    modifier = Modifier.background(Color.Black.copy(0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            IconButton(
                onClick = { if (!savingOffline) toggleSaveOffline() },
                modifier = Modifier.size(54.dp)
                    .background(if (isSavedOffline) Color(0xFF1F3310) else CardDark, CircleShape)
                    .border(1.dp, if (isSavedOffline) NeonGreen else BorderGreen, CircleShape)
            ) {
                if (savingOffline) {
                    CircularProgressIndicator(color = NeonGreen, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        if (isSavedOffline) Icons.Filled.CheckCircle else Icons.Outlined.AddCircleOutline,
                        contentDescription = "Save Offline",
                        tint = if (isSavedOffline) NeonGreen else Color.White
                    )
                }
            }

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
