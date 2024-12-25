package com.github.composemusic.route.login.pwdlogin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.bean.pwdlogin.PwdLoginBean
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.parm.Constants
import com.github.composemusic.tool.SharedPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PwdLoginViewModel @Inject constructor(
    private val service: MusicApiService
) : ViewModel() {

    // UI State
    data class LoginUiState(
        val isLoading: Boolean = false,
        val username: String = "",
        val password: String = "",
        val showPassword: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = mutableStateOf(LoginUiState())
    val uiState: State<LoginUiState> = _uiState

    private val _userStatus = mutableStateOf(
        PwdLoginUIStatus(
            text = "",
            label = "Username",
            hint = "Phone number or email",
            isShowPwd = false
        )
    )
    val userStatus: State<PwdLoginUIStatus> = _userStatus

    private val _passwordStatus = mutableStateOf(
        PwdLoginUIStatus(
            text = "",
            label = "Password",
            hint = "Enter your password",
            isShowPwd = false
        )
    )
    val passwordStatus: State<PwdLoginUIStatus> = _passwordStatus

    private val _eventFlow = MutableSharedFlow<PwdLoginStatus>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val emailEnd = "@163.com"

    fun onEvent(event: PwdLoginEvent) {
        viewModelScope.launch {
            when (event) {
                is PwdLoginEvent.PwdLogin -> handleLogin()
                is PwdLoginEvent.ForgetPassword -> handleForgetPassword()
                is PwdLoginEvent.ChangeUserName -> updateUsername(event.username)
                is PwdLoginEvent.ChangePassword -> updatePassword(event.password)
                is PwdLoginEvent.ChangePwdStatus -> togglePasswordVisibility(event.isShowPwd)
            }
        }
    }

    private fun updateUsername(username: String) {
        _userStatus.value = userStatus.value.copy(text = username)
        _uiState.value = _uiState.value.copy(
            username = username,
            errorMessage = null
        )
    }

    private fun updatePassword(password: String) {
        _passwordStatus.value = passwordStatus.value.copy(text = password)
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    private fun togglePasswordVisibility(currentValue: Boolean) {
        _passwordStatus.value = passwordStatus.value.copy(isShowPwd = !currentValue)
        _uiState.value = _uiState.value.copy(showPassword = !currentValue)
    }

    private suspend fun handleLogin() {
        if (_userStatus.value.text.isEmpty() || _passwordStatus.value.text.isEmpty()) {
            _eventFlow.emit(PwdLoginStatus.LoginEmpty)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            if (_userStatus.value.text.contains(emailEnd)) {
                handleEmailLogin()
            } else {
                handlePhoneLogin()
            }
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private suspend fun handleForgetPassword() {
        _eventFlow.emit(PwdLoginStatus.ForgetPassword)
    }

    private suspend fun handlePhoneLogin() {
        when (val response = baseApiCall {
            service.getPhoneLogin(_userStatus.value.text, _passwordStatus.value.text)
        }) {
            is RemoteResult.Success -> processLoginResponse(response.data)
            is RemoteResult.Error -> handleLoginError(response.exception)
        }
    }

    private suspend fun handleEmailLogin() {
        when (val response = baseApiCall {
            service.getEmailLogin(_userStatus.value.text, _passwordStatus.value.text)
        }) {
            is RemoteResult.Success -> processLoginResponse(response.data)
            is RemoteResult.Error -> handleLoginError(response.exception)
        }
    }

    private suspend fun processLoginResponse(data: PwdLoginBean) {
        if (data.code != 200) {
            _eventFlow.emit(PwdLoginStatus.LoginFailed)
            return
        }

        with(SharedPreferencesUtil.instance) {
            putValue(APP.context, Constants.Cookie, data.cookie!!)
            putValue(APP.context, Constants.UserId, data.account.id)
        }
        _eventFlow.emit(PwdLoginStatus.LoginSuccess)
    }

    private suspend fun handleLoginError(exception: Exception) {
        _eventFlow.emit(PwdLoginStatus.NetworkFailed(exception.message.toString()))
        _uiState.value = _uiState.value.copy(
            errorMessage = exception.message
        )
    }
}