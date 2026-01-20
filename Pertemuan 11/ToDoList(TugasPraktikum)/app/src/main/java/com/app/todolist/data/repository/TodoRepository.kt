package com.app.todolist.data.repository

import com.app.todolist.data.model.Todo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TodoRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun getTodoCollection(userId: String) =
        firestore.collection("users").document(userId).collection("todos")

    fun getTodos(userId: String): Flow<List<Todo>> = callbackFlow {
        val subscription = getTodoCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val todos = snapshot.documents.mapNotNull {
                        it.toObject(Todo::class.java)?.copy(id = it.id)
                    }
                    trySend(todos)
                }
            }
        awaitClose { subscription.remove() }
    }

    // [UBAH] Menambahkan parameter priority
    suspend fun addTodo(userId: String, title: String, priority: String) {
        val todo = Todo(title = title, priority = priority)
        getTodoCollection(userId).add(todo).await()
    }

    suspend fun updateTodoStatus(userId: String, todoId: String, isCompleted: Boolean) {
        getTodoCollection(userId).document(todoId).update("isCompleted", isCompleted).await()
    }

    suspend fun updateTodoTitle(userId: String, todoId: String, newTitle: String) {
        getTodoCollection(userId).document(todoId).update("title", newTitle).await()
    }

    // [BARU] Fungsi untuk update priority
    suspend fun updateTodoPriority(userId: String, todoId: String, newPriority: String) {
        getTodoCollection(userId).document(todoId).update("priority", newPriority).await()
    }

    suspend fun deleteTodo(userId: String, todoId: String) {
        getTodoCollection(userId).document(todoId).delete().await()
    }
}