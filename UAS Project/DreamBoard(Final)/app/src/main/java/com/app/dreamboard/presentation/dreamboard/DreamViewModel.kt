package com.app.dreamboard.presentation.dreamboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.dreamboard.data.model.DreamItem
import com.app.dreamboard.data.repository.DreamRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DreamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DreamRepository(application.applicationContext)
    private val auth = FirebaseAuth.getInstance()

    private val _dreams = MutableStateFlow<List<DreamItem>>(emptyList())
    val dreams: StateFlow<List<DreamItem>> = _dreams

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    // [PERBAIKAN] "Semua" dihapus dari sini karena itu bukan kategori, tapi filter
    private val defaultCategories = listOf("Liburan", "Karir", "Gaming", "Ibadah", "Kendaraan", "Lainnya")

    private val _categories = MutableStateFlow(defaultCategories)
    val categories: StateFlow<List<String>> = _categories

    private var categoryJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getThemeFromDevice().collectLatest { _isDarkTheme.value = it }
        }

        auth.addAuthStateListener {
            _currentUser.value = it.currentUser
            if (it.currentUser != null) {
                loadDreams()
                observeCategories()
            }
        }
        if (auth.currentUser != null) { loadDreams(); observeCategories() }
    }

    private fun observeCategories() {
        categoryJob?.cancel()
        categoryJob = viewModelScope.launch {
            repository.getCategoriesStream().collect { customCats ->
                // Gabungkan default + custom
                val combined = (defaultCategories + customCats).distinct()
                _categories.value = combined
            }
        }
    }

    fun addCategory(name: String) = viewModelScope.launch {
        val currentList = _categories.value.toMutableList()
        val exists = currentList.any { it.equals(name, ignoreCase = true) }

        if (!exists) {
            currentList.add(name)
            _categories.value = currentList
            val customOnly = currentList.filter { it !in defaultCategories }
            repository.saveCustomCategories(customOnly)
        }
    }

    fun deleteCategory(name: String) = viewModelScope.launch {
        if (name in defaultCategories) return@launch

        val currentList = _categories.value.toMutableList()
        currentList.remove(name)
        _categories.value = currentList

        val customOnly = currentList.filter { it !in defaultCategories }
        repository.saveCategoriesToCloud(customOnly)
    }

    fun toggleTheme(isDark: Boolean) = viewModelScope.launch { repository.saveThemeToDevice(isDark) }
    fun loadDreams() = viewModelScope.launch { _dreams.value = repository.getDreams() }

    fun addDream(title: String, desc: String, url: String, cat: String) {
        repository.addDream(DreamItem(title = title, description = desc, imageUrl = url, category = cat))
        loadDreams()
    }

    fun updateDream(id: String, title: String, desc: String, url: String, status: Boolean, cat: String) {
        repository.updateDreamData(id, title, desc, url, status, cat)
        _dreams.value = _dreams.value.map {
            if (it.id == id) it.copy(title = title, description = desc, imageUrl = url, isAchieved = status, category = cat) else it
        }
    }

    fun deleteDream(id: String) {
        repository.deleteDream(id)
        loadDreams()
    }

    fun logout() { repository.signOut() }
}