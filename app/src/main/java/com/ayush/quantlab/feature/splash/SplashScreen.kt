package com.ayush.quantlab.feature.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ayush.quantlab.R
import com.ayush.quantlab.ui.theme.QuantBlue
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SplashDestination(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSplashFinished) {
        if (uiState.isSplashFinished) {
            onSplashFinished()
        }
    }

    SplashScreen(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "splash-wave")
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2f).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6_500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    val shimmerPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerPhase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "QuantLab splash",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        AnimatedWaveOverlay(
            wavePhase = wavePhase,
            shimmerPhase = shimmerPhase,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(360.dp)
        )
    }
}

@Composable
private fun AnimatedWaveOverlay(
    wavePhase: Float,
    shimmerPhase: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val baseY = height * 0.42f
        val primaryGlow = QuantBlue.copy(alpha = 0.55f + shimmerPhase * 0.22f)
        val cyan = Color(0xFF09E6FF)
        val deepBlue = Color(0xFF085DFF)

        repeat(18) { row ->
            val rowProgress = row / 17f
            val amplitude = 18f + rowProgress * 52f
            val yOffset = rowProgress * 120f
            val alpha = (0.18f - rowProgress * 0.006f).coerceAtLeast(0.05f)
            val path = Path()

            for (step in 0..96) {
                val x = width * (step / 96f)
                val wave = sin((step / 12f) + wavePhase + row * 0.18f)
                val y = baseY + yOffset + wave * amplitude
                if (step == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            drawPath(
                path = path,
                color = QuantBlue.copy(alpha = alpha),
                style = Stroke(width = 1.1f, cap = StrokeCap.Round)
            )
        }

        val energyPath = Path()
        for (step in 0..80) {
            val x = width * (step / 80f)
            val y = baseY + sin((step / 8.5f) + wavePhase) * 28f
            if (step == 0) {
                energyPath.moveTo(x, y)
            } else {
                energyPath.lineTo(x, y)
            }
        }
        drawPath(
            path = energyPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    deepBlue.copy(alpha = 0.25f),
                    cyan.copy(alpha = 0.92f),
                    QuantBlue.copy(alpha = 0.42f)
                )
            ),
            style = Stroke(width = 4.5f, cap = StrokeCap.Round)
        )
        drawPath(
            path = energyPath,
            color = cyan.copy(alpha = 0.16f),
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )

        val candleStartX = width * 0.51f
        val candleEndX = width * 0.96f
        val candleCount = 26
        repeat(candleCount) { index ->
            val progress = index / (candleCount - 1f)
            val x = candleStartX + (candleEndX - candleStartX) * progress
            val trend = height * (0.76f - progress * 0.52f)
            val breathing = sin(wavePhase + progress * 5f) * 5f
            val wickTop = trend - 22f - progress * 42f + breathing
            val wickBottom = trend + 24f + breathing
            val bodyHeight = 12f + progress * 18f
            val bodyTop = trend - bodyHeight / 2f + breathing

            drawLine(
                color = primaryGlow,
                start = Offset(x, wickTop),
                end = Offset(x, wickBottom),
                strokeWidth = 1.4f
            )
            drawRect(
                color = QuantBlue.copy(alpha = 0.68f + shimmerPhase * 0.18f),
                topLeft = Offset(x - 4f, bodyTop),
                size = androidx.compose.ui.geometry.Size(8f, bodyHeight)
            )
        }

        repeat(90) { index ->
            val progress = ((index * 37) % 100) / 100f
            val x = width * progress
            val ySeed = ((index * 53) % 100) / 100f
            val y = height * (0.1f + ySeed * 0.82f) + sin(wavePhase + index) * 3.5f
            val sparkle = 0.2f + ((sin(wavePhase * 0.7f + index) + 1f) / 2f) * 0.65f
            val radius = if (index % 9 == 0) 2.2f else 1.25f
            drawCircle(
                color = cyan.copy(alpha = sparkle * 0.72f),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}
