package com.ayush.quantlab.feature.investment

data class InvestmentUiState(
    val selectedTabIndex: Int = 0,
    val sipUiState: SipUiState = SipUiState(),
    val lumpSumUiState: LumpSumUiState = LumpSumUiState(),
    val realWealthUiState: RealWealthUiState = RealWealthUiState(),
    val activeTooltip: InvestmentTooltip? = null
)

data class SipUiState(
    val monthlySip: String = "15000",
    val annualReturnRate: String = "12",
    val durationYears: String = "20",
    val investedAmount: Double = 0.0,
    val estimatedProfit: Double = 0.0,
    val totalAmount: Double = 0.0,
    val investedPoints: List<Float> = emptyList(),
    val worthPoints: List<Float> = emptyList()
)

data class LumpSumUiState(
    val initialInvestment: String = "100000",
    val annualReturnRate: String = "12",
    val durationYears: String = "20",
    val investedAmount: Double = 0.0,
    val estimatedProfit: Double = 0.0,
    val totalAmount: Double = 0.0,
    val investedPoints: List<Float> = emptyList(),
    val worthPoints: List<Float> = emptyList()
)

data class RealWealthUiState(
    val investmentMode: InvestmentMode = InvestmentMode.Combined,
    val initialInvestment: String = "100000",
    val monthlySip: String = "15000",
    val annualReturnRate: String = "12",
    val inflationRate: String = "6",
    val durationYears: String = "20",
    val isAdvancedModeEnabled: Boolean = false,
    val annualStepUpPercentage: String = "10.0",
    val expenseRatio: String = "0.5",
    val ltcgTaxPercentage: String = "12.5",
    val stcgTaxPercentage: String = "20.0",
    val withdrawalStrategy: WithdrawalStrategy = WithdrawalStrategy.Immediate,
    val holdYears: String = "1.0",
    val isHoldYearsError: Boolean = false,
    val totalInvested: Double = 0.0,
    val projectedWealth: Double = 0.0,
    val netRealizedWealth: Double = 0.0,
    val projectedPoints: List<Float> = emptyList(),
    val netRealizedPoints: List<Float> = emptyList(),
    val chartDurationYears: Int = 0
)

enum class WithdrawalStrategy {
    Immediate,
    SmartHold
}

enum class InvestmentMode {
    LumpSum,
    Sip,
    Combined
}

enum class InvestmentTooltip(val title: String, val message: String) {
    ReturnRate(
        title = "Expected return",
        message = "This is an assumed historical average annual return. Actual market returns can vary every year and are not guaranteed."
    ),
    InvestmentTimeline(
        title = "Investment Timeline (Years)",
        message = "Enter the exact time you plan to stay invested before withdrawing. You can use decimals for half-years (e.g., 14.5 for 14 years and 6 months)."
    ),
    ProjectedWealth(
        title = "Projected Wealth",
        message = "The estimated total value of your investment at the end of the duration, based on pure compound interest before accounting for inflation, taxes, or fees."
    ),
    TotalAmount(
        title = "Total Amount",
        message = "Gross maturity value before adjusting for inflation."
    ),
    NetRealizedWealth(
        title = "True Purchasing Power",
        message = "The actual spending power of your final amount in today's money, after deducting estimated market inflation, fund fees, and taxes."
    ),
    AnnualStepUp(
        title = "Annual Step-Up",
        message = "The percentage by which you increase your investment contribution every year. The default is 10.0%, but it can be changed."
    ),
    FundExpenseRatio(
        title = "Fund Expense Ratio",
        message = "The annual fee charged by mutual funds to manage your money. The default is 0.5%."
    ),
    LtcgTax(
        title = "LTCG Tax",
        message = "Long-Term Capital Gains Tax applied to profits on investments older than 1 year. The default is 12.5%."
    ),
    StcgTax(
        title = "STCG Tax",
        message = "Tax applied to profits on investments withdrawn before completing 1 year. The default is 20.0%."
    ),
    WithdrawalStrategy(
        title = "Withdrawal Strategy",
        message = "Immediate: Liquidate exactly at the end of your timeline (Recent SIPs incur STCG). Smart Hold: Stop SIPs but hold the corpus for 1 extra year to bypass STCG entirely."
    )
}

sealed class InvestmentUiEvent {
    data object BackClicked : InvestmentUiEvent()
    data class TabSelected(val index: Int) : InvestmentUiEvent()
    data class TooltipClicked(val tooltip: InvestmentTooltip) : InvestmentUiEvent()
    data object TooltipDismissed : InvestmentUiEvent()

    data class SipMonthlySipChanged(val value: String) : InvestmentUiEvent()
    data class SipAnnualReturnRateChanged(val value: String) : InvestmentUiEvent()
    data class SipDurationYearsChanged(val value: String) : InvestmentUiEvent()

    data class LumpSumInitialInvestmentChanged(val value: String) : InvestmentUiEvent()
    data class LumpSumAnnualReturnRateChanged(val value: String) : InvestmentUiEvent()
    data class LumpSumDurationYearsChanged(val value: String) : InvestmentUiEvent()

    data class RealWealthInitialInvestmentChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthInvestmentModeChanged(val mode: InvestmentMode) : InvestmentUiEvent()
    data class RealWealthMonthlySipChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthAnnualReturnRateChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthInflationRateChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthDurationYearsChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthAdvancedModeChanged(val enabled: Boolean) : InvestmentUiEvent()
    data class RealWealthAnnualStepUpChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthExpenseRatioChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthLtcgTaxChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthStcgTaxChanged(val value: String) : InvestmentUiEvent()
    data class RealWealthWithdrawalStrategyChanged(val strategy: WithdrawalStrategy) : InvestmentUiEvent()
    data class RealWealthHoldYearsChanged(val value: String) : InvestmentUiEvent()
}
