package com.trilogy.mathlearning.ui.presentation.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.domain.model.User
import com.trilogy.mathlearning.domain.repository.AuthRepository
import com.trilogy.mathlearning.utils.GoogleSignInHelper
import com.trilogy.mathlearning.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialManager: CredentialManager
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<Any>>(UiState.Empty)
    val authState: StateFlow<UiState<Any>> = _authState

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                _authState.value = UiState.Loading
                val idToken = GoogleSignInHelper.fetchIdToken(context, credentialManager)
                _authState.value = authRepository.loginWithGoogle(idToken)
            } catch (e: Exception) {
                _authState.value = UiState.Failure(e.message)
            }
        }
    }

    fun fetchCurrentUser(): UiState<User?> {
        val userState = authRepository.getCurrentUser()
        return userState
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            _authState.value = authRepository.logout()
        }
    }

    fun clearAuthState() {
        _authState.value = UiState.Empty
    }
}