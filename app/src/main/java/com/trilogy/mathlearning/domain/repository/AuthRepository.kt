package com.trilogy.mathlearning.domain.repository

import com.trilogy.mathlearning.domain.model.User
import com.trilogy.mathlearning.utils.UiState

interface AuthRepository {
    suspend fun loginWithGoogle(idToken: String): UiState<User>
    fun getCurrentUser(): UiState<User?>
    suspend fun logout(): UiState<Unit>
}