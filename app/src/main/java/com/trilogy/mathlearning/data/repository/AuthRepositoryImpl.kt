package com.trilogy.mathlearning.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.trilogy.mathlearning.domain.model.User
import com.trilogy.mathlearning.domain.model.toUser
import com.trilogy.mathlearning.domain.repository.AuthRepository
import com.trilogy.mathlearning.utils.UiState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): UiState<User> {
        try {
            val cred = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(cred).await()
            return UiState.Success(result.user!!.toUser())
        } catch (e: Exception) {
            return UiState.Failure(e.message)
        }
    }

    override fun getCurrentUser(): UiState<User?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                UiState.Success(firebaseUser.toUser())
            } else {
                UiState.Success(null)
            }
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "An error occurred while retrieving the current user")
        }
    }

    override suspend fun logout(): UiState<Unit> {
        return try {
            firebaseAuth.signOut()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "An error occurred during logout")
        }
    }

}