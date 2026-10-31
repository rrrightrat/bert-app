package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.NetflixCardBorder
import com.example.ui.theme.NetflixDarkSurface
import com.example.ui.theme.NetflixDarkSurfaceVariant
import com.example.ui.theme.NetflixGray
import com.example.ui.theme.NetflixMuted
import com.example.ui.theme.NetflixOffWhite
import com.example.ui.theme.NetflixRed
import com.example.ui.theme.NetflixRedBright
import com.example.ui.theme.NetflixWhite

@Composable
fun LogicScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NetflixDarkSurface,
            contentColor = NetflixRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NetflixRed
                )
            },
            modifier = Modifier.testTag("logic_tab_row")
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("BASE CONVERTER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("LOGIC GATES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

        Text("8-BIT BUS SWITCHES (CLICK BIT TO FLIP)", style = MaterialTheme.typography.labelSmall, color = NetflixGray)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (bit in 7 downTo 0) {
                val isBitSet = (byteValue and (1 shl bit)) != 0
                val bitWeight = 1 shl bit

                val bgColor by animateColorAsState(
                    targetValue = if (isBitSet) NetflixRed else NetflixDarkSurfaceVariant,
                    label = "bitBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(bgColor)
                        .border(1.dp, if (isBitSet) NetflixRedBright else NetflixCardBorder, RoundedCornerShape(4.dp))
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
                            color = if (isBitSet) Color.White else NetflixMuted
                        )
                        Text(
                            text = "b$bit ($bitWeight)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                            color = if (isBitSet) Color.White.copy(alpha = 0.8f) else NetflixMuted.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = NetflixDarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BaseRow("Decimal (Base 10):", "$byteValue")
                BaseRow("Hexadecimal (Base 16):", "0x" + byteValue.toString(16).uppercase().padStart(2, '0'))
                BaseRow("Binary (Base 2):", byteValue.toString(2).padStart(8, '0').chunked(4).joinToString(" "))
                BaseRow("Analog Signal (Lower 4-Bit):", "SS $analogSignal / 15")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            gates.forEachIndexed { index, item ->
                Surface(
                    onClick = { selectedGateIndex = index },
                    shape = RoundedCornerShape(4.dp),
                    color = if (selectedGateIndex == index) NetflixRed.copy(alpha = 0.2f) else NetflixDarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedGateIndex == index) NetflixRed else NetflixCardBorder)
                ) {
                    Text(
                        text = item.name.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selectedGateIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedGateIndex == index) NetflixRedBright else NetflixOffWhite,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = NetflixDarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(gate.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NetflixRedBright)
                    Text(gate.expression, style = MaterialTheme.typography.labelMedium, color = NetflixOffWhite, fontWeight = FontWeight.Bold)
                }

                Text(gate.description, style = MaterialTheme.typography.bodySmall, color = NetflixGray)

                Spacer(modifier = Modifier.height(4.dp))
                Text("TRUTH TABLE", style = MaterialTheme.typography.labelSmall, color = NetflixRedBright)
                gate.truthTable.forEach { (input, output) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(input, style = MaterialTheme.typography.bodySmall, color = NetflixOffWhite)
                        Text(output, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NetflixRedBright)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("BEDROCK CONSTRUCTION", style = MaterialTheme.typography.labelSmall, color = NetflixRedBright)
                Text(gate.bedrockComponentList, style = MaterialTheme.typography.bodySmall, color = NetflixOffWhite)

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Propagation Delay:", style = MaterialTheme.typography.bodySmall, color = NetflixGray)
                    Text(gate.delayTicks, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NetflixWhite)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Typical In-Game Usage:", style = MaterialTheme.typography.labelSmall, color = NetflixGray)
                Text(gate.commonUsage, style = MaterialTheme.typography.bodySmall, color = NetflixOffWhite)
            }
        }
    }
}

@Composable
private fun BaseRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = NetflixGray)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NetflixOffWhite)
    }
}
