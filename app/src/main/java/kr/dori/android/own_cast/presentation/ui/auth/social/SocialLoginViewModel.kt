package kr.dori.android.own_cast.presentation.ui.auth.social

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SocialLoginViewModel: ViewModel() {
    private val _socialLoginUiState = MutableStateFlow<SocialLoginUiState>(SocialLoginUiState.Idle)
    val socialLoginUiState = _socialLoginUiState.asStateFlow()

    fun kakaoLogin() {
        _socialLoginUiState.value = SocialLoginUiState.KakaoLogin
    }

    fun kakaoLoginSuccess() {
        _socialLoginUiState.tryEmit(SocialLoginUiState.KakaoLoginSuccess)
    }

    fun kakaoLoginFail() {
        _socialLoginUiState.tryEmit(SocialLoginUiState.LoginFail)
    }

    fun setUiStateIdle() {
        _socialLoginUiState.tryEmit(SocialLoginUiState.Idle)
    }
}