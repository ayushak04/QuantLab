package com.ayush.quantlab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ayush.quantlab.feature.dashboard.DashboardScreen
import com.ayush.quantlab.feature.dashboard.DashboardUiEvent
import com.ayush.quantlab.feature.dashboard.DashboardViewModel
import com.ayush.quantlab.feature.investment.InvestmentScreen
import com.ayush.quantlab.feature.investment.InvestmentUiEvent
import com.ayush.quantlab.feature.investment.InvestmentViewModel
import com.ayush.quantlab.feature.montecarlo.MonteCarloScreen
import com.ayush.quantlab.feature.montecarlo.MonteCarloUiEvent
import com.ayush.quantlab.feature.montecarlo.MonteCarloViewModel
import com.ayush.quantlab.feature.splash.SplashDestination

@Composable
fun QuantLabNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = QuantLabRoute.Splash.route
    ) {
        composable(QuantLabRoute.Splash.route) {
            SplashDestination(
                onSplashFinished = {
                    navController.navigate(QuantLabRoute.Dashboard.route) {
                        popUpTo(QuantLabRoute.Splash.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(QuantLabRoute.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            DashboardScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                    when (event) {
                        DashboardUiEvent.InvestmentSelected -> {
                            navController.navigate(QuantLabRoute.Investment.route)
                        }

                        DashboardUiEvent.MonteCarloSelected -> {
                            navController.navigate(QuantLabRoute.MonteCarlo.route)
                        }
                    }
                }
            )
        }

        composable(QuantLabRoute.Investment.route) {
            val viewModel: InvestmentViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            InvestmentScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                    if (event == InvestmentUiEvent.BackClicked) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(QuantLabRoute.MonteCarlo.route) {
            val viewModel: MonteCarloViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MonteCarloScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                    if (event == MonteCarloUiEvent.BackClicked) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
