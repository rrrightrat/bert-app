package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RedstoneMath
import com.example.ui.components.BedrockTipCard
import com.example.ui.components.NumericInputField
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
fun TimingScreen(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = PurplePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PurplePrimary
                )
            },
            modifier = Modifier.testTag("timing_tab_row")
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("ETHO CLOCK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("REPEATERS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("TICK CONV", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("EXTENDER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) })
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
            0 -> EthoClockSection(onSaveBlueprint)
            1 -> RepeaterChainSection(onSaveBlueprint)
            2 -> TickConverterSection()
            3 -> PulseExtenderSection(onSaveBlueprint)
        }
    }
}

@Composable
private fun EthoClockSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var isInputModeItems by remember { mutableStateOf(true) }
    var itemCount by remember { mutableIntStateOf(38) }
    var targetSecondsInt by remember { mutableIntStateOf(30) }

    val clockResult = if (isInputModeItems) {
        RedstoneMath.calculateEthoClockByItems(itemCount)
    } else {
        RedstoneMath.calculateEthoClockByTargetSeconds(targetSecondsInt.toDouble())
    }

    SectionCard(
        title = "Etho Hopper Clock Calculator",
        subtitle = "Bedrock's most reliable long-period timer",
        trailingTag = "${clockResult.items} Items"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                onClick = { isInputModeItems = true },
                shape = RoundedCornerShape(8.dp),
                color = if (isInputModeItems) PurpleContainer else DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isInputModeItems) PurplePrimary else DarkCardBorder),
                modifier = Modifier.weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        "BY ITEM COUNT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isInputModeItems) FontWeight.Bold else FontWeight.Medium,
                        color = if (isInputModeItems) PurpleBright else OffWhite
                    )
                }
            }

            Surface(
                onClick = { isInputModeItems = false },
                shape = RoundedCornerShape(8.dp),
                color = if (!isInputModeItems) PurpleContainer else DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (!isInputModeItems) PurplePrimary else DarkCardBorder),
                modifier = Modifier.weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        "BY TARGET SECONDS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (!isInputModeItems) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isInputModeItems) PurpleBright else OffWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (isInputModeItems) {
            NumericInputField(
                value = itemCount,
                onValueChange = { itemCount = it },
                label = "Items in Hopper",
                min = 1,
                max = 320,
                unit = "Items",
                quickPresets = listOf(1, 10, 38, 75, 150, 320),
                helperText = "Type any item count. 38 items = ~30s full cycle. 75 items = 1 minute."
            )
        } else {
            NumericInputField(
                value = targetSecondsInt,
                onValueChange = { targetSecondsInt = it },
                label = "Desired Cycle Duration",
                min = 1,
                max = 3600,
                unit = "Seconds",
                quickPresets = listOf(15, 30, 60, 120, 300),
                helperText = "Type target seconds. 300s = 5-minute item despawn cycle."
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TimingRow("Item Count Required:", "${clockResult.items} items (${clockResult.items / 64} stacks + ${clockResult.items % 64})")
                TimingRow("Full Cycle Period:", "${String.format("%.2f", clockResult.fullCycleSeconds)}s (${clockResult.fullCycleRedstoneTicks} RT / ${clockResult.fullCycleGameTicks} GT)")
                TimingRow("One-Way Transfer Time:", "${String.format("%.2f", clockResult.oneWaySeconds)}s (${clockResult.oneWayRedstoneTicks} RT / ${clockResult.oneWayGameTicks} GT)")
                TimingRow("Frequency:", "${String.format("%.3f", clockResult.frequencyHz)} Hz")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BedrockTipCard(
            title = "Bedrock Chunk-Border Rule",
            description = "To avoid Etho clocks jamming during player travel in MCBE, place both hoppers completely within the same 16x16 chunk border!"
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = {
            val title = "Etho Clock (${String.format("%.1f", clockResult.fullCycleSeconds)}s)"
            val desc = "${clockResult.items} items in Etho Hopper Clock"
            val config = "Items: ${clockResult.items}\nFull Cycle: ${String.format("%.2f", clockResult.fullCycleSeconds)}s\nOne-Way: ${String.format("%.2f", clockResult.oneWaySeconds)}s\nRedstone Ticks: ${clockResult.fullCycleRedstoneTicks} RT"
            onSaveBlueprint(title, "TIMING", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Save Clock Setup to Blueprints", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun RepeaterChainSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var targetTicks by remember { mutableIntStateOf(14) }

    val result = RedstoneMath.calculateRepeaterChain(targetTicks)

    SectionCard(
        title = "Repeater Delay Chain Solver",
        subtitle = "Optimal layout of 4, 3, 2, 1-tick repeaters",
        trailingTag = "${result.totalRepeaters} Repeaters"
    ) {
        NumericInputField(
            value = targetTicks,
            onValueChange = { targetTicks = it },
            label = "Target Delay (Redstone Ticks)",
            min = 1,
            max = 200,
            unit = "RT (1 RT = 0.1s)",
            quickPresets = listOf(2, 4, 8, 14, 20, 40),
            helperText = "Type any target delay in ticks. E.g. 2 RT for sticky pistons, 40 RT for TNT fuse."
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("OPTIMAL REPEATER COMBINATION", style = MaterialTheme.typography.labelSmall, color = PurpleBright)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    RepeaterCountPill("4-TICK", result.count4Tick)
                    RepeaterCountPill("3-TICK", result.count3Tick)
                    RepeaterCountPill("2-TICK", result.count2Tick)
                    RepeaterCountPill("1-TICK", result.count1Tick)
                }

                Spacer(modifier = Modifier.height(6.dp))
                TimingRow("Total Repeaters Required:", "${result.totalRepeaters} components")
                TimingRow("Exact Redstone Delay:", "${result.actualTicks} RT (${result.actualTicks * 2} Game Ticks)")
                TimingRow("Real-Time Duration:", "${String.format("%.2f", result.delaySeconds)} seconds")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BedrockTipCard(
            title = "Bedrock Repeater Routing",
            description = "Repeaters in Bedrock can be placed directly on non-solid blocks (upside-down stairs and top slabs) to route vertical delay lines cleanly."
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = {
            val title = "${result.actualTicks} RT Repeater Chain (${String.format("%.1f", result.delaySeconds)}s)"
            val desc = "${result.totalRepeaters} repeaters: ${result.count4Tick}x 4-tick, ${result.count3Tick}x 3-tick, ${result.count2Tick}x 2-tick, ${result.count1Tick}x 1-tick"
            val config = "Target: ${result.targetTicks} RT\nRepeaters: ${result.totalRepeaters}\n4-tick: ${result.count4Tick}\n3-tick: ${result.count3Tick}\n2-tick: ${result.count2Tick}\n1-tick: ${result.count1Tick}"
            onSaveBlueprint(title, "TIMING", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Save Repeater Chain to Blueprints", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun TickConverterSection() {
    var rawSecondsInt by remember { mutableIntStateOf(1) }

    val rawSec = rawSecondsInt.toDouble()
    val gt = (rawSec * 20.0).toLong()
    val rt = (rawSec * 10.0).toLong()
    val ms = (rawSec * 1000.0).toLong()

    SectionCard(title = "Universal Tick & Engine Converter", subtitle = "Convert between real-time seconds, GT, and RT") {
        NumericInputField(
            value = rawSecondsInt,
            onValueChange = { rawSecondsInt = it },
            label = "Real-Time Duration",
            min = 1,
            max = 600,
            unit = "Seconds",
            quickPresets = listOf(1, 2, 4, 10, 30, 60),
            helperText = "Type seconds to convert instantly into Minecraft ticks."
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TimingRow("Game Ticks (GT):", "$gt ticks (50ms base engine tick)")
                TimingRow("Redstone Ticks (RT):", "$rt ticks (100ms redstone tick)")
                TimingRow("Milliseconds (ms):", "$ms ms")
                TimingRow("Observed Frequency:", "${String.format("%.2f", 1.0 / rawSec)} Hz")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BedrockTipCard(
            title = "Torch Burnout Warning",
            description = "If a redstone torch is toggled faster than 8 times within 60 game ticks (3.0s), it burns out and drops into an idle state for 100 game ticks in MCBE!",
            isWarning = true
        )
    }
}

@Composable
private fun PulseExtenderSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var initialSignal by remember { mutableIntStateOf(15) }
    var wireLength by remember { mutableIntStateOf(1) }

    val faderResult = RedstoneMath.calculateComparatorFader(initialSignal, wireLength)

    SectionCard(
        title = "Comparator Pulse Extender (Fader)",
        subtitle = "Two comparators in decay loop",
        trailingTag = "${String.format("%.1f", faderResult.totalSeconds)}s"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            NumericInputField(
                value = initialSignal,
                onValueChange = { initialSignal = it },
                label = "Initial Signal Strength",
                min = 1,
                max = 15,
                unit = "SS 1-15",
                quickPresets = listOf(5, 10, 15)
            )

            NumericInputField(
                value = wireLength,
                onValueChange = { wireLength = it },
                label = "Wire Length in Loop",
                min = 1,
                max = 5,
                unit = "Blocks",
                quickPresets = listOf(1, 2, 3)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TimingRow("Decay Iteration Steps:", "${faderResult.decaySteps} drops")
                TimingRow("Total Pulse Duration:", "${String.format("%.2f", faderResult.totalSeconds)} seconds")
                TimingRow("Total Redstone Ticks:", "${faderResult.totalRedstoneTicks} RT (${faderResult.totalRedstoneTicks * 2} GT)")
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = {
            val title = "Comparator Pulse Extender (${String.format("%.1f", faderResult.totalSeconds)}s)"
            val desc = "Pulse extender using $initialSignal initial signal and $wireLength wire length"
            val config = "Initial SS: $initialSignal\nWire: $wireLength\nDuration: ${String.format("%.2f", faderResult.totalSeconds)}s\nTicks: ${faderResult.totalRedstoneTicks} RT"
            onSaveBlueprint(title, "TIMING", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Save Extender Setup to Blueprints", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun TimingRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = GrayText)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = OffWhite)
    }
}

@Composable
private fun RepeaterCountPill(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MutedText)
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            color = if (count > 0) PurpleBright else MutedText.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold
        )
    }
}
