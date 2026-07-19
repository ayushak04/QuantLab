package com.ayush.quantlab.feature.dashboard

data class DashboardFeatureUiModel(
    val title: String,
    val subtitle: String,
    val metric: String,
    val actionLabel: String
)

data class DashboardUiState(
    val appName: String = "QuantLab",
    val headline: String = "Research-grade wealth tools",
    val portfolioSnapshot: String = "MVP Lab",
    val investmentFeature: DashboardFeatureUiModel = DashboardFeatureUiModel(
        title = "Investment Growth",
        subtitle = "Model lump sum, SIP, inflation and yearly compounding.",
        metric = "Live calculator",
        actionLabel = "Open"
    ),
    val monteCarloFeature: DashboardFeatureUiModel = DashboardFeatureUiModel(
        title = "Monte Carlo",
        subtitle = "Stress-test outcomes with simulated market paths.",
        metric = "Preview mode",
        actionLabel = "Preview"
    )
)

sealed class DashboardUiEvent {
    data object InvestmentSelected : DashboardUiEvent()
    data object MonteCarloSelected : DashboardUiEvent()
}
