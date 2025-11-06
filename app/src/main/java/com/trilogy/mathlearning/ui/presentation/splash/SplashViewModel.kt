package com.trilogy.mathlearning.ui.presentation.splash

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.domain.repository.DataStoreRepository
import com.trilogy.mathlearning.ui.presentation.navigation.Screen
import com.trilogy.mathlearning.utils.tokenApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf(Screen.Splash.route)
    val startDestination: State<String> = _startDestination

    init {
        viewModelScope.launch {
            repository.readOnBoardingState().collect { completed ->
                delay(1500)
                if (completed) {
                    if(tokenApi != null) {
                        Log.i("TAG123", tokenApi ?: "")
                        _startDestination.value = Screen.HomeRoot.route
                    } else {
                        _startDestination.value = Screen.Login.route
                    }
                } else {
                    _startDestination.value = Screen.Welcome.route
                }
            }
            _isLoading.value = false
        }
    }

}