package com.ayush.quantlab.navigation

sealed class QuantLabRoute(val route: String) {
    data object Splash : QuantLabRoute("splash")
    data object Dashboard : QuantLabRoute("dashboard")
    data object Investment : QuantLabRoute("investment")
    data object MonteCarlo : QuantLabRoute("monte_carlo")
}
