package kr.dori.android.own_cast.presentation.ui.auth.social

sealed interface SocialLoginUiState {
    object KakaoLogin: SocialLoginUiState
    object KakaoLoginSuccess: SocialLoginUiState
    object LoginFail: SocialLoginUiState
    object Idle: SocialLoginUiState
}