package com.trilogy.mathlearning.ui.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.AuthRepository
import com.trilogy.mathlearning.domain.model.LoginDto
import com.trilogy.mathlearning.domain.model.RegisterDto
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import com.trilogy.mathlearning.utils.tokenApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<Any>>(UiState.Empty)
    val authState: StateFlow<UiState<Any>> = _authState

    private val _registerInfo = MutableStateFlow<LoginDto?>(null)
    val registerInfo: StateFlow<LoginDto?> = _registerInfo


    fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            val response = authRepository.login(
                LoginDto(
                    email= email,
                    password = password
                )
            )
            when (response) {
                is NetworkResource.Success -> {
                    tokenApi = response.data.accessToken
//                    myUser = response.data.user
                    _authState.value = UiState.Success(response.data)
                }

                is NetworkResource.NetworkException -> {
                    _authState.value = UiState.Failure(response.message)
                }

                is NetworkResource.Error -> {
                    _authState.value = UiState.Failure(response.message)
                }
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
    ) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            val response = authRepository.registerUser(
                RegisterDto(
                    name = name,
                    password = password,
                    email = email
                )
            )
            when (response) {
                is NetworkResource.Success -> {
                    _registerInfo.value = LoginDto(
                        email = email,
                        password = password
                    )

                    _authState.value = UiState.Success(response.data)
                }

                is NetworkResource.NetworkException -> {
                    _authState.value = UiState.Failure(response.message)
                }

                is NetworkResource.Error -> {
                    _authState.value = UiState.Failure(response.message)
                }
            }
        }
    }
}