package com.app.todolist.presentation.todo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var showProfileDialog by remember { mutableStateOf(false) }

    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    // --- DIALOG POPUP PROFIL ---
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            // [UBAH DI SINI] Membuat background dialog menjadi agak transparan
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),

            title = { Text("Profil Pengguna", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (userData?.profilePictureUrl != null) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(100.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(userData?.username ?: "Pengguna", style = MaterialTheme.typography.headlineSmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileDialog = false; onSignOut() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar Akun")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Tutup") }
            }
        )
    }

    // --- ROOT LAYOUT ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // [1. GAMBAR BACKGROUND BODY]
        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // [2. SCAFFOLD TRANSPARAN]
        Scaffold(
            containerColor = Color.Transparent,

            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Halo, ${userData?.username?.split(" ")?.firstOrNull() ?: "User"}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Tugas tersisa: ${todos.count { !it.isCompleted }}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    actions = {
                        userData?.let {
                            AsyncImage(
                                model = it.profilePictureUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { showProfileDialog = true },
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        // TopBar Transparan (0.7f)
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->

            // CONTENT
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // INPUT SECTION
                Surface(
                    shadowElevation = 0.dp,
                    // Input Area Transparan (0.7f)
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("High", "Medium", "Low").forEach { priority ->
                                FilterChip(
                                    selected = selectedPriority == priority,
                                    onClick = { selectedPriority = priority },
                                    label = { Text(priority) },
                                    leadingIcon = if (selectedPriority == priority) {
                                        { Icon(Icons.Outlined.CheckCircle, null, Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = getPriorityColor(priority).copy(alpha = 0.2f),
                                        selectedLabelColor = getPriorityColor(priority)
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = todoText,
                                onValueChange = { todoText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Apa rencana hari ini?") },
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (todoText.isNotBlank()) {
                                        userData?.userId?.let { uid -> viewModel.add(uid, todoText, selectedPriority) }
                                        todoText = ""
                                    }
                                })
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FloatingActionButton(
                                onClick = {
                                    if (todoText.isNotBlank()) {
                                        userData?.userId?.let { uid -> viewModel.add(uid, todoText, selectedPriority) }
                                        todoText = ""
                                    }
                                },
                                containerColor = MaterialTheme.colorScheme.primary,
                                elevation = FloatingActionButtonDefaults.elevation(0.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }
                }

                // LIST TUGAS
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todos, key = { it.id }) { todo ->
                        TodoItem(
                            todo = todo,
                            onNavigateToEdit = onNavigateToEdit,
                            onToggle = { userData?.userId?.let { viewModel.toggle(it, todo) } },
                            onDelete = { userData?.userId?.let { viewModel.delete(it, todo.id) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onNavigateToEdit: (String) -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = getPriorityColor(todo.priority)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToEdit(todo.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // Item List juga agak transparan (0.9f)
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        border = if (todo.priority == "High" && !todo.isCompleted) BorderStroke(1.dp, priorityColor) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (todo.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if(todo.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                        color = if(todo.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                if(!todo.isCompleted) {
                    Text(todo.priority, style = MaterialTheme.typography.labelSmall, color = priorityColor, fontWeight = FontWeight.Bold)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun getPriorityColor(priority: String): Color {
    return when(priority) {
        "High" -> Color(0xFFE57373)
        "Medium" -> Color(0xFFFFB74D)
        "Low" -> Color(0xFF81C784)
        else -> Color.Gray
    }
}