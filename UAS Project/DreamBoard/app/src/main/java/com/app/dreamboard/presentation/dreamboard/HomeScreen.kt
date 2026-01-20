package com.app.dreamboard.presentation.dreamboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: DreamViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val dreams by viewModel.dreams.collectAsState()

    // --- STATES ---
    var showInputDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var selectedDream by remember { mutableStateOf<DreamItem?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedDetailDream by remember { mutableStateOf<DreamItem?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // State Input Form
    var isEditing by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var statusInput by remember { mutableStateOf(false) }
    val categories = listOf("Semua", "Liburan", "Karir", "Gaming", "Ibadah", "Kendaraan", "Lainnya")
    var selectedFilter by remember { mutableStateOf("Semua") }
    var categoryInput by remember { mutableStateOf("Lainnya") }

    // Filter dreams based on selected category
    val filteredDreams = if (selectedFilter == "Semua") {
        dreams
    } else {
        dreams.filter { it.category == selectedFilter }
    }

    val totalDreams = dreams.size
    val achievedDreams = dreams.count { it.isAchieved }

    val greenColor = Color(0xFF00E676)

    // ==========================================
    // --- LOGIKA TEMA & BACKGROUND ---
    // ==========================================

    val backgroundImageRes = if (isDarkTheme) R.drawable.bg3 else R.drawable.bg7

    val homeHeaderColor = if (isDarkTheme) Color.White else Color(0xFF1D1B20)

    val overlayGradient = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D1B2A).copy(alpha = 0.4f),
                Color.Transparent,
                Color(0xFF0D1B2A).copy(alpha = 0.9f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f),
                Color.White.copy(alpha = 0.2f),
                Color.White.copy(alpha = 0.5f)
            )
        )
    }

    val glassBaseColor = if (isDarkTheme) {
        Color(0xFF0A1128).copy(alpha = 0.85f)
    } else {
        Color(0xFFF0F8FF).copy(alpha = 0.85f)
    }

    val popupTextColor = if (isDarkTheme) Color(0xFFE3F2FD) else Color(0xFF051324)

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            if(isDarkTheme) Color.White.copy(0.7f) else Color.White.copy(0.9f),
            if(isDarkTheme) Color.White.copy(0.1f) else Color.White.copy(0.4f),
            if(isDarkTheme) Color.White.copy(0.3f) else Color.White.copy(0.6f)
        )
    )

    val surfaceSheenBrush = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent, Color.White.copy(alpha = 0.05f)),
        tileMode = TileMode.Clamp
    )

    val shadowColor = if(isDarkTheme) DreamBluePrimary.copy(alpha=0.5f) else Color.Black.copy(alpha=0.1f)

    fun Modifier.dreamyAcrylic(shape: androidx.compose.ui.graphics.Shape) = this
        .shadow(elevation = 16.dp, shape = shape, spotColor = shadowColor)
        .clip(shape)
        .background(glassBaseColor)
        .background(surfaceSheenBrush)
        .border(width = 1.dp, brush = borderBrush, shape = shape)

    val inputFieldColor = if (isDarkTheme) Color.White.copy(alpha = 0.08f) else DreamBluePrimary.copy(alpha = 0.05f)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // [DYNAMIC BACKGROUND IMAGE]
        Image(
            painter = painterResource(id = backgroundImageRes),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // [DYNAMIC OVERLAY]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayGradient)
        )

        // --- KONTEN UTAMA ---
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = "Dream Board", style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = homeHeaderColor))
                            user?.displayName?.let { Text("Hi, $it", style = MaterialTheme.typography.labelMedium, color = homeHeaderColor.copy(alpha = 0.7f)) }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showProfileDialog = true }) {
                            val photoUrl = user?.photoUrl
                            val profileBg = if(isDarkTheme) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f)

                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(profileBg)) {
                                if (photoUrl != null) AsyncImage(model = photoUrl, contentDescription = "Profile", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                else Icon(Icons.Default.AccountCircle, null, modifier = Modifier.fillMaxSize(), tint = homeHeaderColor)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDarkTheme) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f)
                    )
                )
            },
            floatingActionButton = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .dreamyAcrylic(RoundedCornerShape(16.dp))
                        .clickable {
                            isEditing = false
                            titleInput = ""
                            descInput = ""
                            urlInput = ""
                            categoryInput = "Lainnya"
                            showInputDialog = true
                        }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add",
                        tint = if(isDarkTheme) Color.White else Color(0xFF1976D2),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // [CATEGORY FILTER CHIPS]
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedFilter == category
                        val chipBackgroundColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                if (isDarkTheme) DreamBluePrimary else DreamBlueSecondary
                            } else {
                                if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)
                            },
                            label = "chipColor"
                        )
                        val chipTextColor = if (isSelected) Color.White else homeHeaderColor

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedFilter = category },
                            color = chipBackgroundColor,
                            shape = RoundedCornerShape(20.dp),
                            border = if (!isSelected) BorderStroke(1.dp, homeHeaderColor.copy(alpha = 0.2f)) else null
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = chipTextColor
                            )
                        }
                    }
                }

                // [DREAM ITEMS GRID]
                if (filteredDreams.isEmpty()) {
                    // [EMPTY STATE]
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = homeHeaderColor.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Belum Ada Impian",
                                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                                fontWeight = FontWeight.Bold,
                                color = homeHeaderColor.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedFilter == "Semua")
                                    "Mulai tambahkan impianmu dengan menekan tombol +"
                                else
                                    "Tidak ada impian di kategori \"$selectedFilter\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = homeHeaderColor.copy(alpha = 0.5f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredDreams) { dream ->
                            DreamItemCard(
                                dream = dream,
                                onClick = { selectedDetailDream = dream; showDetailDialog = true },
                                onLongClick = {
                                    selectedDream = dream
                                    isEditing = true
                                    titleInput = dream.title
                                    descInput = dream.description
                                    urlInput = dream.imageUrl
                                    statusInput = dream.isAchieved
                                    categoryInput = dream.category
                                    showInputDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- 1. POPUP PROFIL ---
    if (showProfileDialog) {
        Dialog(onDismissRequest = { showProfileDialog = false }) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp).dreamyAcrylic(RoundedCornerShape(24.dp))) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(brush = Brush.horizontalGradient(colors = listOf(DreamBluePrimary, DreamBlueSecondary)))) {
                        IconButton(onClick = { showProfileDialog = false }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) { Icon(Icons.Default.Close, "Close", tint = Color.White) }
                    }
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.offset(y = (-50).dp).size(100.dp).dreamyAcrylic(CircleShape).padding(4.dp).clip(CircleShape)) {
                            val photoUrl = user?.photoUrl
                            if (photoUrl != null) AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(photoUrl).crossfade(true).build(), contentDescription = "Profile", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            else Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = popupTextColor)
                        }
                        Spacer(modifier = Modifier.height(12.dp).offset(y = (-50).dp))
                        Text(text = user?.displayName ?: "Dreamer", style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = popupTextColor, modifier = Modifier.offset(y = (-50).dp))
                        Spacer(modifier = Modifier.height(24.dp).offset(y = (-50).dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-50).dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$achievedDreams", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("Tercapai", style = MaterialTheme.typography.bodySmall, color = popupTextColor.copy(alpha = 0.7f)) }
                            Divider(modifier = Modifier.height(40.dp).width(1.dp), color = popupTextColor.copy(alpha = 0.2f))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$totalDreams", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary); Text("Total Impian", style = MaterialTheme.typography.bodySmall, color = popupTextColor.copy(alpha = 0.7f)) }
                        }
                        Spacer(modifier = Modifier.height(24.dp).offset(y = (-50).dp))
                        Divider(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-50).dp), color = popupTextColor.copy(alpha = 0.2f))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).offset(y = (-50).dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode, contentDescription = null, tint = popupTextColor)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Night Mode", style = MaterialTheme.typography.bodyLarge, color = popupTextColor)
                            }
                            Switch(checked = isDarkTheme, onCheckedChange = { onThemeChange(it) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = DreamBluePrimary, uncheckedThumbColor = Color.Gray, uncheckedTrackColor = Color.Transparent))
                        }
                        Button(onClick = { showProfileDialog = false; viewModel.logout(); navController.navigate("sign_in") { popUpTo("home") { inclusive = true } } }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer), modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-30).dp).height(50.dp)) { Icon(Icons.Default.Logout, null); Spacer(modifier = Modifier.width(8.dp)); Text("Keluar Akun") }
                        Spacer(modifier = Modifier.height(1.dp))
                    }
                }
            }
        }
    }

    // --- 2. DIALOG PREVIEW ---
    if (showDetailDialog && selectedDetailDream != null) {
        Dialog(onDismissRequest = { showDetailDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight().dreamyAcrylic(RoundedCornerShape(24.dp))) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(selectedDetailDream!!.imageUrl).crossfade(true).build(), contentDescription = "Full Image", contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth())
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(text = selectedDetailDream!!.title, style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold, color = popupTextColor)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (selectedDetailDream!!.description.isNotEmpty()) {
                            Text(text = selectedDetailDream!!.description, style = MaterialTheme.typography.bodyLarge, color = popupTextColor.copy(alpha = 0.8f), lineHeight = 24.sp); Spacer(modifier = Modifier.height(20.dp))
                        }
                        if (selectedDetailDream!!.isAchieved) {
                            Surface(color = greenColor, shape = RoundedCornerShape(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) { Icon(Icons.Rounded.CheckCircle, null, tint = Color.White, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("TERCAPAI ✨", fontWeight = FontWeight.Bold, color = Color.White) }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- 3. DIALOG INPUT ---
    if (showInputDialog) {
        Dialog(onDismissRequest = { showInputDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(modifier = Modifier.fillMaxWidth(0.92f).dreamyAcrylic(RoundedCornerShape(28.dp))) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (urlInput.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(urlInput).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.6f)))))
                            Text(text = if (isEditing) "Edit Mode" else "New Dream", style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
                        }
                    } else {
                        Text(text = if (isEditing) "Edit Impian" else "Impian Baru", style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp), color = popupTextColor)
                    }

                    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = if(urlInput.isNotEmpty()) 16.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        val textFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = inputFieldColor, unfocusedContainerColor = inputFieldColor,
                            focusedTextColor = popupTextColor, unfocusedTextColor = popupTextColor,
                            focusedBorderColor = popupTextColor.copy(alpha = 0.3f), unfocusedBorderColor = popupTextColor.copy(alpha = 0.1f),
                            focusedLabelColor = popupTextColor, unfocusedLabelColor = popupTextColor.copy(alpha = 0.7f),
                            cursorColor = popupTextColor
                        )

                        OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = textFieldColors)
                        OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Ceritakan impianmu...") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
                        OutlinedTextField(value = urlInput, onValueChange = { urlInput = it }, label = { Text("Link Gambar") }, placeholder = { Text("https://...") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.Default.Image, null, tint = popupTextColor.copy(alpha = 0.6f)) }, singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), colors = textFieldColors)

                        // [CATEGORY SELECTOR]
                        Column {
                            Text("Kategori", style = MaterialTheme.typography.labelMedium, color = popupTextColor.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(categories.filter { it != "Semua" }) { category ->
                                    val isSelected = categoryInput == category
                                    val categoryChipColor by animateColorAsState(
                                        targetValue = if (isSelected) DreamBluePrimary else inputFieldColor,
                                        label = "categoryChip"
                                    )
                                    Surface(
                                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { categoryInput = category },
                                        color = categoryChipColor,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = category,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else popupTextColor
                                        )
                                    }
                                }
                            }
                        }

                        if (isEditing && selectedDream != null) {
                            Divider(color = popupTextColor.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

                            val statusColor by animateColorAsState(targetValue = if (statusInput) greenColor else inputFieldColor, label = "color")
                            val statusContentColor = if (statusInput) Color.White else popupTextColor.copy(alpha = 0.7f)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(statusColor)
                                    .clickable { statusInput = !statusInput }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(imageVector = if(statusInput) Icons.Rounded.Check else Icons.Rounded.Star, contentDescription = null, tint = statusContentColor)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = if (statusInput) "Mimpi Tercapai! ✨" else "Tandai Tercapai", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = statusContentColor)
                            }

                            OutlinedButton(
                                onClick = { showDeleteConfirmDialog = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Outlined.Delete, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hapus Impian Ini")
                            }
                        }

                        Button(
                            onClick = {
                                if (titleInput.isNotEmpty() && urlInput.isNotEmpty()) {
                                    if (isEditing && selectedDream != null) viewModel.updateDream(selectedDream!!.id, titleInput, descInput, urlInput, statusInput, categoryInput)
                                    else viewModel.addDream(titleInput, descInput, urlInput, categoryInput)
                                    showInputDialog = false; selectedDream = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(DreamBluePrimary, DreamBlueSecondary))), contentAlignment = Alignment.Center) {
                                Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            }
                        }

                        if (!isEditing) TextButton(onClick = { showInputDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Batal", color = popupTextColor.copy(alpha = 0.6f)) }
                    }
                }
            }
        }

        // --- 4. DIALOG KONFIRMASI HAPUS ---
        if (showDeleteConfirmDialog && selectedDream != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Yakin Hapus?", color = popupTextColor, fontFamily = FontFamily.Serif) },
                text = { Text("Data ini akan hilang selamanya.", color = popupTextColor.copy(alpha = 0.7f)) },
                containerColor = Color.Transparent,
                modifier = Modifier.dreamyAcrylic(RoundedCornerShape(24.dp)),
                confirmButton = {
                    Button(onClick = {
                        selectedDream?.let { viewModel.deleteDream(it.id) }
                        selectedDream = null
                        showDeleteConfirmDialog = false
                        showInputDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Hapus") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Batal", color = popupTextColor) }
                }
            )
        }
    }
}