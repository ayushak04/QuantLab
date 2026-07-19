package com.ayush.quantlab.feature.investment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayush.quantlab.ui.theme.QuantBackground
import com.ayush.quantlab.ui.theme.QuantBlue
import com.ayush.quantlab.ui.theme.QuantBorder
import com.ayush.quantlab.ui.theme.QuantGreen
import com.ayush.quantlab.ui.theme.QuantSurface
import com.ayush.quantlab.ui.theme.QuantSurfaceElevated
import com.ayush.quantlab.ui.theme.QuantTextMuted
import com.ayush.quantlab.ui.theme.QuantTextSecondary
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Composable
fun InvestmentScreen(
    uiState: InvestmentUiState,
    onEvent: (InvestmentUiEvent) -> Unit,
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
            ScreenHeader(
                title = "Investment Growth",
                subtitle = "Compound wealth forecast with clean portfolio calculators.",
                onBack = { onEvent(InvestmentUiEvent.BackClicked) }
            )
            InvestmentTabs(
                selectedTabIndex = uiState.selectedTabIndex,
                onTabSelected = { onEvent(InvestmentUiEvent.TabSelected(it)) }
            )
            if (uiState.selectedTabIndex == REAL_WEALTH_TAB_INDEX) {
                Text(
                    text = "Forecast your most realistic future wealth by factoring in real-world market variables. Expand 'Advanced Settings' to include Step-Up SIPs, Expense Ratios, and Taxes for maximum precision, or simply use the default inflation adjustments.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = QuantTextSecondary
                )
            }
            ResultSummary(uiState = uiState, onEvent = onEvent)
            GrowthChartCard(uiState = uiState)
            InputPanel(uiState = uiState, onEvent = onEvent)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    uiState.activeTooltip?.let { tooltip ->
        AlertDialog(
            onDismissRequest = { onEvent(InvestmentUiEvent.TooltipDismissed) },
            confirmButton = {
                TextButton(onClick = { onEvent(InvestmentUiEvent.TooltipDismissed) }) {
                    Text(text = "Got it", color = QuantBlue)
                }
            },
            title = {
                Text(
                    text = tooltip.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = tooltip.message,
                    color = QuantTextSecondary
                )
            },
            containerColor = QuantSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = QuantTextSecondary
        )
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack) {
            Text(text = "Back", color = QuantBlue)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = QuantTextSecondary
        )
    }
}

@Composable
private fun InvestmentTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("SIP", "Lump Sum", "Real Wealth")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = QuantSurface,
        contentColor = QuantBlue,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = QuantBlue
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                selectedContentColor = QuantBlue,
                unselectedContentColor = QuantTextMuted,
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
private fun ResultSummary(
    uiState: InvestmentUiState,
    onEvent: (InvestmentUiEvent) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = QuantSurface),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState.selectedTabIndex) {
                SIP_TAB_INDEX -> SimpleResultCards(
                    investedAmount = uiState.sipUiState.investedAmount,
                    estimatedProfit = uiState.sipUiState.estimatedProfit,
                    totalAmount = uiState.sipUiState.totalAmount
                )

                LUMP_SUM_TAB_INDEX -> SimpleResultCards(
                    investedAmount = uiState.lumpSumUiState.investedAmount,
                    estimatedProfit = uiState.lumpSumUiState.estimatedProfit,
                    totalAmount = uiState.lumpSumUiState.totalAmount
                )

                else -> RealWealthResultCards(
                    state = uiState.realWealthUiState,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun SimpleResultCards(
    investedAmount: Double,
    estimatedProfit: Double,
    totalAmount: Double
) {
    Text(
        text = "Total Amount",
        style = MaterialTheme.typography.labelLarge,
        color = QuantTextMuted
    )
    Text(
        text = formatCurrency(totalAmount),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricTile(
            label = "Invested Amount",
            value = formatCurrency(investedAmount),
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            label = "Estimated Profit",
            value = formatCurrency(estimatedProfit),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RealWealthResultCards(
    state: RealWealthUiState,
    onEvent: (InvestmentUiEvent) -> Unit
) {
    val projectedGain = state.projectedWealth - state.totalInvested

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResultGridTile(
                label = "Total Invested",
                value = formatCurrency(state.totalInvested),
                modifier = Modifier.weight(1f)
            )
            ResultGridTile(
                label = "Total Amount",
                value = formatCurrency(state.totalInvested + projectedGain),
                showInfo = true,
                onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.TotalAmount)) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResultGridTile(
                label = "Projected Gain",
                value = formatCurrency(projectedGain),
                showInfo = true,
                onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.ProjectedWealth)) },
                modifier = Modifier.weight(1f)
            )
            ResultGridTile(
                label = "True Purchasing Power",
                value = formatCurrency(state.netRealizedWealth),
                showInfo = true,
                onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.NetRealizedWealth)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ResultGridTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    showInfo: Boolean = false,
    onInfoClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = QuantSurfaceElevated),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = QuantTextMuted
                )
                if (showInfo) {
                    InfoIcon(onClick = onInfoClick)
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    showInfo: Boolean = false,
    onInfoClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = QuantSurfaceElevated),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = QuantTextMuted
                )
                if (showInfo) {
                    InfoIcon(onClick = onInfoClick)
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun GrowthChartCard(uiState: InvestmentUiState) {
    val chartConfig = when (uiState.selectedTabIndex) {
        SIP_TAB_INDEX -> ChartConfig(
            title = "SIP growth curve",
            primaryDataPoints = uiState.sipUiState.worthPoints,
            secondaryDataPoints = uiState.sipUiState.investedPoints,
            durationYears = uiState.sipUiState.durationYears.toChartYears(),
            secondaryLegend = "Invested Amount",
            primaryLegend = "Total Worth"
        )

        LUMP_SUM_TAB_INDEX -> ChartConfig(
            title = "Lump sum growth curve",
            primaryDataPoints = uiState.lumpSumUiState.worthPoints,
            secondaryDataPoints = uiState.lumpSumUiState.investedPoints,
            durationYears = uiState.lumpSumUiState.durationYears.toChartYears(),
            secondaryLegend = "Invested Amount",
            primaryLegend = "Total Worth"
        )

        else -> ChartConfig(
            title = "Real wealth curve",
            primaryDataPoints = uiState.realWealthUiState.projectedPoints,
            secondaryDataPoints = uiState.realWealthUiState.netRealizedPoints,
            durationYears = uiState.realWealthUiState.chartDurationYears,
            secondaryLegend = "Net Realized Wealth",
            primaryLegend = "Projected Wealth"
        )
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chartConfig.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${chartConfig.durationYears} years",
                    style = MaterialTheme.typography.labelLarge,
                    color = QuantTextMuted
                )
            }
            GrowthAreaChart(
                primaryDataPoints = chartConfig.primaryDataPoints,
                secondaryDataPoints = chartConfig.secondaryDataPoints,
                startYear = 0,
                durationYears = chartConfig.durationYears
            )
            ChartLegend(
                secondaryLabel = chartConfig.secondaryLegend,
                primaryLabel = chartConfig.primaryLegend
            )
        }
    }
}

@Composable
private fun ChartLegend(
    secondaryLabel: String,
    primaryLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(
            color = QuantGreen.copy(alpha = 0.78f),
            label = secondaryLabel
        )
        LegendItem(
            color = QuantBlue,
            label = primaryLabel
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 24.dp, height = 3.dp)
                .background(color, RoundedCornerShape(8.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = QuantTextMuted
        )
    }
}

@Composable
private fun InputPanel(
    uiState: InvestmentUiState,
    onEvent: (InvestmentUiEvent) -> Unit
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
                text = "Inputs",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            when (uiState.selectedTabIndex) {
                SIP_TAB_INDEX -> SipInputs(
                    state = uiState.sipUiState,
                    onEvent = onEvent
                )

                LUMP_SUM_TAB_INDEX -> LumpSumInputs(
                    state = uiState.lumpSumUiState,
                    onEvent = onEvent
                )

                else -> RealWealthInputs(
                    state = uiState.realWealthUiState,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun SipInputs(
    state: SipUiState,
    onEvent: (InvestmentUiEvent) -> Unit
) {
    FinancialInputField(
        label = "Monthly SIP",
        value = state.monthlySip,
        suffix = "Rs",
        onValueChange = { onEvent(InvestmentUiEvent.SipMonthlySipChanged(it)) }
    )
    FinancialInputField(
        label = "Return",
        value = state.annualReturnRate,
        suffix = "%",
        showInfo = true,
        onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.ReturnRate)) },
        onValueChange = { onEvent(InvestmentUiEvent.SipAnnualReturnRateChanged(it)) }
    )
    FinancialInputField(
        label = "Duration",
        value = state.durationYears,
        suffix = "years",
        onValueChange = { onEvent(InvestmentUiEvent.SipDurationYearsChanged(it)) },
        keyboardType = KeyboardType.Number
    )
}

@Composable
private fun LumpSumInputs(
    state: LumpSumUiState,
    onEvent: (InvestmentUiEvent) -> Unit
) {
    FinancialInputField(
        label = "Lump Sum",
        value = state.initialInvestment,
        suffix = "Rs",
        onValueChange = { onEvent(InvestmentUiEvent.LumpSumInitialInvestmentChanged(it)) }
    )
    FinancialInputField(
        label = "Return",
        value = state.annualReturnRate,
        suffix = "%",
        showInfo = true,
        onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.ReturnRate)) },
        onValueChange = { onEvent(InvestmentUiEvent.LumpSumAnnualReturnRateChanged(it)) }
    )
    FinancialInputField(
        label = "Duration",
        value = state.durationYears,
        suffix = "years",
        onValueChange = { onEvent(InvestmentUiEvent.LumpSumDurationYearsChanged(it)) },
        keyboardType = KeyboardType.Number
    )
}

@Composable
private fun RealWealthInputs(
    state: RealWealthUiState,
    onEvent: (InvestmentUiEvent) -> Unit
) {
    Text(
        text = "Mode of Investment",
        style = MaterialTheme.typography.labelLarge,
        color = QuantTextMuted
    )
    InvestmentModeSegmentedControl(
        selectedMode = state.investmentMode,
        onModeSelected = { onEvent(InvestmentUiEvent.RealWealthInvestmentModeChanged(it)) }
    )
    if (state.investmentMode != InvestmentMode.Sip) {
        FinancialInputField(
            label = "Lump Sum",
            value = state.initialInvestment,
            suffix = "Rs",
            onValueChange = { onEvent(InvestmentUiEvent.RealWealthInitialInvestmentChanged(it)) }
        )
    }
    if (state.investmentMode != InvestmentMode.LumpSum) {
        FinancialInputField(
            label = "Monthly SIP",
            value = state.monthlySip,
            suffix = "Rs",
            onValueChange = { onEvent(InvestmentUiEvent.RealWealthMonthlySipChanged(it)) }
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FinancialInputField(
        label = "Return",
        value = state.annualReturnRate,
        suffix = "%",
        showInfo = true,
        onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.ReturnRate)) },
        onValueChange = { onEvent(InvestmentUiEvent.RealWealthAnnualReturnRateChanged(it)) },
        modifier = Modifier.weight(1f)
        )
        FinancialInputField(
            label = "Inflation",
            value = state.inflationRate,
            suffix = "%",
            onValueChange = { onEvent(InvestmentUiEvent.RealWealthInflationRateChanged(it)) },
            modifier = Modifier.weight(1f)
        )
    }
    FinancialInputField(
        label = "Investment Timeline (Years)",
        value = state.durationYears,
        suffix = "years",
        showInfo = true,
        onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.InvestmentTimeline)) },
        onValueChange = { onEvent(InvestmentUiEvent.RealWealthDurationYearsChanged(it)) },
        keyboardType = KeyboardType.Decimal
    )
    AdvancedSettings(
        state = state,
        onEvent = onEvent
    )
}

@Composable
private fun InvestmentModeSegmentedControl(
    selectedMode: InvestmentMode,
    onModeSelected: (InvestmentMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InvestmentModeSegment(
            label = "Lump Sum",
            selected = selectedMode == InvestmentMode.LumpSum,
            onClick = { onModeSelected(InvestmentMode.LumpSum) },
            modifier = Modifier.weight(1f)
        )
        InvestmentModeSegment(
            label = "SIP",
            selected = selectedMode == InvestmentMode.Sip,
            onClick = { onModeSelected(InvestmentMode.Sip) },
            modifier = Modifier.weight(1f)
        )
        InvestmentModeSegment(
            label = "Combined",
            selected = selectedMode == InvestmentMode.Combined,
            onClick = { onModeSelected(InvestmentMode.Combined) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InvestmentModeSegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (selected) QuantBlue.copy(alpha = 0.2f) else QuantSurfaceElevated,
        contentColor = if (selected) QuantBlue else QuantTextMuted,
        border = BorderStroke(1.dp, if (selected) QuantBlue else QuantBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun AdvancedSettings(
    state: RealWealthUiState,
    onEvent: (InvestmentUiEvent) -> Unit
) {
    val supportsSipControls = state.investmentMode != InvestmentMode.LumpSum
    val isStcgLocked = supportsSipControls && state.withdrawalStrategy == WithdrawalStrategy.SmartHold

    Card(
        colors = CardDefaults.cardColors(containerColor = QuantSurfaceElevated),
        border = BorderStroke(1.dp, QuantBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Advanced Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(
                    checked = state.isAdvancedModeEnabled,
                    onCheckedChange = { onEvent(InvestmentUiEvent.RealWealthAdvancedModeChanged(it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = QuantBlue,
                        checkedTrackColor = QuantBlue.copy(alpha = 0.42f),
                        uncheckedThumbColor = QuantTextMuted,
                        uncheckedTrackColor = QuantSurface
                    )
                )
            }
            if (state.isAdvancedModeEnabled) {
                if (supportsSipControls) {
                    FinancialInputField(
                        label = "Annual Step-Up",
                        value = state.annualStepUpPercentage,
                        suffix = "%",
                        showInfo = true,
                        onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.AnnualStepUp)) },
                        onValueChange = { onEvent(InvestmentUiEvent.RealWealthAnnualStepUpChanged(it)) }
                    )
                }
                FinancialInputField(
                    label = "Fund Expense Ratio",
                    value = state.expenseRatio,
                    suffix = "%",
                    showInfo = true,
                    onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.FundExpenseRatio)) },
                    onValueChange = { onEvent(InvestmentUiEvent.RealWealthExpenseRatioChanged(it)) }
                )
                FinancialInputField(
                    label = "LTCG Tax",
                    value = state.ltcgTaxPercentage,
                    suffix = "%",
                    showInfo = true,
                    onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.LtcgTax)) },
                    onValueChange = { onEvent(InvestmentUiEvent.RealWealthLtcgTaxChanged(it)) }
                )
                if (supportsSipControls) {
                    FinancialInputField(
                        label = "STCG Tax",
                        value = if (isStcgLocked) "0.0" else state.stcgTaxPercentage,
                        suffix = "%",
                        enabled = !isStcgLocked,
                        showInfo = true,
                        onInfoClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.StcgTax)) },
                        onValueChange = { onEvent(InvestmentUiEvent.RealWealthStcgTaxChanged(it)) }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Withdrawal Strategy",
                            style = MaterialTheme.typography.labelLarge,
                            color = QuantTextMuted
                        )
                        InfoIcon(onClick = { onEvent(InvestmentUiEvent.TooltipClicked(InvestmentTooltip.WithdrawalStrategy)) })
                    }
                    StrategySegmentedControl(
                        selectedStrategy = state.withdrawalStrategy,
                        onStrategySelected = {
                            onEvent(InvestmentUiEvent.RealWealthWithdrawalStrategyChanged(it))
                        }
                    )
                    if (state.withdrawalStrategy == WithdrawalStrategy.SmartHold) {
                        FinancialInputField(
                            label = "Smart Hold Years",
                            value = state.holdYears,
                            suffix = "years",
                            isError = state.isHoldYearsError,
                            errorText = if (state.isHoldYearsError) {
                                "Minimum 1 year required to bypass STCG."
                            } else {
                                null
                            },
                            onValueChange = { onEvent(InvestmentUiEvent.RealWealthHoldYearsChanged(it)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StrategySegmentedControl(
    selectedStrategy: WithdrawalStrategy,
    onStrategySelected: (WithdrawalStrategy) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StrategySegment(
            label = "Immediate",
            selected = selectedStrategy == WithdrawalStrategy.Immediate,
            onClick = { onStrategySelected(WithdrawalStrategy.Immediate) },
            modifier = Modifier.weight(1f)
        )
        StrategySegment(
            label = "Smart Hold",
            selected = selectedStrategy == WithdrawalStrategy.SmartHold,
            onClick = { onStrategySelected(WithdrawalStrategy.SmartHold) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StrategySegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (selected) QuantBlue.copy(alpha = 0.2f) else QuantSurface,
        contentColor = if (selected) QuantBlue else QuantTextMuted,
        border = BorderStroke(1.dp, if (selected) QuantBlue else QuantBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun FinancialInputField(
    label: String,
    value: String,
    suffix: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null,
    showInfo: Boolean = false,
    onInfoClick: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label)
                if (showInfo) {
                    InfoIcon(onClick = onInfoClick)
                }
            }
        },
        supportingText = if (errorText != null) {
            {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            null
        },
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
            disabledTextColor = QuantTextMuted,
            disabledContainerColor = QuantSurfaceElevated,
            disabledBorderColor = QuantBorder.copy(alpha = 0.42f),
            disabledLabelColor = QuantTextMuted.copy(alpha = 0.72f),
            cursorColor = QuantBlue
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun InfoIcon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .background(QuantSurface, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "i",
            style = MaterialTheme.typography.labelSmall,
            color = QuantBlue,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GrowthAreaChart(
    primaryDataPoints: List<Float>,
    secondaryDataPoints: List<Float>,
    startYear: Int,
    durationYears: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = QuantTextMuted,
        fontSize = 11.sp
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(292.dp)
            .padding(start = 24.dp, bottom = 36.dp)
    ) {
        val allPoints = primaryDataPoints + secondaryDataPoints
        val minValue = min(0f, allPoints.minOrNull() ?: 0f)
        val maxValue = max(1f, allPoints.maxOrNull() ?: 1f)
        val yTicks = financialAxisTicks(maxValue.toDouble())
        val yAxisLabels = yTicks.map { formatAxisAmount(it) }
        val maxYAxisLabelWidth = yAxisLabels.maxOfOrNull { label ->
            textMeasurer.measure(label, style = labelStyle).size.width
        } ?: 0
        val leftAxisWidth = max(78f, maxYAxisLabelWidth + 14f)
        val rightPadding = 12f
        val topPadding = 18f
        val bottomAxisHeight = 52f
        val chartLeft = leftAxisWidth
        val chartRight = size.width - rightPadding
        val chartTop = topPadding
        val chartBottom = size.height - bottomAxisHeight
        val chartWidth = (chartRight - chartLeft).coerceAtLeast(1f)
        val chartHeight = (chartBottom - chartTop).coerceAtLeast(1f)
        val chartMaxValue = (yTicks.lastOrNull() ?: maxValue.toDouble()).toFloat().coerceAtLeast(1f)
        val valueRange = (chartMaxValue - minValue).coerceAtLeast(1f)

        yTicks.forEachIndexed { index, tick ->
            val y = chartBottom - (((tick.toFloat() - minValue) / valueRange) * chartHeight)
            drawLine(
                color = QuantBorder.copy(alpha = 0.45f),
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1f
            )
            drawText(
                textMeasurer = textMeasurer,
                text = yAxisLabels[index],
                topLeft = Offset(0f, y - 8f),
                style = labelStyle
            )
        }

        val xLabelYears = buildXAxisYears(durationYears)
        xLabelYears.forEach { offsetYear ->
            val x = if (durationYears == 0) {
                chartLeft
            } else {
                chartLeft + (offsetYear / durationYears.toFloat()) * chartWidth
            }
            drawText(
                textMeasurer = textMeasurer,
                text = "Y${startYear + offsetYear}",
                topLeft = Offset((x - 14f).coerceIn(chartLeft, chartRight - 32f), chartBottom + 9f),
                style = labelStyle
            )
        }

        val secondaryOffsets = secondaryDataPoints.toChartOffsets(
            chartLeft = chartLeft,
            chartBottom = chartBottom,
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            minValue = minValue,
            valueRange = valueRange
        )
        val primaryOffsets = primaryDataPoints.toChartOffsets(
            chartLeft = chartLeft,
            chartBottom = chartBottom,
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            minValue = minValue,
            valueRange = valueRange
        )

        if (secondaryOffsets.isNotEmpty()) {
            drawPath(
                path = secondaryOffsets.toAreaPath(chartBottom),
                color = QuantGreen.copy(alpha = 0.07f)
            )
            drawPath(
                path = secondaryOffsets.toBezierPath(),
                color = QuantGreen.copy(alpha = 0.72f),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }

        if (primaryOffsets.isNotEmpty()) {
            drawPath(
                path = primaryOffsets.toAreaPath(chartBottom),
                brush = Brush.verticalGradient(
                    colors = listOf(
                        QuantBlue.copy(alpha = 0.46f),
                        QuantBlue.copy(alpha = 0.12f),
                        QuantSurface.copy(alpha = 0f)
                    ),
                    startY = chartTop,
                    endY = chartBottom
                )
            )
            drawPath(
                path = primaryOffsets.toBezierPath(),
                color = QuantBlue,
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
            primaryOffsets.forEach { point ->
                drawCircle(
                    color = Color.White,
                    radius = 4.5f,
                    center = point
                )
                drawCircle(
                    color = QuantBlue,
                    radius = 3f,
                    center = point
                )
            }
        }
    }
}

private fun List<Float>.toChartOffsets(
    chartLeft: Float,
    chartBottom: Float,
    chartWidth: Float,
    chartHeight: Float,
    minValue: Float,
    valueRange: Float
): List<Offset> {
    if (isEmpty()) return emptyList()
    return mapIndexed { index, value ->
        val x = if (size == 1) {
            chartLeft
        } else {
            chartLeft + index * (chartWidth / lastIndex)
        }
        val normalized = ((value - minValue) / valueRange).coerceIn(0f, 1f)
        val y = chartBottom - (normalized * chartHeight)
        Offset(x, y)
    }
}

private fun List<Offset>.toBezierPath(): Path {
    return Path().apply {
        moveTo(first().x, first().y)
        for (index in 0 until lastIndex) {
            val start = this@toBezierPath[index]
            val end = this@toBezierPath[index + 1]
            val controlX = (start.x + end.x) / 2f
            cubicTo(
                controlX,
                start.y,
                controlX,
                end.y,
                end.x,
                end.y
            )
        }
    }
}

private fun List<Offset>.toAreaPath(chartBottom: Float): Path {
    return Path().apply {
        moveTo(first().x, chartBottom)
        lineTo(first().x, first().y)
        for (index in 0 until lastIndex) {
            val start = this@toAreaPath[index]
            val end = this@toAreaPath[index + 1]
            val controlX = (start.x + end.x) / 2f
            cubicTo(
                controlX,
                start.y,
                controlX,
                end.y,
                end.x,
                end.y
            )
        }
        lineTo(last().x, chartBottom)
        close()
    }
}

private fun financialAxisTicks(maxValue: Double): List<Double> {
    val step = when {
        maxValue <= 500_000.0 -> 100_000.0
        maxValue <= 2_000_000.0 -> 200_000.0
        maxValue <= 5_000_000.0 -> 500_000.0
        maxValue <= 10_000_000.0 -> 1_000_000.0
        maxValue <= 30_000_000.0 -> 2_000_000.0
        maxValue <= 100_000_000.0 -> 10_000_000.0
        else -> 50_000_000.0
    }
    val cappedMax = max(step, ceil(maxValue / step) * step)
    return generateSequence(0.0) { it + step }
        .takeWhile { it <= cappedMax }
        .toList()
}

private fun buildXAxisYears(durationYears: Int): List<Int> {
    if (durationYears <= 0) return listOf(0)
    val labels = (0..durationYears step X_AXIS_INTERVAL_YEARS).toMutableList()
    if (labels.last() != durationYears) {
        labels.add(durationYears)
    }
    return labels
}

private fun formatCurrency(value: Double): String {
    return "Rs ${indianNumberFormat.format(value)}"
}

private fun formatAxisAmount(value: Double): String {
    return when {
        value >= CRORE * THOUSAND -> "${trimDecimal(value / (CRORE * THOUSAND))}K Cr"
        value >= CRORE -> "${trimDecimal(value / CRORE)} Cr"
        value >= LAKH -> "${trimDecimal(value / LAKH)} L"
        value >= THOUSAND -> "${trimDecimal(value / THOUSAND)} K"
        else -> indianNumberFormat.format(value)
    }
}

private fun trimDecimal(value: Double): String {
    val formatted = String.format(Locale.US, "%.1f", value)
    return formatted.removeSuffix(".0")
}

private fun String.toChartYears(): Int {
    return trim()
        .toIntOrNull()
        ?.coerceAtLeast(0)
        ?: 0
}

private data class ChartConfig(
    val title: String,
    val primaryDataPoints: List<Float>,
    val secondaryDataPoints: List<Float>,
    val durationYears: Int,
    val secondaryLegend: String,
    val primaryLegend: String
)

private val indianNumberFormat = DecimalFormat(
    "##,##,##,##0",
    DecimalFormatSymbols(Locale.US)
)

private const val SIP_TAB_INDEX = 0
private const val LUMP_SUM_TAB_INDEX = 1
private const val REAL_WEALTH_TAB_INDEX = 2
private const val X_AXIS_INTERVAL_YEARS = 4
private const val THOUSAND = 1_000.0
private const val LAKH = 100_000.0
private const val CRORE = 10_000_000.0
