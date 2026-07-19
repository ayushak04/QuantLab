package com.ayush.quantlab.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val initializer: SplashInitializer = DefaultSplashInitializer()
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        onEvent(SplashUiEvent.Started)
    }

    fun onEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.Started -> startInitialization()
        }
    }

    private fun startInitialization() {
        viewModelScope.launch {
            initializer.initialize()
            _uiState.update {
                it.copy(
                    isInitializing = false,
                    isSplashFinished = true
                )
            }
        }
    }
}

interface SplashInitializer {
    suspend fun initialize()
}

class DefaultSplashInitializer : SplashInitializer {
    override suspend fun initialize() {
        delay(SPLASH_DURATION_MS)
    }
}

private const val SPLASH_DURATION_MS = 2_000L
