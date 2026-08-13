package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IOSBackground
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSCardSurface
import com.example.ui.theme.IOSDivider
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.StatusAbsentBg
import com.example.ui.theme.StatusAbsentRed
import com.example.ui.theme.StatusHalfDayAmber
import com.example.ui.theme.StatusHalfDayBg
import com.example.ui.theme.StatusLateBg
import com.example.ui.theme.StatusLateOrange
import com.example.ui.theme.StatusLeaveBg
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusNotMarkedBg
import com.example.ui.theme.StatusNotMarkedGray
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentGreen

@Composable
fun IOSCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation, RoundedCornerShape(cornerRadius), ambientColor = Color.Black.copy(alpha = 0.05f))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = IOSCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            content()
        }
    }
}

@Composable
fun IOSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = IOSBlue,
    contentColor: Color = Color.White,
    icon: ImageVector? = null,
    testTag: String = "ios_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = IOSDivider,
            disabledContentColor = IOSTextSecondary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun IOSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    testTag: String = "ios_text_field"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = IOSTextSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = IOSTextSecondary.copy(alpha = 0.7f)) },
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = IOSTextSecondary) }
            } else null,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = IOSCardSurface,
                unfocusedContainerColor = IOSBackground,
                disabledContainerColor = IOSBackground,
                focusedBorderColor = IOSBlue,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = IOSTextPrimary,
                unfocusedTextColor = IOSTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
fun IOSStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "PRESENT" -> Triple(StatusPresentBg, StatusPresentGreen, "🟢 Present")
        "ABSENT" -> Triple(StatusAbsentBg, StatusAbsentRed, "🔴 Absent")
        "LATE" -> Triple(StatusLateBg, StatusLateOrange, "🟠 Late")
        "HALF_DAY" -> Triple(StatusHalfDayBg, StatusHalfDayAmber, "🟡 Half Day")
        "LEAVE" -> Triple(StatusLeaveBg, StatusLeaveBlue, "🔵 Leave")
        else -> Triple(StatusNotMarkedBg, StatusNotMarkedGray, "⚪ Not Marked")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
fun IOSStatSummaryCard(
    title: String,
    value: String,
    subtext: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = IOSBlue,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(18.dp), ambientColor = Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = IOSCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = IOSTextSecondary
                )
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = IOSTextPrimary
            )
            if (subtext != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.labelSmall,
                    color = IOSTextSecondary
                )
            }
        }
    }
}

// Canvas-based minimal bar chart for Department Reports
@Composable
fun IOSBarChart(
    data: List<Pair<String, Float>>,
    maxVal: Float = 100f,
    barColor: Color = IOSBlue,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(vertical = 8.dp)
        ) {
            val barWidth = size.width / (data.size * 2)
            val chartHeight = size.height - 30.dp.toPx()

            data.forEachIndexed { index, pair ->
                val x = (index * 2 + 0.5f) * barWidth
                val barH = (pair.second / maxVal) * chartHeight
                val y = chartHeight - barH

                // Draw background bar track
                drawRoundRect(
                    color = IOSBackground,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, chartHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )

                // Draw active bar
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = IOSTextSecondary
                )
            }
        }
    }
}

// Canvas-based Donut chart for Attendance distribution
@Composable
fun IOSDonutChart(
    presentPct: Float,
    absentPct: Float,
    latePct: Float,
    leavePct: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(130.dp)) {
            val strokeWidth = 22.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            var startAngle = -90f

            val segments = listOf(
                presentPct to StatusPresentGreen,
                absentPct to StatusAbsentRed,
                latePct to StatusLateOrange,
                leavePct to StatusLeaveBlue
            )

            segments.forEach { (pct, color) ->
                if (pct > 0f) {
                    val sweep = (pct / 100f) * 360f
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweep
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${presentPct.toInt()}%",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = IOSTextPrimary
            )
            Text(
                text = "Present",
                style = MaterialTheme.typography.labelSmall,
                color = IOSTextSecondary
            )
        }
    }
}
