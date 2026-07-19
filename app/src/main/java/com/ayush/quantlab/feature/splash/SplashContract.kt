package com.ayush.quantlab.feature.splash

data class SplashUiState(
    val isInitializing: Boolean = true,
    val isSplashFinished: Boolean = false
)

sealed class SplashUiEvent {
    data object Started : SplashUiEvent()
}
