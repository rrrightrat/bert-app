package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.CozyLanternAmber
import com.example.ui.theme.CozyTorchGlow
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GrayText
import com.example.ui.theme.MutedText
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurpleDark
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.SignalHigh
import com.example.ui.theme.SignalLow
import com.example.ui.theme.SignalMax
import com.example.ui.theme.SignalMid
import com.example.ui.theme.SignalOff
import com.example.ui.theme.SignalUltra
import com.example.ui.theme.White

@Composable
fun NumericInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    unit: String? = null,
    quickPresets: List<Int> = emptyList(),
    helperText: String? = null
) {
    val focusManager = LocalFocusManager.current
    var textState by remember(value) { mutableStateOf(value.toString()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Spacious header row with clear separation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = White,
                fontWeight = FontWeight.SemiBold
            )
            if (unit != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PurpleContainer.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = unit.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = PurpleBright,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Spacious Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Decrement Button
            Surface(
                onClick = {
                    val next = (value - 1).coerceIn(min, max)
                    onValueChange(next)
                    textState = next.toString()
                },
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Keyboard direct input field with generous height
            OutlinedTextField(
                value = textState,
                onValueChange = { input ->
                    textState = input
                    val parsed = input.toIntOrNull()
                    if (parsed != null) {
                        onValueChange(parsed.coerceIn(min, max))
                    } else if (input.isEmpty()) {
                        onValueChange(min)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        val parsed = textState.toIntOrNull() ?: min
                        val clamped = parsed.coerceIn(min, max)
                        textState = clamped.toString()
                        onValueChange(clamped)
                    }
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = White,
                    fontSize = 18.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    cursorColor = PurplePrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("numeric_input_${label.lowercase().replace(" ", "_")}")
            )

            // Increment Button
            Surface(
                onClick = {
                    val next = (value + 1).coerceIn(min, max)
                    onValueChange(next)
                    textState = next.toString()
                },
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (helperText != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = helperText,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = GrayText
            )
        }

        // Quick Preset Chips with spacious cozy layout
        if (quickPresets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPresets.forEach { preset ->
                    val isSelected = value == preset
                    Surface(
                        onClick = {
                            val next = preset.coerceIn(min, max)
                            onValueChange(next)
                            textState = next.toString()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PurpleContainer else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PurplePrimary else DarkCardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 9.dp)
                        ) {
                            Text(
                                text = preset.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PurpleBright else OffWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RedstoneSignalBar(
    currentSignal: Int,
    modifier: Modifier = Modifier,
    onSegmentClick: ((Int) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("redstone_signal_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REDSTONE SIGNAL GAUGE",
                style = MaterialTheme.typography.labelSmall,
                color = GrayText,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (currentSignal > 0) CozyLanternAmber else MutedText)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SS $currentSignal / 15 (${currentSignal} BLOCKS)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (currentSignal > 0) White else MutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 15 segment LED row with comfortable cozy height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (level in 1..15) {
                val isActive = level <= currentSignal
                val activeColor = when {
                    level == 15 -> SignalMax
                    level >= 12 -> SignalUltra
                    level >= 9 -> SignalHigh
                    level >= 5 -> SignalMid
                    else -> SignalLow
                }

                val targetColor = if (isActive) activeColor else SignalOff
                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(durationMillis = 150),
                    label = "segmentColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(animatedColor)
                        .clickable(enabled = onSegmentClick != null) {
                            onSegmentClick?.invoke(level)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (level == 1 || level == 5 || level == 10 || level == 15) {
                        Text(
                            text = level.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isActive) Color.Black else MutedText.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingTag: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkCardBorder),
            width = 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(PurpleBright.copy(alpha = 0.85f))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText
                        )
                    }
                }
                if (!trailingTag.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PurpleContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = trailingTag,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = PurpleBright,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            content()
        }
    }
}

@Composable
fun BedrockTipCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    val borderColor = if (isWarning) AccentAmber.copy(alpha = 0.6f) else DarkCardBorder
    val accentColor = if (isWarning) AccentAmber else PurpleBright

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OffWhite,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
