package com.app.dreamboard.presentation.dreamboard // Sesuaikan package jika beda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dreamboard.data.model.DreamItem
import com.app.dreamboard.data.repository.DreamRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DreamViewModel : ViewModel() {
    private val repository = DreamRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _dreams = MutableStateFlow<List<DreamItem>>(emptyList())
    val dreams: StateFlow<List<DreamItem>> = _dreams

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener { _currentUser.value = it.currentUser; if (it.currentUser != null) loadDreams() }
        loadDreams()
    }

    fun loadDreams() = viewModelScope.launch { _dreams.value = repository.getDreams() }

    fun addDream(title: String, description: String, imageUrl: String, category: String) {
        val dream = DreamItem(title = title, description = description, imageUrl = imageUrl, category = category)
        repository.addDream(dream)
        loadDreams()
    }

    // [UPDATE] Update data LENGKAP termasuk status
    fun updateDream(id: String, title: String, description: String, imageUrl: String, isAchieved: Boolean, category: String) {
        repository.updateDreamData(id, title, description, imageUrl, isAchieved, category)

        // Update Lokal
        _dreams.value = _dreams.value.map {
            if (it.id == id) it.copy(title = title, description = description, imageUrl = imageUrl, isAchieved = isAchieved, category = category)
            else it
        }
    }

    fun toggleAchievedStatus(dream: DreamItem) {
        repository.updateDreamStatus(dream.id, !dream.isAchieved)
        _dreams.value = _dreams.value.map {
            if (it.id == dream.id) it.copy(isAchieved = !it.isAchieved) else it
        }
    }

    fun deleteDream(dreamId: String) {
        repository.deleteDream(dreamId)
        loadDreams()
    }

    fun logout() { repository.signOut() }
}