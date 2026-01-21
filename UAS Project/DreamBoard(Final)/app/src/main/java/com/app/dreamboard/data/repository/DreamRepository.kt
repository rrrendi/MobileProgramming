package com.app.dreamboard.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.app.dreamboard.data.model.DreamItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DreamRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid ?: ""

    // --- TEMA (DEVICE) ---
    private val THEME_KEY = booleanPreferencesKey("is_dark_mode")

    suspend fun saveThemeToDevice(isDark: Boolean) {
        context.dataStore.edit { it[THEME_KEY] = isDark }
    }

    fun getThemeFromDevice(): Flow<Boolean> = context.dataStore.data
        .map { it[THEME_KEY] ?: false }

    // --- KATEGORI (CLOUD) ---
    private fun getCategoryDoc() = firestore.collection("users").document(userId).collection("settings").document("categories")

    suspend fun saveCustomCategories(categories: List<String>) {
        if (userId.isEmpty()) return
        withContext(Dispatchers.IO) {
            try {
                // [PERBAIKAN] Hapus merge, gunakan set langsung (overwrite list)
                // Ini mencegah konflik data array di Firestore
                val data = mapOf("list" to categories)
                getCategoryDoc().set(data).await()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun saveCategoriesToCloud(categories: List<String>) {
        // Alias function untuk kompatibilitas
        if (userId.isNotEmpty()) {
            val data = mapOf("list" to categories)
            getCategoryDoc().set(data)
        }
    }

    fun getCategoriesStream(): Flow<List<String>> = callbackFlow {
        if (userId.isEmpty()) { trySend(emptyList()); close(); return@callbackFlow }
        val listener = getCategoryDoc().addSnapshotListener { snapshot, _ ->
            val list = snapshot?.get("list") as? List<String> ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    // --- CRUD IMPIAN ---
    private fun getDreamsCollection() = firestore.collection("users").document(userId).collection("dreams")

    suspend fun getDreams(): List<DreamItem> {
        if (userId.isEmpty()) return emptyList()
        return try {
            val snapshot = getDreamsCollection().get().await()
            snapshot.toObjects(DreamItem::class.java)
        } catch (e: Exception) { emptyList() }
    }

    fun addDream(dream: DreamItem) {
        if (userId.isEmpty()) return
        val doc = getDreamsCollection().document()
        doc.set(dream.copy(id = doc.id))
    }

    fun updateDreamData(id: String, title: String, desc: String, url: String, status: Boolean, cat: String) {
        if (userId.isEmpty()) return
        val updates = mapOf("title" to title, "description" to desc, "imageUrl" to url, "isAchieved" to status, "category" to cat)
        getDreamsCollection().document(id).update(updates)
    }

    fun deleteDream(id: String) {
        if (userId.isEmpty()) return
        getDreamsCollection().document(id).delete()
    }

    fun signOut() { auth.signOut() }
}