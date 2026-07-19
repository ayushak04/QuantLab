package com.ayush.quantlab.feature.investment

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

class InvestmentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        InvestmentUiState(
            sipUiState = SipUiState().withCalculatedResults(),
            lumpSumUiState = LumpSumUiState().withCalculatedResults(),
            realWealthUiState = RealWealthUiState().withCalculatedResults()
        )
    )
    val uiState: StateFlow<InvestmentUiState> = _uiState.asStateFlow()

    fun onEvent(event: InvestmentUiEvent) {
        when (event) {
            InvestmentUiEvent.BackClicked -> Unit
            InvestmentUiEvent.TooltipDismissed -> _uiState.update {
                it.copy(activeTooltip = null)
            }

            is InvestmentUiEvent.TooltipClicked -> _uiState.update {
                it.copy(activeTooltip = event.tooltip)
            }

            is InvestmentUiEvent.TabSelected -> _uiState.update {
                it.copy(selectedTabIndex = event.index.coerceIn(SIP_TAB, REAL_WEALTH_TAB))
            }

            is InvestmentUiEvent.SipMonthlySipChanged -> updateSip {
                copy(monthlySip = event.value)
            }

            is InvestmentUiEvent.SipAnnualReturnRateChanged -> updateSip {
                copy(annualReturnRate = event.value)
            }

            is InvestmentUiEvent.SipDurationYearsChanged -> updateSip {
                copy(durationYears = event.value)
            }

            is InvestmentUiEvent.LumpSumInitialInvestmentChanged -> updateLumpSum {
                copy(initialInvestment = event.value)
            }

            is InvestmentUiEvent.LumpSumAnnualReturnRateChanged -> updateLumpSum {
                copy(annualReturnRate = event.value)
            }

            is InvestmentUiEvent.LumpSumDurationYearsChanged -> updateLumpSum {
                copy(durationYears = event.value)
            }

            is InvestmentUiEvent.RealWealthInitialInvestmentChanged -> updateRealWealth {
                copy(initialInvestment = event.value)
            }

            is InvestmentUiEvent.RealWealthInvestmentModeChanged -> updateRealWealth {
                copy(investmentMode = event.mode)
            }

            is InvestmentUiEvent.RealWealthMonthlySipChanged -> updateRealWealth {
                copy(monthlySip = event.value)
            }

            is InvestmentUiEvent.RealWealthAnnualReturnRateChanged -> updateRealWealth {
                copy(annualReturnRate = event.value)
            }

            is InvestmentUiEvent.RealWealthInflationRateChanged -> updateRealWealth {
                copy(inflationRate = event.value)
            }

            is InvestmentUiEvent.RealWealthDurationYearsChanged -> updateRealWealth {
                copy(durationYears = event.value)
            }

            is InvestmentUiEvent.RealWealthAdvancedModeChanged -> updateRealWealth {
                copy(isAdvancedModeEnabled = event.enabled)
            }

            is InvestmentUiEvent.RealWealthAnnualStepUpChanged -> updateRealWealth {
                copy(annualStepUpPercentage = event.value)
            }

            is InvestmentUiEvent.RealWealthExpenseRatioChanged -> updateRealWealth {
                copy(expenseRatio = event.value)
            }

            is InvestmentUiEvent.RealWealthLtcgTaxChanged -> updateRealWealth {
                copy(ltcgTaxPercentage = event.value)
            }

            is InvestmentUiEvent.RealWealthStcgTaxChanged -> updateRealWealth {
                copy(stcgTaxPercentage = event.value)
            }

            is InvestmentUiEvent.RealWealthWithdrawalStrategyChanged -> updateRealWealth {
                copy(withdrawalStrategy = event.strategy)
            }

            is InvestmentUiEvent.RealWealthHoldYearsChanged -> updateRealWealth {
                copy(holdYears = event.value)
            }
        }
    }

    private fun updateSip(transform: SipUiState.() -> SipUiState) {
        _uiState.update { state ->
            state.copy(sipUiState = state.sipUiState.transform().withCalculatedResults())
        }
    }

    private fun updateLumpSum(transform: LumpSumUiState.() -> LumpSumUiState) {
        _uiState.update { state ->
            state.copy(lumpSumUiState = state.lumpSumUiState.transform().withCalculatedResults())
        }
    }

    private fun updateRealWealth(transform: RealWealthUiState.() -> RealWealthUiState) {
        _uiState.update { state ->
            val transformedState = state.realWealthUiState.transform()
            val hasHoldYearsError = transformedState.requiresHoldYearsValidation() &&
                !transformedState.hasValidHoldYears()
            val nextState = transformedState.copy(isHoldYearsError = hasHoldYearsError)
            state.copy(
                realWealthUiState = if (hasHoldYearsError) {
                    nextState
                } else {
                    nextState.withCalculatedResults()
                }
            )
        }
    }
}

private fun RealWealthUiState.requiresHoldYearsValidation(): Boolean {
    return isAdvancedModeEnabled &&
        investmentMode != InvestmentMode.LumpSum &&
        withdrawalStrategy == WithdrawalStrategy.SmartHold
}

private fun RealWealthUiState.hasValidHoldYears(): Boolean {
    return holdYears.toYearsDouble() >= MIN_SMART_HOLD_YEARS
}

private fun SipUiState.withCalculatedResults(): SipUiState {
    val monthlyContribution = monthlySip.moneyToDouble()
    val years = durationYears.toWholeYears()
    val months = years * MONTHS_IN_YEAR
    val monthlyRate = annualReturnRate.toPercent() / 1200.0
    val invested = monthlyContribution * months
    val total = futureValueOfAnnuityDue(
        payment = monthlyContribution,
        monthlyRate = monthlyRate,
        months = months
    )

    val investedPoints = (0..years).map { year ->
        (monthlyContribution * year * MONTHS_IN_YEAR).toFloat()
    }
    val worthPoints = (0..years).map { year ->
        futureValueOfAnnuityDue(
            payment = monthlyContribution,
            monthlyRate = monthlyRate,
            months = year * MONTHS_IN_YEAR
        ).toFloat()
    }

    return copy(
        investedAmount = invested,
        estimatedProfit = total - invested,
        totalAmount = total,
        investedPoints = investedPoints,
        worthPoints = worthPoints
    )
}

private fun LumpSumUiState.withCalculatedResults(): LumpSumUiState {
    val principal = initialInvestment.moneyToDouble()
    val years = durationYears.toWholeYears()
    val annualRate = annualReturnRate.toPercent() / 100.0
    val total = principal * (1.0 + annualRate).pow(years)
    val investedPoints = (0..years).map { principal.toFloat() }
    val worthPoints = (0..years).map { year ->
        (principal * (1.0 + annualRate).pow(year)).toFloat()
    }

    return copy(
        investedAmount = principal,
        estimatedProfit = total - principal,
        totalAmount = total,
        investedPoints = investedPoints,
        worthPoints = worthPoints
    )
}

private fun RealWealthUiState.withCalculatedResults(): RealWealthUiState {
    val result = calculateRealWealthV2()

    return copy(
        totalInvested = result.totalInvested,
        projectedWealth = result.finalNominalWealth,
        netRealizedWealth = result.trueRealWealthValue,
        projectedPoints = result.nominalPoints,
        netRealizedPoints = result.realWealthPoints,
        chartDurationYears = ceil(result.finalMonths / MONTHS_IN_YEAR.toDouble()).toInt()
    )
}

private fun RealWealthUiState.calculateRealWealthV2(): RealWealthResult {
    val principal = if (investmentMode == InvestmentMode.Sip) {
        0.0
    } else {
        initialInvestment.moneyToDouble()
    }
    val baseSip = if (investmentMode == InvestmentMode.LumpSum) {
        0.0
    } else {
        monthlySip.moneyToDouble()
    }
    val grossReturn = annualReturnRate.toPercent()
    val inflation = inflationRate.toPercent()
    val targetMonths = (durationYears.toYearsDouble() * MONTHS_IN_YEAR).roundToInt().coerceAtLeast(0)

    val stepUp = if (isAdvancedModeEnabled && investmentMode != InvestmentMode.LumpSum) {
        annualStepUpPercentage.toPercent()
    } else {
        0.0
    }
    val expenseRatioValue = if (isAdvancedModeEnabled) expenseRatio.toPercent() else 0.0
    val ltcgRate = if (isAdvancedModeEnabled) ltcgTaxPercentage.toPercent() else 0.0
    val stcgRate = if (isAdvancedModeEnabled) stcgTaxPercentage.toPercent() else 0.0
    val effectiveStrategy = if (isAdvancedModeEnabled && investmentMode != InvestmentMode.LumpSum) {
        withdrawalStrategy
    } else {
        WithdrawalStrategy.Immediate
    }
    val holdMonths = if (effectiveStrategy == WithdrawalStrategy.SmartHold) {
        (holdYears.toYearsDouble() * MONTHS_IN_YEAR).roundToInt().coerceAtLeast(0)
    } else {
        0
    }
    val finalMonths = targetMonths + holdMonths

    val annualNetRate = ((grossReturn - expenseRatioValue) / 100.0).coerceAtLeast(MIN_ANNUAL_NET_RATE)
    val lumpMonthlyRate = (1.0 + annualNetRate).pow(1.0 / MONTHS_IN_YEAR) - 1.0
    val sipMonthlyRate = annualNetRate / MONTHS_IN_YEAR
    val ltcgFactor = ltcgRate / 100.0
    val stcgFactor = if (effectiveStrategy == WithdrawalStrategy.SmartHold) {
        0.0
    } else {
        stcgRate / 100.0
    }

    var lumpValue = principal
    var sipValue = 0.0
    var currentSip = baseSip
    var totalSipInvested = 0.0
    val sipLots = mutableListOf<SipLot>()
    val nominalPoints = mutableListOf(principal.toFloat())
    val realWealthPoints = mutableListOf(principal.toFloat())

    for (month in 1..finalMonths) {
        lumpValue *= (1.0 + lumpMonthlyRate)

        if (month <= targetMonths && currentSip > 0.0) {
            sipLots.add(SipLot(month = month, amount = currentSip, value = currentSip))
            totalSipInvested += currentSip
        }

        sipLots.forEach { lot ->
            lot.value *= (1.0 + sipMonthlyRate)
        }
        sipValue = sipLots.sumOf { it.value }

        nominalPoints.add((lumpValue + sipValue).toFloat())
        realWealthPoints.add(
            ((lumpValue + sipValue) / inflationMultiplier(inflation, month)).toFloat()
        )

        if (month <= targetMonths && month % MONTHS_IN_YEAR == 0) {
            currentSip *= (1.0 + (stepUp / 100.0))
        }
    }

    val taxResult = calculateFifoTax(
        strategy = effectiveStrategy,
        targetMonths = targetMonths,
        lumpValue = lumpValue,
        principal = principal,
        sipValue = sipValue,
        totalSipInvested = totalSipInvested,
        sipLots = sipLots,
        ltcgFactor = ltcgFactor,
        stcgFactor = stcgFactor
    )
    val finalNominalWealth = (lumpValue + sipValue) - taxResult.totalTaxOwed
    val trueRealWealthValue = finalNominalWealth / inflationMultiplier(inflation, finalMonths)

    if (nominalPoints.isNotEmpty()) {
        nominalPoints[nominalPoints.lastIndex] = finalNominalWealth.toFloat()
        realWealthPoints[realWealthPoints.lastIndex] = trueRealWealthValue.toFloat()
    }

    return RealWealthResult(
        totalInvested = principal + totalSipInvested,
        finalNominalWealth = finalNominalWealth,
        trueRealWealthValue = trueRealWealthValue,
        nominalPoints = nominalPoints,
        realWealthPoints = realWealthPoints,
        finalMonths = finalMonths
    )
}

private fun calculateFifoTax(
    strategy: WithdrawalStrategy,
    targetMonths: Int,
    lumpValue: Double,
    principal: Double,
    sipValue: Double,
    totalSipInvested: Double,
    sipLots: List<SipLot>,
    ltcgFactor: Double,
    stcgFactor: Double
): TaxResult {
    val totalLtcgGains: Double
    val totalStcgGains: Double

    if (strategy == WithdrawalStrategy.Immediate) {
        val lumpLtcgGains = maxOf(0.0, lumpValue - principal)
        val stcgStartMonth = (targetMonths - STCG_WINDOW_MONTHS + 1).coerceAtLeast(1)
        val stcgLots = sipLots.filter { it.month in stcgStartMonth..targetMonths }
        val sipStcgCurrentValue = stcgLots.sumOf { it.value }
        val sipStcgCostBasis = stcgLots.sumOf { it.amount }
        val sipStcgGains = maxOf(0.0, sipStcgCurrentValue - sipStcgCostBasis)

        val remainingCostBasis = totalSipInvested - sipStcgCostBasis
        val sipLtcgCurrentValue = sipValue - sipStcgCurrentValue
        val sipLtcgGains = maxOf(0.0, sipLtcgCurrentValue - remainingCostBasis)

        totalLtcgGains = lumpLtcgGains + sipLtcgGains
        totalStcgGains = sipStcgGains
    } else {
        totalLtcgGains = (lumpValue + sipValue) - (principal + totalSipInvested)
        totalStcgGains = 0.0
    }

    val taxableLtcg = maxOf(0.0, totalLtcgGains - LTCG_EXEMPTION_LIMIT)
    val ltcgTaxOwed = taxableLtcg * ltcgFactor
    val stcgTaxOwed = totalStcgGains * stcgFactor

    return TaxResult(totalTaxOwed = ltcgTaxOwed + stcgTaxOwed)
}

private fun inflationMultiplier(
    annualInflationRate: Double,
    months: Int
): Double {
    return (1.0 + (annualInflationRate / 100.0)).pow(months / MONTHS_IN_YEAR.toDouble())
}

private data class SipLot(
    val month: Int,
    val amount: Double,
    var value: Double
)

private data class TaxResult(
    val totalTaxOwed: Double
)

private data class RealWealthResult(
    val totalInvested: Double,
    val finalNominalWealth: Double,
    val trueRealWealthValue: Double,
    val nominalPoints: List<Float>,
    val realWealthPoints: List<Float>,
    val finalMonths: Int
)

private fun futureValueOfAnnuityDue(
    payment: Double,
    monthlyRate: Double,
    months: Int
): Double {
    if (months <= 0 || payment == 0.0) return 0.0
    if (abs(monthlyRate) < ZERO_RATE_THRESHOLD) return payment * months
    return payment * (((1.0 + monthlyRate).pow(months) - 1.0) / monthlyRate) * (1.0 + monthlyRate)
}

private fun String.moneyToDouble(): Double {
    return replace(",", "")
        .trim()
        .toDoubleOrNull()
        ?.coerceAtLeast(0.0)
        ?: 0.0
}

private fun String.toPercent(): Double {
    return trim()
        .toDoubleOrNull()
        ?.coerceIn(MIN_PERCENT, MAX_PERCENT)
        ?: 0.0
}

private fun String.toWholeYears(): Int {
    return trim()
        .toIntOrNull()
        ?.coerceIn(MIN_YEARS, MAX_YEARS)
        ?: 0
}

private fun String.toYearsDouble(): Double {
    return trim()
        .toDoubleOrNull()
        ?.coerceIn(MIN_YEARS.toDouble(), MAX_YEARS.toDouble())
        ?: 0.0
}

private const val SIP_TAB = 0
private const val REAL_WEALTH_TAB = 2
private const val MONTHS_IN_YEAR = 12
private const val STCG_WINDOW_MONTHS = 12
private const val ZERO_RATE_THRESHOLD = 0.0000001
private const val LTCG_EXEMPTION_LIMIT = 125_000.0
private const val MIN_ANNUAL_NET_RATE = -0.999999
private const val MIN_SMART_HOLD_YEARS = 1.0
private const val MIN_PERCENT = -99.0
private const val MAX_PERCENT = 100.0
private const val MIN_YEARS = 0
private const val MAX_YEARS = 60
