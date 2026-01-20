package com.app.dreamboard.data.repository

import com.app.dreamboard.data.model.DreamItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DreamRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid ?: ""
    private val userDreamsCollection get() = firestore.collection("users").document(userId).collection("dreams")

    suspend fun getDreams(): List<DreamItem> {
        if (userId.isEmpty()) return emptyList()
        return try {
            val snapshot = userDreamsCollection.get().await()
            snapshot.toObjects(DreamItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addDream(dream: DreamItem) {
        if (userId.isEmpty()) return
        val docRef = userDreamsCollection.document()
        docRef.set(dream.copy(id = docRef.id))
    }

    fun updateDreamStatus(dreamId: String, isAchieved: Boolean) {
        if (userId.isEmpty()) return
        userDreamsCollection.document(dreamId).update("isAchieved", isAchieved)
    }

    // [UPDATE] Menerima Status (isAchieved) agar tersimpan permanen saat Edit
    fun updateDreamData(dreamId: String, newTitle: String, newDesc: String, newUrl: String, newStatus: Boolean, newCategory: String) {
        if (userId.isEmpty()) return
        val updates = mapOf(
            "title" to newTitle,
            "description" to newDesc,
            "imageUrl" to newUrl,
            "isAchieved" to newStatus, // Simpan status juga!
            "category" to newCategory
        )
        userDreamsCollection.document(dreamId).update(updates)
    }

    fun deleteDream(dreamId: String) {
        if (userId.isEmpty()) return
        userDreamsCollection.document(dreamId).delete()
    }

    fun signOut() {
        auth.signOut()
    }
}