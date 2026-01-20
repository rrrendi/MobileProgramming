package com.app.dreamboard.presentation.sign_in

import androidx.lifecycle.ViewModel
import com.app.dreamboard.data.model.SignInResult
import com.app.dreamboard.presentation.sign_in.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessfull = result.data != null,
            signInError = result.errorMessage,
            isLoading = false // [BARU] Matikan loading setelah selesai
        ) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    // [BARU] Fungsi untuk menyalakan loading sebelum proses login dimulai
    fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }
}