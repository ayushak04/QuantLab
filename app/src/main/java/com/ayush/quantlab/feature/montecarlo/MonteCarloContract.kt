package com.ayush.quantlab.feature.montecarlo

data class MonteCarloStatisticUiModel(
    val label: String,
    val value: String
)

data class MonteCarloUiState(
    val startingCapital: String = "500000",
    val monthlyContribution: String = "20000",
    val expectedReturn: String = "11",
    val volatility: String = "18",
    val years: String = "15",
    val confidenceStats: List<MonteCarloStatisticUiModel> = listOf(
        MonteCarloStatisticUiModel("P10 downside", "Rs 31.8L"),
        MonteCarloStatisticUiModel("Median outcome", "Rs 82.4L"),
        MonteCarloStatisticUiModel("P90 upside", "Rs 1.74Cr"),
        MonteCarloStatisticUiModel("Success probability", "72%")
    )
)

sealed class MonteCarloUiEvent {
    data object BackClicked : MonteCarloUiEvent()
    data class StartingCapitalChanged(val value: String) : MonteCarloUiEvent()
    data class MonthlyContributionChanged(val value: String) : MonteCarloUiEvent()
    data class ExpectedReturnChanged(val value: String) : MonteCarloUiEvent()
    data class VolatilityChanged(val value: String) : MonteCarloUiEvent()
    data class YearsChanged(val value: String) : MonteCarloUiEvent()
}
