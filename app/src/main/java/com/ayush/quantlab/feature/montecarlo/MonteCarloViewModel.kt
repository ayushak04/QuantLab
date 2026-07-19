package com.ayush.quantlab.feature.montecarlo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MonteCarloViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MonteCarloUiState())
    val uiState: StateFlow<MonteCarloUiState> = _uiState.asStateFlow()

    fun onEvent(event: MonteCarloUiEvent) {
        when (event) {
            MonteCarloUiEvent.BackClicked -> Unit
            is MonteCarloUiEvent.StartingCapitalChanged -> _uiState.update {
                it.copy(startingCapital = event.value)
            }

            is MonteCarloUiEvent.MonthlyContributionChanged -> _uiState.update {
                it.copy(monthlyContribution = event.value)
            }

            is MonteCarloUiEvent.ExpectedReturnChanged -> _uiState.update {
                it.copy(expectedReturn = event.value)
            }

            is MonteCarloUiEvent.VolatilityChanged -> _uiState.update {
                it.copy(volatility = event.value)
            }

            is MonteCarloUiEvent.YearsChanged -> _uiState.update {
                it.copy(years = event.value)
            }
        }

        // Future milestone: invoke the Retrofit client here to call the FastAPI
        // Monte Carlo simulation endpoint, then map the response into MonteCarloUiState.
    }
}
