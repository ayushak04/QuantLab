package com.ayush.quantlab.feature.montecarlo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ayush.quantlab.ui.theme.QuantBackground
import com.ayush.quantlab.ui.theme.QuantBlue
import com.ayush.quantlab.ui.theme.QuantBorder
import com.ayush.quantlab.ui.theme.QuantGreen
import com.ayush.quantlab.ui.theme.QuantSurface
import com.ayush.quantlab.ui.theme.QuantSurfaceElevated
import com.ayush.quantlab.ui.theme.QuantTextMuted
import com.ayush.quantlab.ui.theme.QuantTextSecondary

@Composable
fun MonteCarloScreen(
    uiState: MonteCarloUiState,
    onEvent: (MonteCarloUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = QuantBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MonteCarloHeader(onBack = { onEvent(MonteCarloUiEvent.BackClicked) })
            MonteCarloInputs(uiState = uiState, onEvent = onEvent)
            HistogramPlaceholder()
            StatsPanel(stats = uiState.confidenceStats)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun MonteCarloHeader(onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack) {
            Text(text = "Back", color = QuantBlue)
        }
        Text(
            text = "Monte Carlo",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Scenario inputs and placeholder statistics for the simulation engine.",
            style = MaterialTheme.typography.bodyLarge,
            color = QuantTextSecondary
        )
    }
}

@Composable
private fun MonteCarloInputs(
    uiState: MonteCarloUiState,
    onEvent: (MonteCarloUiEvent) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = QuantSurface),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Simulation setup",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            MonteCarloInputField(
                label = "Starting capital",
                value = uiState.startingCapital,
                suffix = "Rs",
                onValueChange = { onEvent(MonteCarloUiEvent.StartingCapitalChanged(it)) }
            )
            MonteCarloInputField(
                label = "Monthly contribution",
                value = uiState.monthlyContribution,
                suffix = "Rs",
                onValueChange = { onEvent(MonteCarloUiEvent.MonthlyContributionChanged(it)) }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MonteCarloInputField(
                    label = "Return",
                    value = uiState.expectedReturn,
                    suffix = "%",
                    onValueChange = { onEvent(MonteCarloUiEvent.ExpectedReturnChanged(it)) },
                    modifier = Modifier.weight(1f)
                )
                MonteCarloInputField(
                    label = "Volatility",
                    value = uiState.volatility,
                    suffix = "%",
                    onValueChange = { onEvent(MonteCarloUiEvent.VolatilityChanged(it)) },
                    modifier = Modifier.weight(1f)
                )
            }
            MonteCarloInputField(
                label = "Years",
                value = uiState.years,
                suffix = "years",
                onValueChange = { onEvent(MonteCarloUiEvent.YearsChanged(it)) },
                keyboardType = KeyboardType.Number
            )
        }
    }
}

@Composable
private fun HistogramPlaceholder() {
    Card(
        colors = CardDefaults.cardColors(containerColor = QuantSurface),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Outcome distribution",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                QuantSurfaceElevated,
                                QuantSurface
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Histogram placeholder",
                    style = MaterialTheme.typography.bodyLarge,
                    color = QuantTextMuted
                )
            }
        }
    }
}

@Composable
private fun StatsPanel(stats: List<MonteCarloStatisticUiModel>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = QuantSurface),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Fake statistical output",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            stats.chunked(2).forEach { rowStats ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowStats.forEach { stat ->
                        StatisticTile(
                            statistic = stat,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowStats.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticTile(
    statistic: MonteCarloStatisticUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = QuantSurfaceElevated),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = statistic.label,
                style = MaterialTheme.typography.labelMedium,
                color = QuantTextMuted
            )
            Text(
                text = statistic.value,
                style = MaterialTheme.typography.titleMedium,
                color = if (statistic.label.contains("Success")) QuantGreen else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MonteCarloInputField(
    label: String,
    value: String,
    suffix: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        suffix = { Text(text = suffix, color = QuantTextMuted) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = QuantSurfaceElevated,
            unfocusedContainerColor = QuantSurfaceElevated,
            focusedBorderColor = QuantBlue,
            unfocusedBorderColor = QuantBorder,
            focusedLabelColor = QuantBlue,
            unfocusedLabelColor = QuantTextMuted,
            cursorColor = QuantBlue
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    )
}
