package com.trilogy.mathlearning.ui.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.AuthRepository
import com.trilogy.mathlearning.data.repository.UserRepository
import com.trilogy.mathlearning.domain.model.*
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import com.trilogy.mathlearning.utils.myUser
import com.trilogy.mathlearning.utils.tokenApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<Any>>(UiState.Empty)
    val authState: StateFlow<UiState<Any>> = _authState

    private val _userInfo = MutableStateFlow<UserResDto?>(null)
    val userInfo: StateFlow<UserResDto?> = _userInfo

    private val _registerInfo = MutableStateFlow<LoginDto?>(null)
    val registerInfo: StateFlow<LoginDto?> = _registerInfo

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val res = authRepository.login(LoginDto(email = email, password = password))) {
                is NetworkResource.Success -> {
                    tokenApi = res.data.accessToken
                    Log.i("DanMinh123", tokenApi!!)
                    getUser()
                    _authState.value = UiState.Success(res.data)
                }
                is NetworkResource.NetworkException -> _authState.value = UiState.Failure(res.message)
                is NetworkResource.Error -> _authState.value = UiState.Failure(res.message)
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val res = authRepository.registerUser(RegisterDto(name = name, email = email, password = password))) {
                is NetworkResource.Success -> {
                    // Lưu lại email/password để dùng tiếp (nếu cần auto-fill)
                    _registerInfo.value = LoginDto(email = email, password = password)
                    _authState.value = UiState.Success(res.data) // UserDto
                }
                is NetworkResource.NetworkException -> _authState.value = UiState.Failure(res.message)
                is NetworkResource.Error -> _authState.value = UiState.Failure(res.message)
            }
        }
    }

    fun activate(email: String, code: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val res = authRepository.activateUser(ActiveUserDto(email = email, confirmCode = code))) {
                is NetworkResource.Success -> {
                    _authState.value = UiState.Success(res.data) // UserDto
                }
                is NetworkResource.NetworkException -> _authState.value = UiState.Failure(res.message)
                is NetworkResource.Error -> _authState.value = UiState.Failure(res.message)
            }
        }
    }

    fun clearAuthState() {
        _authState.value = UiState.Empty
    }

    fun getUser(){
        viewModelScope.launch {
            when(val res = userRepository.getUser()){
                is NetworkResource.Success -> {
                    _userInfo.value = res.data
                    myUser = res.data
                    Log.d("getUser", res.data.toString())
                }
                is NetworkResource.NetworkException -> res.message?.let { Log.d("NetworkException", it) }

                is NetworkResource.Error -> res.message?.let { Log.d("Error123", it) }
            }
        }

    }
}
