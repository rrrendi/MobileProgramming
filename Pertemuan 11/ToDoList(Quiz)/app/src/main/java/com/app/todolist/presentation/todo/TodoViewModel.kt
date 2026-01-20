package com.app.todolist.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.todolist.data.model.Todo
import com.app.todolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos = _todos.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect { _todos.value = it }
        }
    }

    // [UBAH] Tambah parameter category
    fun add(userId: String, title: String, priority: String, category: String) = viewModelScope.launch {
        repository.addTodo(userId, title, priority, category)
    }

    fun toggle(userId: String, todo: Todo) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todo.id, !todo.isCompleted)
    }

    fun updateTitle(userId: String, todoId: String, newTitle: String) =
        viewModelScope.launch {
            repository.updateTodoTitle(userId, todoId, newTitle)
        }

    fun updatePriority(userId: String, todoId: String, priority: String) =
        viewModelScope.launch {
            repository.updateTodoPriority(userId, todoId, priority)
        }

    // [BARU]
    fun updateCategory(userId: String, todoId: String, category: String) =
        viewModelScope.launch {
            repository.updateTodoCategory(userId, todoId, category)
        }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId, todoId)
    }
}