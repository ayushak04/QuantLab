package com.ayush.quantlab.feature.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
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
fun DashboardScreen(
    uiState: DashboardUiState,
    onEvent: (DashboardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = QuantBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Header(uiState = uiState)
            SnapshotCard(uiState = uiState)
            FeatureCard(
                feature = uiState.investmentFeature,
                accentBrush = Brush.horizontalGradient(listOf(QuantBlue, QuantGreen)),
                onClick = { onEvent(DashboardUiEvent.InvestmentSelected) }
            )
            FeatureCard(
                feature = uiState.monteCarloFeature,
                accentBrush = Brush.horizontalGradient(listOf(QuantGreen, QuantBlue)),
                onClick = { onEvent(DashboardUiEvent.MonteCarloSelected) }
            )
        }
    }
}

@Composable
private fun Header(uiState: DashboardUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = uiState.appName,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = uiState.headline,
            style = MaterialTheme.typography.titleMedium,
            color = QuantTextSecondary
        )
    }
}

@Composable
private fun SnapshotCard(uiState: DashboardUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = QuantSurface),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Workspace",
                    style = MaterialTheme.typography.labelLarge,
                    color = QuantTextMuted
                )
                Text(
                    text = uiState.portfolioSnapshot,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(QuantSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Q",
                    style = MaterialTheme.typography.titleMedium,
                    color = QuantBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: DashboardFeatureUiModel,
    accentBrush: Brush,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = QuantSurface),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accentBrush)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = feature.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = QuantTextSecondary
                    )
                }
                Spacer(modifier = Modifier.size(14.dp))
                Text(
                    text = feature.actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = QuantBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = feature.metric,
                style = MaterialTheme.typography.labelLarge,
                color = QuantTextMuted
            )
        }
    }
}
