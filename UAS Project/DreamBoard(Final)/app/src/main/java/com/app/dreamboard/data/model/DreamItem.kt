package com.app.dreamboard.data.model

import com.google.firebase.firestore.PropertyName

data class DreamItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "Lainnya",
    // [PERBAIKAN UTAMA] Memaksa Firebase membaca field "isAchieved" dengan benar
    @get:PropertyName("isAchieved")
    val isAchieved: Boolean = false
)