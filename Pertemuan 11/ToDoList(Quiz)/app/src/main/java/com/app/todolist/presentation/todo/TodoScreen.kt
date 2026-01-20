package com.app.todolist.presentation.todo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.app.todolist.R
import com.app.todolist.data.model.Todo
import com.app.todolist.data.model.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    // State Data
    val todos by viewModel.todos.collectAsState()

    // State Input (Untuk Bottom Sheet)
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var selectedCategory by remember { mutableStateOf("Kerja") }

    // State UI
    var showBottomSheet by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // State Filter & Search
    var searchQuery by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("Semua") }

    val sheetState = rememberModalBottomSheetState()

    // Init Data
    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    // --- LOGIKA FILTERING ---
    val filteredTodos = todos.filter { todo ->
        val matchesSearch = todo.title.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (filterCategory == "Semua") true else todo.category == filterCategory
        val matchesActive = if (filterCategory == "Belum Selesai") !todo.isCompleted else true

        matchesSearch && matchesCategory && matchesActive
    }

    // --- LOGIKA STATISTIK ---
    val totalTasks = todos.size
    val completedTasks = todos.count { it.isCompleted }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    // --- [PROFILE POPUP] ---
    if (showProfileDialog) {
        Dialog(onDismissRequest = { showProfileDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Box {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(brush = Brush.horizontalGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))))
                    IconButton(onClick = { showProfileDialog = false }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) { Icon(Icons.Default.Close, "Close", tint = Color.White) }
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(60.dp))
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp).background(Color.White, CircleShape).padding(4.dp).clip(CircleShape)) {
                            if (userData?.profilePictureUrl != null) AsyncImage(model = userData.profilePictureUrl, contentDescription = "Profile", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            else Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(userData?.username ?: "Pengguna", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Member", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$completedTasks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("Selesai", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                            Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("${totalTasks - completedTasks}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error); Text("Pending", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { showProfileDialog = false; onSignOut() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer), modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(50.dp), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("Keluar Akun", fontWeight = FontWeight.SemiBold) }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // --- INPUT BOTTOM SHEET ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp).fillMaxWidth()) {
                Text("Tambah Tugas Baru", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = todoText, onValueChange = { todoText = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Apa rencana hari ini?") }, label = { Text("Judul Tugas") }, singleLine = true, shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Kategori", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Kerja", "Kuliah", "Hobby", "Lainnya")) { cat ->
                        FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) }, leadingIcon = if (selectedCategory == cat) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Prioritas", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("High", "Medium", "Low")) { pri ->
                        FilterChip(selected = selectedPriority == pri, onClick = { selectedPriority = pri }, label = { Text(pri) }, colors = FilterChipDefaults.filterChipColors(selectedLabelColor = getPriorityColor(pri), selectedContainerColor = getPriorityColor(pri).copy(alpha = 0.2f)))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { if (todoText.isNotBlank()) { userData?.userId?.let { uid -> viewModel.add(uid, todoText, selectedPriority, selectedCategory) }; todoText = ""; showBottomSheet = false } }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Add, null); Spacer(modifier = Modifier.width(8.dp)); Text("Tambah Tugas") }
            }
        }
    }

    // --- UI UTAMA ---
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.bg2), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(onClick = { showBottomSheet = true }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Default.Add, "Tambah", modifier = Modifier.size(32.dp))
                }
            },
            topBar = {
                TopAppBar(
                    title = { Column { Text("Halo, ${userData?.username?.split(" ")?.firstOrNull() ?: "User"}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)); Text("Yuk selesaikan tugasmu!", style = MaterialTheme.typography.bodySmall) } },
                    actions = { userData?.let { AsyncImage(model = it.profilePictureUrl, contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape).clickable { showProfileDialog = true }, contentScale = ContentScale.Crop); Spacer(modifier = Modifier.width(16.dp)) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // DASHBOARD
                Card(modifier = Modifier.padding(16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(progress = { 1f }, modifier = Modifier.size(60.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f), strokeWidth = 6.dp)
                            CircularProgressIndicator(progress = { animatedProgress }, modifier = Modifier.size(60.dp), strokeCap = StrokeCap.Round, strokeWidth = 6.dp)
                            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column { Text("Progress Harian", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("$completedTasks dari $totalTasks tugas selesai", style = MaterialTheme.typography.bodyMedium) }
                    }
                }

                // FILTER & SEARCH
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Cari tugas...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val filters = listOf("Semua", "Belum Selesai", "Kerja", "Kuliah", "Hobby")
                        items(filters) { filter ->
                            FilterChip(selected = filterCategory == filter, onClick = { filterCategory = filter }, label = { Text(filter) }, colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), selectedContainerColor = MaterialTheme.colorScheme.primary))
                        }
                    }
                }

                // [BAGIAN BARU: LIST TUGAS / EMPTY STATE]
                if (filteredTodos.isEmpty()) {
                    // [TAMPILAN JIKA KOSONG]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Icon Besar
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Teks Penjelasan
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Tugas tidak ditemukan" else "Belum ada tugas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Coba kata kunci lain" else "Yuk tambah tugas baru!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    // [TAMPILAN JIKA ADA DATA]
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTodos, key = { it.id }) { todo ->
                            SwipeToDeleteItem(item = todo, onDelete = { userData?.userId?.let { viewModel.delete(it, todo.id) } }) {
                                TodoItem(
                                    todo = todo,
                                    onNavigateToEdit = onNavigateToEdit,
                                    onToggle = { userData?.userId?.let { uid -> viewModel.toggle(uid, todo) } },
                                    onDelete = { userData?.userId?.let { uid -> viewModel.delete(uid, todo.id) } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// [HELPER COMPONENTS]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteItem(item: Any, onDelete: () -> Unit, content: @Composable () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { if (it == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false })
    SwipeToDismissBox(state = dismissState, backgroundContent = {
        val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.errorContainer else Color.Transparent
        Box(modifier = Modifier.fillMaxSize().background(color, RoundedCornerShape(16.dp)).padding(16.dp), contentAlignment = Alignment.CenterEnd) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.onErrorContainer) }
    }, content = { content() })
}

@Composable
fun TodoItem(todo: Todo, onNavigateToEdit: (String) -> Unit, onToggle: () -> Unit, onDelete: () -> Unit) {
    val priorityColor = getPriorityColor(todo.priority)
    Card(modifier = Modifier.fillMaxWidth().clickable { onNavigateToEdit(todo.id) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)), border = if (todo.priority == "High" && !todo.isCompleted) BorderStroke(1.dp, priorityColor) else null) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggle) { Icon(imageVector = if (todo.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked, contentDescription = null, tint = if (todo.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray) }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = todo.title, style = MaterialTheme.typography.bodyLarge.copy(textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null, color = if (todo.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) { Text(text = todo.category, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.onSecondaryContainer) }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!todo.isCompleted) { Text(todo.priority, style = MaterialTheme.typography.labelSmall, color = priorityColor, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

fun getPriorityColor(priority: String): Color {
    return when (priority) { "High" -> Color(0xFFE57373); "Medium" -> Color(0xFFFFB74D); "Low" -> Color(0xFF81C784); else -> Color.Gray }
}