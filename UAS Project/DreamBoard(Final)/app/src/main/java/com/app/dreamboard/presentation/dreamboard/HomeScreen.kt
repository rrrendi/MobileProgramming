package com.app.dreamboard.presentation.dreamboard

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.dreamboard.R
import com.app.dreamboard.data.model.DreamItem
import com.app.dreamboard.ui.theme.DreamBluePrimary
import com.app.dreamboard.ui.theme.DreamBlueSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: DreamViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val dreams by viewModel.dreams.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val context = LocalContext.current
    var showInputDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var selectedDream by remember { mutableStateOf<DreamItem?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedDetailDream by remember { mutableStateOf<DreamItem?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf("") }
    var newCategoryName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var statusInput by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var categoryInput by remember { mutableStateOf("Lainnya") }
    val filteredDreams = if (selectedFilter == "Semua") dreams else dreams.filter { it.category == selectedFilter }
    val totalDreams = dreams.size
    val achievedDreams = dreams.count { it.isAchieved }
    val greenColor = Color(0xFF00E676)
    val backgroundImageRes = if (isDarkTheme) R.drawable.bg3 else R.drawable.bg7
    val homeHeaderColor = if (isDarkTheme) Color.White else Color(0xFF1D1B20)
    val overlayGradient = if (isDarkTheme) {
        Brush.verticalGradient(colors = listOf(Color(0xFF0D1B2A).copy(alpha = 0.4f), Color.Transparent, Color(0xFF0D1B2A).copy(alpha = 0.9f)))
    } else {
        Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.5f)))
    }

    val glassBaseColor = if (isDarkTheme) Color(0xFF0A1128).copy(alpha = 0.85f) else Color(0xFFF0F8FF).copy(alpha = 0.85f)
    val popupTextColor = if (isDarkTheme) Color(0xFFE3F2FD) else Color(0xFF051324)
    val borderBrush = Brush.linearGradient(colors = listOf(if(isDarkTheme) Color.White.copy(0.7f) else Color.White.copy(0.9f), if(isDarkTheme) Color.White.copy(0.1f) else Color.White.copy(0.4f)))
    val shadowColor = if(isDarkTheme) DreamBluePrimary.copy(alpha=0.5f) else Color.Black.copy(alpha=0.1f)
    val inputFieldColor = if (isDarkTheme) Color.White.copy(alpha = 0.08f) else DreamBluePrimary.copy(alpha = 0.05f)

    fun Modifier.dreamyAcrylic(shape: androidx.compose.ui.graphics.Shape) = this.shadow(16.dp, shape, spotColor = shadowColor).clip(shape).background(glassBaseColor).border(1.dp, borderBrush, shape)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = backgroundImageRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(overlayGradient))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Dream Board", style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = homeHeaderColor))
                            user?.displayName?.let { Text("Hi, $it", style = MaterialTheme.typography.labelMedium, color = homeHeaderColor.copy(alpha = 0.7f)) }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showProfileDialog = true }) {
                            val photoUrl = user?.photoUrl
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if(isDarkTheme) Color.White.copy(0.2f) else Color.Black.copy(0.1f))) {
                                if (photoUrl != null) AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) else Icon(Icons.Default.AccountCircle, null, modifier = Modifier.fillMaxSize(), tint = homeHeaderColor)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDarkTheme) Color.Black.copy(0.1f) else Color.White.copy(0.1f))
                )
            },
            floatingActionButton = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp).dreamyAcrylic(RoundedCornerShape(16.dp)).clickable { isEditing = false; titleInput = ""; descInput = ""; urlInput = ""; categoryInput = "Lainnya"; showInputDialog = true }) {
                    Icon(Icons.Rounded.Add, null, tint = if(isDarkTheme) Color.White else Color(0xFF1976D2), modifier = Modifier.size(32.dp))
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val displayCategories = if (categories.contains("Semua")) categories else listOf("Semua") + categories

                    items(displayCategories) { category ->
                        val isSelected = selectedFilter == category
                        val chipColor = if (isSelected) (if (isDarkTheme) DreamBluePrimary else DreamBlueSecondary) else (if (isDarkTheme) Color.White.copy(0.15f) else Color.Black.copy(0.08f))
                        val chipText = if (isSelected) Color.White else homeHeaderColor

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    if (selectedFilter == category) {
                                        selectedFilter = "Semua"
                                    } else {
                                        selectedFilter = category
                                    }
                                },
                            color = chipColor,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(text = category, modifier = Modifier.padding(16.dp, 10.dp), style = MaterialTheme.typography.labelLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = chipText)
                        }
                    }
                }

                if (filteredDreams.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.Star, null, Modifier.size(80.dp), tint = homeHeaderColor.copy(0.3f))
                            Text("Belum Ada Impian", style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold, color = homeHeaderColor.copy(0.6f))
                        }
                    }
                } else {
                    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2), contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp), modifier = Modifier.fillMaxSize()) {
                        items(filteredDreams) { dream ->
                            DreamItemCard(dream, onClick = { selectedDetailDream = dream; showDetailDialog = true }, onLongClick = { selectedDream = dream; isEditing = true; titleInput = dream.title; descInput = dream.description; urlInput = dream.imageUrl; statusInput = dream.isAchieved; categoryInput = dream.category; showInputDialog = true })
                        }
                    }
                }
            }
        }
    }

    if (showProfileDialog) {
        Dialog(onDismissRequest = { showProfileDialog = false }) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp).dreamyAcrylic(RoundedCornerShape(24.dp))) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        Image(painter = painterResource(id = backgroundImageRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)))
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.2f)))
                        IconButton(onClick = { showProfileDialog = false }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) { Icon(Icons.Default.Close, "Close", tint = Color.White) }
                    }
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.offset(y = (-50).dp).size(100.dp).dreamyAcrylic(CircleShape).padding(4.dp).clip(CircleShape)) {
                            val photoUrl = user?.photoUrl
                            if (photoUrl != null) AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(photoUrl).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) else Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = popupTextColor)
                        }
                        Text(user?.displayName ?: "Dreamer", style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold, color = popupTextColor, modifier = Modifier.offset(y = (-40).dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-20).dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$achievedDreams", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("Tercapai", style = MaterialTheme.typography.bodySmall, color = popupTextColor.copy(alpha = 0.7f)) }
                            Divider(modifier = Modifier.height(40.dp).width(1.dp), color = popupTextColor.copy(alpha = 0.2f))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$totalDreams", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary); Text("Total Impian", style = MaterialTheme.typography.bodySmall, color = popupTextColor.copy(alpha = 0.7f)) }
                        }
                        Divider(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-10).dp), color = popupTextColor.copy(alpha = 0.2f))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).offset(y = (-10).dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode, null, tint = popupTextColor)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(if(isDarkTheme) "Night Mode" else "Light Mode", style = MaterialTheme.typography.bodyLarge, color = popupTextColor)
                            }
                            Switch(checked = isDarkTheme, onCheckedChange = { onThemeChange(it) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = DreamBluePrimary))
                        }
                        Button(onClick = { showProfileDialog = false; viewModel.logout(); navController.navigate("sign_in") { popUpTo("home") { inclusive = true } } }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer), modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-15).dp).height(50.dp)) { Icon(Icons.Default.Logout, null); Spacer(modifier = Modifier.width(8.dp)); Text("Keluar Akun") }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

    if (showDetailDialog && selectedDetailDream != null) {
        Dialog(onDismissRequest = { showDetailDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight().dreamyAcrylic(RoundedCornerShape(24.dp))) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(selectedDetailDream!!.imageUrl).crossfade(true).build(), contentDescription = "Full Image", contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth())
                    Column(modifier = Modifier.padding(24.dp)) {

                        Surface(color = if(isDarkTheme) Color.White.copy(0.1f) else Color.Black.copy(0.05f), shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(text = selectedDetailDream!!.category, modifier = Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelMedium, color = popupTextColor.copy(alpha = 0.8f))
                        }

                        Text(text = selectedDetailDream!!.title, style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold, color = popupTextColor)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (selectedDetailDream!!.description.isNotEmpty()) {
                            Text(text = selectedDetailDream!!.description, style = MaterialTheme.typography.bodyLarge, color = popupTextColor.copy(alpha = 0.8f), lineHeight = 24.sp); Spacer(modifier = Modifier.height(20.dp))
                        }

                        if (selectedDetailDream!!.isAchieved) {
                            Surface(color = greenColor, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(50.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(Icons.Rounded.CheckCircle, null, tint = Color.White); Spacer(modifier = Modifier.width(8.dp))
                                    Text("TERCAPAI", fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 2.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInputDialog) {
        Dialog(onDismissRequest = { showInputDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(modifier = Modifier.fillMaxWidth(0.92f).dreamyAcrylic(RoundedCornerShape(28.dp))) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (urlInput.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(urlInput).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.6f)))))
                            Text(if (isEditing) "Edit Mode" else "New Dream", style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
                        }
                    } else {
                        Text(if (isEditing) "Edit Impian" else "Impian Baru", style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp), color = popupTextColor)
                    }

                    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = if(urlInput.isNotEmpty()) 16.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val textFieldColors = OutlinedTextFieldDefaults.colors(focusedContainerColor = inputFieldColor, unfocusedContainerColor = inputFieldColor, focusedTextColor = popupTextColor, unfocusedTextColor = popupTextColor, focusedBorderColor = popupTextColor.copy(0.3f), unfocusedBorderColor = popupTextColor.copy(0.1f), focusedLabelColor = popupTextColor, unfocusedLabelColor = popupTextColor.copy(0.7f), cursorColor = popupTextColor)
                        OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = textFieldColors)
                        OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Ceritakan impianmu...") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
                        OutlinedTextField(value = urlInput, onValueChange = { urlInput = it }, label = { Text("Link Gambar") }, placeholder = { Text("https://...") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.Image, null, tint = popupTextColor.copy(0.6f)) }, singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), colors = textFieldColors)

                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Kategori", style = MaterialTheme.typography.labelMedium, color = popupTextColor.copy(0.7f))
                                TextButton(onClick = { showAddCategoryDialog = true }) { Text("+ Baru") }
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(categories.filter { it != "Semua" }) { category ->
                                    val isSelected = categoryInput == category
                                    val chipColor by animateColorAsState(targetValue = if (isSelected) DreamBluePrimary else inputFieldColor, label = "categoryChip")
                                    val chipText = if (isSelected) Color.White else popupTextColor

                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .combinedClickable(
                                                onClick = { categoryInput = category },
                                                onLongClick = {
                                                    val defaultCats = listOf("Liburan", "Karir", "Gaming", "Ibadah", "Kendaraan", "Lainnya")
                                                    if (category !in defaultCats) {
                                                        categoryToDelete = category
                                                        showDeleteCategoryDialog = true
                                                    } else {
                                                        Toast.makeText(context, "Kategori bawaan tidak bisa dihapus", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            ),
                                        color = chipColor,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(text = category, modifier = Modifier.padding(16.dp, 8.dp), style = MaterialTheme.typography.labelMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = chipText)
                                    }
                                }
                            }
                        }

                        if (isEditing && selectedDream != null) {
                            Divider(color = popupTextColor.copy(0.2f), modifier = Modifier.padding(vertical = 8.dp))
                            val statusColor by animateColorAsState(targetValue = if (statusInput) greenColor else inputFieldColor, label = "color")
                            val statusContentColor = if (statusInput) Color.White else popupTextColor.copy(0.7f)
                            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(statusColor).clickable { statusInput = !statusInput }.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(if(statusInput) Icons.Rounded.Check else Icons.Rounded.Star, null, tint = statusContentColor); Spacer(modifier = Modifier.width(12.dp)); Text(if (statusInput) "Mimpi Tercapai! ✨" else "Tandai Tercapai", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = statusContentColor)
                            }
                            OutlinedButton(onClick = { showDeleteConfirmDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(0.5f)), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Icon(Icons.Outlined.Delete, null); Spacer(modifier = Modifier.width(8.dp)); Text("Hapus Impian Ini") }
                        }

                        Button(onClick = { if (titleInput.isNotEmpty() && urlInput.isNotEmpty()) { if (isEditing && selectedDream != null) viewModel.updateDream(selectedDream!!.id, titleInput, descInput, urlInput, statusInput, categoryInput) else viewModel.addDream(titleInput, descInput, urlInput, categoryInput); showInputDialog = false; selectedDream = null } }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                            Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(DreamBluePrimary, DreamBlueSecondary))), contentAlignment = Alignment.Center) { Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) }
                        }
                        if (!isEditing) TextButton(onClick = { showInputDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Batal", color = popupTextColor.copy(0.6f)) }
                    }
                }
            }
        }

        if (showDeleteConfirmDialog && selectedDream != null) {
            AlertDialog(onDismissRequest = { showDeleteConfirmDialog = false }, icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) }, title = { Text("Yakin Hapus?", color = popupTextColor) }, text = { Text("Data ini akan hilang selamanya.", color = popupTextColor.copy(0.7f)) }, containerColor = Color.Transparent, modifier = Modifier.dreamyAcrylic(RoundedCornerShape(24.dp)), confirmButton = { Button(onClick = { selectedDream?.let { viewModel.deleteDream(it.id) }; selectedDream = null; showDeleteConfirmDialog = false; showInputDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Hapus") } }, dismissButton = { TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Batal", color = popupTextColor) } })
        }

        if (showAddCategoryDialog) {
            AlertDialog(onDismissRequest = { showAddCategoryDialog = false }, title = { Text("Tambah Kategori Baru", color = popupTextColor) }, text = { OutlinedTextField(value = newCategoryName, onValueChange = { newCategoryName = it }, label = { Text("Nama Kategori") }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = popupTextColor, unfocusedTextColor = popupTextColor, focusedContainerColor = inputFieldColor, unfocusedContainerColor = inputFieldColor)) }, containerColor = Color.Transparent, modifier = Modifier.dreamyAcrylic(RoundedCornerShape(24.dp)), confirmButton = { Button(onClick = { if(newCategoryName.isNotEmpty()) { viewModel.addCategory(newCategoryName); categoryInput = newCategoryName; newCategoryName = ""; showAddCategoryDialog = false } }) { Text("Tambah") } }, dismissButton = { TextButton(onClick = { showAddCategoryDialog = false }) { Text("Batal", color = popupTextColor) } })
        }

        if (showDeleteCategoryDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteCategoryDialog = false },
                title = { Text("Hapus Kategori?", color = popupTextColor) },
                text = { Text("Hapus kategori '$categoryToDelete'?", color = popupTextColor.copy(0.7f)) },
                containerColor = Color.Transparent,
                modifier = Modifier.dreamyAcrylic(RoundedCornerShape(24.dp)),
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteCategory(categoryToDelete)
                            if (categoryInput == categoryToDelete) categoryInput = "Lainnya"
                            showDeleteCategoryDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Hapus") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteCategoryDialog = false }) { Text("Batal", color = popupTextColor) }
                }
            )
        }
    }
}