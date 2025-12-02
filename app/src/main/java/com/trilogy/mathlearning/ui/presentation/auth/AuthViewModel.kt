package com.trilogy.mathlearning.ui.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.AuthRepository
import com.trilogy.mathlearning.data.repository.UserRepository
import com.trilogy.mathlearning.domain.model.ActiveUserDto
import com.trilogy.mathlearning.domain.model.ForgotPasswordDto
import com.trilogy.mathlearning.domain.model.LoginDto
import com.trilogy.mathlearning.domain.model.RegisterDto
import com.trilogy.mathlearning.domain.model.ResetPasswordDto
import com.trilogy.mathlearning.domain.model.ResDto
import com.trilogy.mathlearning.domain.model.UserResDto
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

    private val _logoutState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val logoutState: StateFlow<UiState<Unit>> = _logoutState

    private val _forgotPasswordState = MutableStateFlow<UiState<ResDto>>(UiState.Empty)
    val forgotPasswordState: StateFlow<UiState<ResDto>> = _forgotPasswordState

    private val _resetPasswordState = MutableStateFlow<UiState<ResDto>>(UiState.Empty)
    val resetPasswordState: StateFlow<UiState<ResDto>> = _resetPasswordState

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
                    _registerInfo.value = LoginDto(email = email, password = password)
                    _authState.value = UiState.Success(res.data)
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
                    _authState.value = UiState.Success(res.data)
                }
                is NetworkResource.NetworkException -> _authState.value = UiState.Failure(res.message)
                is NetworkResource.Error -> _authState.value = UiState.Failure(res.message)
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = UiState.Loading
            when (val res = authRepository.forgotPassword(ForgotPasswordDto(email))) {
                is NetworkResource.Success -> _forgotPasswordState.value = UiState.Success(res.data)
                is NetworkResource.NetworkException -> _forgotPasswordState.value = UiState.Failure(res.message)
                is NetworkResource.Error -> _forgotPasswordState.value = UiState.Failure(res.message)
            }
        }
    }

    fun resetPassword(code: String, newPassword: String, confirmNewPassword: String) {
        viewModelScope.launch {
            _resetPasswordState.value = UiState.Loading
            val dto = ResetPasswordDto(
                newPassword = newPassword,
                confirmNewPassword = confirmNewPassword,
                forgotPasswordCode = code
            )
            when (val res = authRepository.resetPassword(dto)) {
                is NetworkResource.Success -> _resetPasswordState.value = UiState.Success(res.data)
                is NetworkResource.NetworkException -> _resetPasswordState.value = UiState.Failure(res.message)
                is NetworkResource.Error -> _resetPasswordState.value = UiState.Failure(res.message)
            }
        }
    }

    fun clearAuthState() {
        _authState.value = UiState.Empty
    }

    fun clearRegisterInfo() {
        _registerInfo.value = null
    }

    fun clearForgotPasswordState() {
        _forgotPasswordState.value = UiState.Empty
    }

    fun clearResetPasswordState() {
        _resetPasswordState.value = UiState.Empty
    }

    fun getUser() {
        viewModelScope.launch {
            when (val res = userRepository.getUser()) {
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

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = UiState.Loading
            tokenApi = null
            myUser = null
            _userInfo.value = null
            _registerInfo.value = null
            _authState.value = UiState.Empty
            _forgotPasswordState.value = UiState.Empty
            _resetPasswordState.value = UiState.Empty
            _logoutState.value = UiState.Success(Unit)
        }
    }
}
