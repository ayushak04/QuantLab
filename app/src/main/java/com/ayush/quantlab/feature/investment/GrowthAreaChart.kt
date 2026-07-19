package com.ayush.quantlab.feature.investment

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ayush.quantlab.ui.theme.QuantBlue
import com.ayush.quantlab.ui.theme.QuantBorder
import com.ayush.quantlab.ui.theme.QuantSurface

@Composable
fun GrowthAreaChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        val chartTop = 18f
        val chartBottom = size.height - 18f
        val chartHeight = chartBottom - chartTop
        val chartWidth = size.width
        val maxValue = dataPoints.maxOrNull()?.coerceAtLeast(1f) ?: 1f
        val minValue = 0f
        val range = (maxValue - minValue).coerceAtLeast(1f)

        repeat(4) { index ->
            val y = chartTop + chartHeight * (index / 3f)
            drawLine(
                color = QuantBorder.copy(alpha = 0.45f),
                start = Offset(0f, y),
                end = Offset(chartWidth, y),
                strokeWidth = 1f
            )
        }

        if (dataPoints.isEmpty()) return@Canvas

        val points = dataPoints.mapIndexed { index, value ->
            val x = if (dataPoints.size == 1) {
                chartWidth
            } else {
                index * (chartWidth / (dataPoints.lastIndex))
            }
            val normalized = ((value - minValue) / range).coerceIn(0f, 1f)
            val y = chartBottom - (normalized * chartHeight)
            Offset(x, y)
        }

        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (index in 0 until points.lastIndex) {
                val start = points[index]
                val end = points[index + 1]
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

        val areaPath = Path().apply {
            moveTo(points.first().x, chartBottom)
            lineTo(points.first().x, points.first().y)
            for (index in 0 until points.lastIndex) {
                val start = points[index]
                val end = points[index + 1]
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
            lineTo(points.last().x, chartBottom)
            close()
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    QuantBlue.copy(alpha = 0.46f),
                    QuantBlue.copy(alpha = 0.12f),
                    QuantSurface.copy(alpha = 0f)
                )
            )
        )
        drawPath(
            path = linePath,
            color = QuantBlue,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
        points.forEach { point ->
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
