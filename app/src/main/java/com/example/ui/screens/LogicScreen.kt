package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BedrockKnowledgeBase
import com.example.ui.components.NumericInputField
import com.example.ui.components.RedstoneSignalBar
import com.example.ui.components.SectionCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GrayText
import com.example.ui.theme.MutedText
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White

@Composable
fun LogicScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = PurpleBright,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PurplePrimary
                    )
                },
                modifier = Modifier.testTag("logic_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "BASE CONVERTER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) PurpleBright else GrayText
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "LOGIC GATES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) PurpleBright else GrayText
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
            0 -> BinaryConverterSection()
            1 -> LogicGatesSection()
        }
    }
}

@Composable
private fun BinaryConverterSection() {
    var byteValue by remember { mutableIntStateOf(15) }

    val analogSignal = byteValue and 0x0F

    SectionCard(
        title = "8-Bit Computational Redstone Converter",
        subtitle = "Type decimal byte or click switches to toggle individual bits",
        trailingTag = "DEC $byteValue"
    ) {
        NumericInputField(
            value = byteValue,
            onValueChange = { byteValue = it.coerceIn(0, 255) },
            label = "Decimal Value (0 to 255)",
            min = 0,
            max = 255,
            unit = "Byte",
            quickPresets = listOf(0, 1, 15, 64, 128, 255),
            helperText = "Type any decimal value or toggle individual bits below."
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text("8-BIT BUS SWITCHES (CLICK BIT TO FLIP)", style = MaterialTheme.typography.labelSmall, color = GrayText)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (bit in 7 downTo 0) {
                val isBitSet = (byteValue and (1 shl bit)) != 0
                val bitWeight = 1 shl bit

                val bgColor by animateColorAsState(
                    targetValue = if (isBitSet) PurplePrimary else DarkSurfaceVariant,
                    label = "bitBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .border(
                            1.dp,
                            if (isBitSet) PurpleBright else DarkCardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            byteValue = if (isBitSet) {
                                byteValue and (1 shl bit).inv()
                            } else {
                                byteValue or (1 shl bit)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBitSet) "1" else "0",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isBitSet) White else MutedText
                        )
                        Text(
                            text = "b$bit ($bitWeight)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = if (isBitSet) White.copy(alpha = 0.85f) else MutedText.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BaseRow("Decimal (Base 10):", "$byteValue")
                BaseRow("Hexadecimal (Base 16):", "0x" + byteValue.toString(16).uppercase().padStart(2, '0'))
                BaseRow("Binary (Base 2):", byteValue.toString(2).padStart(8, '0').chunked(4).joinToString(" "))
                BaseRow("Analog Signal (Lower 4-Bit):", "SS $analogSignal / 15")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        RedstoneSignalBar(currentSignal = analogSignal)
    }
}

@Composable
private fun LogicGatesSection() {
    val gates = BedrockKnowledgeBase.LOGIC_GATES
    var selectedGateIndex by remember { mutableIntStateOf(0) }
    val gate = gates[selectedGateIndex]

    SectionCard(title = "Bedrock Logic Gate Library", subtitle = "Standard gates, flip-flops, and memory cells") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gates.forEachIndexed { index, item ->
                val isSelected = selectedGateIndex == index
                Surface(
                    onClick = { selectedGateIndex = index },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PurplePrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) PurplePrimary else DarkCardBorder)
                ) {
                    Text(
                        text = item.name.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PurpleBright else OffWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(gate.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PurpleBright)
                    Text(gate.expression, style = MaterialTheme.typography.labelMedium, color = OffWhite, fontWeight = FontWeight.Bold)
                }

                Text(gate.description, style = MaterialTheme.typography.bodyMedium, color = GrayText)

                Spacer(modifier = Modifier.height(6.dp))
                Text("TRUTH TABLE", style = MaterialTheme.typography.labelSmall, color = PurpleBright, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        gate.truthTable.forEach { (input, output) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(input, style = MaterialTheme.typography.bodySmall, color = OffWhite)
                                Text(output, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PurpleBright)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("BEDROCK CONSTRUCTION", style = MaterialTheme.typography.labelSmall, color = PurpleBright, fontWeight = FontWeight.Bold)
                Text(gate.bedrockComponentList, style = MaterialTheme.typography.bodySmall, color = OffWhite)

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Propagation Delay:", style = MaterialTheme.typography.bodySmall, color = GrayText)
                    Text(gate.delayTicks, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = White)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("Typical In-Game Usage:", style = MaterialTheme.typography.labelSmall, color = GrayText)
                Text(gate.commonUsage, style = MaterialTheme.typography.bodySmall, color = OffWhite)
            }
        }
    }
}

@Composable
private fun BaseRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = GrayText)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OffWhite)
    }
}
