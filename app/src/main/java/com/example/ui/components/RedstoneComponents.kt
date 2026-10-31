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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.NetflixCardBorder
import com.example.ui.theme.NetflixDarkSurface
import com.example.ui.theme.NetflixDarkSurfaceVariant
import com.example.ui.theme.NetflixGray
import com.example.ui.theme.NetflixMuted
import com.example.ui.theme.NetflixOffWhite
import com.example.ui.theme.NetflixRed
import com.example.ui.theme.NetflixRedBright
import com.example.ui.theme.NetflixWhite
import com.example.ui.theme.SignalHigh
import com.example.ui.theme.SignalLow
import com.example.ui.theme.SignalMax
import com.example.ui.theme.SignalMid
import com.example.ui.theme.SignalOff

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

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = NetflixGray,
                letterSpacing = 1.sp
            )
            if (unit != null) {
                Text(
                    text = unit.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NetflixMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Decrement Button
            Surface(
                onClick = {
                    val next = (value - 1).coerceIn(min, max)
                    onValueChange(next)
                    textState = next.toString()
                },
                shape = RoundedCornerShape(6.dp),
                color = NetflixDarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, NetflixCardBorder),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = NetflixWhite)
                }
            }

            // Keyboard direct input field
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
                    color = NetflixWhite,
                    letterSpacing = 0.5.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NetflixRed,
                    unfocusedBorderColor = NetflixCardBorder,
                    focusedContainerColor = NetflixDarkSurfaceVariant,
                    unfocusedContainerColor = NetflixDarkSurfaceVariant,
                    cursorColor = NetflixRed
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("numeric_input_${label.lowercase().replace(" ", "_")}")
            )

            // Increment Button
            Surface(
                onClick = {
                    val next = (value + 1).coerceIn(min, max)
                    onValueChange(next)
                    textState = next.toString()
                },
                shape = RoundedCornerShape(6.dp),
                color = NetflixDarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, NetflixCardBorder),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = NetflixWhite)
                }
            }
        }

        if (helperText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = helperText,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = NetflixMuted
            )
        }

        if (quickPresets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickPresets.forEach { preset ->
                    Surface(
                        onClick = {
                            val next = preset.coerceIn(min, max)
                            onValueChange(next)
                            textState = next.toString()
                        },
                        shape = RoundedCornerShape(4.dp),
                        color = if (value == preset) NetflixRed.copy(alpha = 0.15f) else NetflixDarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (value == preset) NetflixRed else NetflixCardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = preset.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (value == preset) FontWeight.Bold else FontWeight.Normal,
                                color = if (value == preset) NetflixRedBright else NetflixOffWhite
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
                text = "SIGNAL LEVEL (0-15)",
                style = MaterialTheme.typography.labelSmall,
                color = NetflixGray,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (currentSignal > 0) NetflixRed else NetflixMuted)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SS $currentSignal  |  $currentSignal BLOCKS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (currentSignal > 0) NetflixWhite else NetflixMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(NetflixDarkSurfaceVariant)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (level in 1..15) {
                val isActive = level <= currentSignal
                val activeColor = when {
                    level == 15 -> SignalMax
                    level >= 12 -> SignalHigh
                    level >= 8 -> SignalMid
                    level >= 4 -> NetflixRed
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
                        .height(22.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(animatedColor)
                        .clickable(enabled = onSegmentClick != null) {
                            onSegmentClick?.invoke(level)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (level == 1 || level == 5 || level == 10 || level == 15) {
                        Text(
                            text = level.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = if (isActive) Color.White else NetflixMuted.copy(alpha = 0.5f),
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
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = NetflixDarkSurface
        ),
        shape = RoundedCornerShape(8.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(NetflixCardBorder),
            width = 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        color = NetflixWhite,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = NetflixGray
                        )
                    }
                }
                if (!trailingTag.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = NetflixDarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NetflixRed.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = trailingTag,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = NetflixRedBright,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
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
    val borderColor = if (isWarning) AccentAmber.copy(alpha = 0.5f) else NetflixCardBorder
    val accentColor = if (isWarning) AccentAmber else NetflixRed

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(6.dp),
        color = NetflixDarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NetflixOffWhite
                )
            }
        }
    }
}
