package com.app.dreamboard.presentation.sign_in

data class SignInState(
    val isSignInSuccessfull: Boolean = false,
    val signInError: String? = null,
    val isLoading: Boolean = false // [BARU] Tambahkan ini agar tidak error
)