package com.app.todolist.presentation.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TodoList") },
                actions = {
                    userData?.let {
                        Text(it.username ?: "", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                        )
                        IconButton(onClick = onSignOut) {
                            Text("Out", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = todoText,
                    onValueChange = { todoText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tambah tugas baru...") }
                )
                Button(
                    onClick = {
                        if (todoText.isNotBlank()) {
                            userData?.userId?.let { viewModel.add(it, todoText) }
                            todoText = ""
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Tambah")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(todos) { todo ->
                    ListItem(
                        modifier = Modifier.clickable { onNavigateToEdit(todo.id) },
                        headlineContent = { Text(todo.title) },
                        leadingContent = {
                            Checkbox(
                                checked = todo.isCompleted,
                                onCheckedChange = {
                                    userData?.userId?.let { uid ->
                                        viewModel.toggle(uid, todo)
                                    }
                                }
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                userData?.userId?.let { uid ->
                                    viewModel.delete(uid, todo.id)
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    }
}