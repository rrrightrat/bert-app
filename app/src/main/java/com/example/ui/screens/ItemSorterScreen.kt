package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BedrockTipCard
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
fun ItemSorterScreen(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
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
            modifier = Modifier.testTag("sorter_tab_row")
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("FILTER LAYOUT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("THROUGHPUT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("NON-STACKABLE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> FilterLayoutSection(onSaveBlueprint)
            1 -> HopperThroughputSection(onSaveBlueprint)
            2 -> NonStackableSorterSection(onSaveBlueprint)
        }
    }
}

@Composable
private fun FilterLayoutSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var filterType by remember { mutableIntStateOf(0) } // 0 = Standard 41, 1 = Budget 18, 2 = 16-Stack
    var simulatedIncomingItems by remember { mutableIntStateOf(0) }

    val (targetItems, blockerItems, totalResting, activationItem, overflowItem, stackSize) = when (filterType) {
        0 -> FilterSpecs(41, 4, 45, 46, 69, 64)
        1 -> FilterSpecs(18, 4, 22, 23, 69, 64)
        else -> FilterSpecs(10, 4, 14, 15, 23, 16)
    }

    val currentTotal = totalResting + simulatedIncomingItems
    val currentWeight = if (stackSize == 16) {
        (currentTotal - 4) * 4 + 4
    } else {
        currentTotal
    }

    val currentSignal = when {
        currentWeight <= 0 -> 0
        currentWeight < 23 -> 1
        currentWeight < 46 -> 2
        currentWeight < 69 -> 3
        currentWeight < 92 -> 4
        else -> 5
    }

    SectionCard(
        title = "Hopper Filter Configurator",
        subtitle = "Standard ImpulseSV slice layout and overflow safety calculation",
        trailingTag = if (filterType == 0) "41 Standard" else if (filterType == 1) "18 Budget" else "16-Stack"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "41-ITEM STANDARD" to 0,
                "18-ITEM BUDGET" to 1,
                "16-STACK (PEARLS)" to 2
            ).forEach { (label, type) ->
                Surface(
                    onClick = { filterType = type; simulatedIncomingItems = 0 },
                    shape = RoundedCornerShape(4.dp),
                    color = if (filterType == type) NetflixRed.copy(alpha = 0.2f) else NetflixDarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (filterType == type) NetflixRed else NetflixCardBorder)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (filterType == type) FontWeight.Bold else FontWeight.Normal,
                        color = if (filterType == type) NetflixRedBright else NetflixOffWhite,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        RedstoneSignalBar(currentSignal = currentSignal)

        Spacer(modifier = Modifier.height(14.dp))

        val (stateText, stateColor) = when {
            currentSignal == 2 -> "RESTING STATE (Locked — Waiting for items)" to NetflixOffWhite
            currentSignal == 3 -> "ACTIVE STATE (Unlocked — Feeding items into storage)" to NetflixRedBright
            currentSignal >= 4 -> "OVERFLOW DANGER! (Signal bleeds into adjacent hopper slices)" to AccentAmber
            else -> "BELOW THRESHOLD" to NetflixMuted
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = NetflixDarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("STATUS: $stateText", fontWeight = FontWeight.Bold, color = stateColor)
                SorterRow("Slot 1 (Target Filter Item):", "$targetItems items")
                SorterRow("Slots 2, 3, 4, 5 (Renamed Blockers):", "1 item in each slot (4 total)")
                SorterRow("Resting Signal Strength:", "SS 2 ($totalResting items)")
                SorterRow("Activation Signal (Unlocks Hopper):", "SS 3 ($activationItem items)")
                SorterRow("Crosstalk Overflow Point:", "SS 4 ($overflowItem items)")
                SorterRow("Safe Buffer Capacity:", "${overflowItem - totalResting - 1} overflow items safe")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NumericInputField(
            value = simulatedIncomingItems,
            onValueChange = { simulatedIncomingItems = it },
            label = "Simulate Hopper Backlog (+Items)",
            min = 0,
            max = 64,
            unit = "+Items",
            quickPresets = listOf(0, 1, 5, 23, 24),
            helperText = "Type or test how many items can back up before signal reaches SS 4."
        )

        Spacer(modifier = Modifier.height(10.dp))

        BedrockTipCard(
            title = "Bedrock Overflow Rule",
            description = "In MCBE, always use 41 items in slot 1 for stackable items. The 18-item layout only has a 4-item buffer before crosstalk drains your neighboring hoppers if storage backs up!",
            isWarning = filterType == 1
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Button(
        onClick = {
            val title = if (filterType == 0) "41-Item ImpulseSV Filter" else if (filterType == 1) "18-Item Budget Filter" else "16-Stack Item Filter"
            val desc = "Sorter slice with $targetItems filter items and 4 blockers. Safe buffer: ${overflowItem - totalResting - 1} items"
            val config = "Target items: $targetItems\nBlockers: 4\nResting SS: 2\nUnlock SS: 3\nOverflow SS: 4 ($overflowItem items)"
            onSaveBlueprint(title, "SORTER", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
        shape = RoundedCornerShape(6.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = NetflixWhite)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save Filter Layout to Blueprints", color = NetflixWhite, fontWeight = FontWeight.Bold)
    }
}

private data class FilterSpecs(
    val targetItems: Int,
    val blockerItems: Int,
    val totalResting: Int,
    val activationItem: Int,
    val overflowItem: Int,
    val stackSize: Int
)

@Composable
private fun HopperThroughputSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var customTotalItems by remember { mutableIntStateOf(1728) } // default Shulker box
    var hopperConfiguration by remember { mutableIntStateOf(0) } // 0 = Single, 1 = Double, 2 = Minecart

    val (hopperName, itemsPerSecond) = when (hopperConfiguration) {
        0 -> "Single Hopper (4 RT / 8 GT per item)" to 2.5
        1 -> "Double Parallel Hoppers (2x speed)" to 5.0
        else -> "Minecart with Hopper (1 GT per item)" to 20.0
    }

    val transferSeconds = customTotalItems / itemsPerSecond
    val transferMinutes = transferSeconds / 60.0

    SectionCard(
        title = "Hopper Throughput & Transfer Time",
        subtitle = "Calculate storage fill and drain rates",
        trailingTag = "${String.format("%.1f", transferMinutes)} min"
    ) {
        NumericInputField(
            value = customTotalItems,
            onValueChange = { customTotalItems = it },
            label = "Total Items to Transfer",
            min = 1,
            max = 100000,
            unit = "Items",
            quickPresets = listOf(64, 1728, 3456, 10000),
            helperText = "1,728 items = 1 Shulker Box / Single Chest. 3,456 items = Double Chest."
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("HOPPER FEED MECHANISM", style = MaterialTheme.typography.labelSmall, color = NetflixGray)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "1x HOPPER (2.5/s)" to 0,
                "2x HOPPERS (5.0/s)" to 1,
                "MINECART HOPPER (20/s)" to 2
            ).forEach { (label, config) ->
                Surface(
                    onClick = { hopperConfiguration = config },
                    shape = RoundedCornerShape(4.dp),
                    color = if (hopperConfiguration == config) NetflixRed.copy(alpha = 0.2f) else NetflixDarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (hopperConfiguration == config) NetflixRed else NetflixCardBorder)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (hopperConfiguration == config) FontWeight.Bold else FontWeight.Normal,
                        color = if (hopperConfiguration == config) NetflixRedBright else NetflixOffWhite,
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
                SorterRow("Total Items to Transfer:", "$customTotalItems items (${customTotalItems / 64} stacks + ${customTotalItems % 64})")
                SorterRow("Transfer Rate:", "$itemsPerSecond items/sec (${(itemsPerSecond * 60).toInt()} items/min)")
                SorterRow("Total Duration in Seconds:", "${transferSeconds.toInt()} seconds")
                SorterRow("Total Duration in Minutes:", "${String.format("%.2f", transferMinutes)} minutes")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        BedrockTipCard(
            title = "Minecart Hopper Suction",
            description = "Minecart Hoppers in Bedrock pull items at 1 item per Game Tick (20 items/s), allowing instant collection through solid blocks and honey blocks with zero item loss!"
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Button(
        onClick = {
            val title = "$customTotalItems items via $hopperName"
            val desc = "$customTotalItems items at $itemsPerSecond items/s (${String.format("%.1f", transferMinutes)}m)"
            val config = "Items: $customTotalItems\nRate: $itemsPerSecond/s\nDuration: ${String.format("%.2f", transferMinutes)} minutes"
            onSaveBlueprint(title, "SORTER", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
        shape = RoundedCornerShape(6.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = NetflixWhite)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save Throughput Specs to Blueprints", color = NetflixWhite, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NonStackableSorterSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    SectionCard(
        title = "Bedrock Non-Stackable Item Sorting",
        subtitle = "Swords, armor, potions, bows, and enchanted books"
    ) {
        Text("Signal Output for Non-Stackable in Hopper:", style = MaterialTheme.typography.labelSmall, color = NetflixGray)
        Spacer(modifier = Modifier.height(6.dp))
        RedstoneSignalBar(currentSignal = 3)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "When a single non-stackable item enters an empty 5-slot hopper, it fills 64/320 weight (20%), instantly producing Signal Strength 3 (compared to SS 1 for a single 64-stack item).",
            style = MaterialTheme.typography.bodySmall,
            color = NetflixOffWhite
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = NetflixDarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("BEDROCK METHODS FOR NON-STACKABLES", style = MaterialTheme.typography.labelSmall, color = NetflixRedBright)
                Text("1. Allay Sorting: Give an Allay the target item. Allays pick up duplicate items and deliver to a Note Block.", style = MaterialTheme.typography.bodySmall, color = NetflixOffWhite)
                Text("2. Potion Brewing Sorter: Fill a brewing stand with water bottles. Potions with effects filter through.", style = MaterialTheme.typography.bodySmall, color = NetflixOffWhite)
                Text("3. Shulker Box Separation: Dispensers place Shulker Boxes; items pass into hoppers while shulkers are broken by a piston.", style = MaterialTheme.typography.bodySmall, color = NetflixOffWhite)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        BedrockTipCard(
            title = "Dropper Elevator Tick Warning",
            description = "In Bedrock Edition, pulsing a vertical dropper item elevator faster than 3 redstone ticks per pulse causes droppers to desync and lock up due to random update order. Use a 4-tick repeater clock or torch burnout-safe ladder!"
        )
    }
}

@Composable
private fun SorterRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = NetflixGray)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NetflixOffWhite)
    }
}
