package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.ContainerType
import com.example.data.model.RedstoneMath
import com.example.ui.components.BedrockTipCard
import com.example.ui.components.NumericInputField
import com.example.ui.components.RedstoneSignalBar
import com.example.ui.components.SectionCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GrayText
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparatorScreen(
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
        // Spacious Tab Bar
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
            modifier = Modifier.testTag("comparator_tab_row")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("CONTAINER CALC", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("TARGET SOLVER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("MODES SIM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
            0 -> ContainerAnalyzerSection(onSaveBlueprint)
            1 -> TargetSignalSolverSection(onSaveBlueprint)
            2 -> ComparatorModesSection()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContainerAnalyzerSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedContainer by remember { mutableStateOf(ContainerType.HOPPER) }

    // Direct numeric keyboard inputs
    var count64 by remember { mutableIntStateOf(45) }
    var count16 by remember { mutableIntStateOf(0) }
    var count1 by remember { mutableIntStateOf(0) }

    // Special containers
    var crafterDisabledSlots by remember { mutableIntStateOf(5) }
    var lecternPage by remember { mutableIntStateOf(1) }
    var lecternTotalPages by remember { mutableIntStateOf(15) }
    var composterLevel by remember { mutableIntStateOf(7) }
    var cauldronLevel by remember { mutableIntStateOf(3) }
    var chiseledSlot by remember { mutableIntStateOf(4) }
    var anchorCharges by remember { mutableIntStateOf(4) }
    var cakeSlices by remember { mutableIntStateOf(7) }

    // Spacious Container Selector
    ExposedDropdownMenuBox(
        expanded = expandedDropdown,
        onExpandedChange = { expandedDropdown = !expandedDropdown },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedContainer.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("CONTAINER TO MEASURE", style = MaterialTheme.typography.labelSmall, color = GrayText) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = DarkCardBorder,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = White,
                unfocusedTextColor = White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .menuAnchor()
                .testTag("container_select_field")
        )
        ExposedDropdownMenu(
            expanded = expandedDropdown,
            onDismissRequest = { expandedDropdown = false }
        ) {
            ContainerType.values().forEach { container ->
                DropdownMenuItem(
                    text = {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(container.displayName, fontWeight = FontWeight.Bold, color = White)
                            if (container.isItemContainer) {
                                Text("${container.slots} slots (max ${container.slots * 64} weight capacity)", style = MaterialTheme.typography.bodySmall, color = GrayText)
                            } else {
                                Text(container.description, style = MaterialTheme.typography.bodySmall, color = GrayText)
                            }
                        }
                    },
                    onClick = {
                        selectedContainer = container
                        expandedDropdown = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Perform calculation
    val result = if (selectedContainer.isItemContainer) {
        RedstoneMath.calculateItemContainerSignal(
            container = selectedContainer,
            items64 = count64,
            items16 = count16,
            nonStackables = count1
        )
    } else {
        val signal = when (selectedContainer) {
            ContainerType.CRAFTER -> crafterDisabledSlots
            ContainerType.LECTERN -> RedstoneMath.calculateLecternSignal(lecternPage, lecternTotalPages)
            ContainerType.COMPOSTER -> composterLevel
            ContainerType.CAULDRON -> cauldronLevel
            ContainerType.CHISELED_BOOKSHELF -> chiseledSlot
            ContainerType.RESPAWN_ANCHOR -> anchorCharges
            ContainerType.CAKE -> cakeSlices * 2
            else -> 1
        }
        val pct = (signal.toDouble() / 15.0) * 100.0
        com.example.data.model.ComparatorCalculationResult(
            signalStrength = signal,
            fullnessPercentage = pct,
            totalWeight = signal,
            maxWeight = 15,
            itemsUntilNextSignal = null,
            formulaExplanation = "Direct state mapping: ${selectedContainer.displayName} -> Signal Strength $signal"
        )
    }

    // Output Signal Card
    SectionCard(
        title = "Comparator Output",
        trailingTag = "SS ${result.signalStrength}"
    ) {
        RedstoneSignalBar(currentSignal = result.signalStrength)

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("SIGNAL STRENGTH", style = MaterialTheme.typography.labelSmall, color = GrayText)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${result.signalStrength} / 15",
                    style = MaterialTheme.typography.displaySmall,
                    color = PurpleBright,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("CONTAINER FULLNESS", style = MaterialTheme.typography.labelSmall, color = GrayText)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = String.format("%.1f%%", result.fullnessPercentage),
                    style = MaterialTheme.typography.displaySmall,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("FORMULA & BREAKDOWN", style = MaterialTheme.typography.labelSmall, color = PurpleBright)
                Text(result.formulaExplanation, style = MaterialTheme.typography.bodyMedium, color = OffWhite)
                if (result.itemsUntilNextSignal != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Next SS ${result.signalStrength + 1}: Enter +${result.itemsUntilNextSignal} item(s) of 64-stack (or ${Math.ceil(result.itemsUntilNextSignal / 4.0).toInt()} of 16-stack)",
                        style = MaterialTheme.typography.bodySmall,
                        color = PurpleBright,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Keyboard Numeric Inputs with spacious layout
    if (selectedContainer.isItemContainer) {
        SectionCard(title = "Inventory Contents", subtitle = "Direct keyboard entry with quick presets") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NumericInputField(
                    value = count64,
                    onValueChange = { count64 = it },
                    label = "64-Stack Items (e.g. Redstone, Stone, Iron)",
                    unit = "1 item = 1 wt",
                    quickPresets = listOf(1, 16, 41, 45, 64, 320),
                    helperText = "Enter any integer. 45 items is ImpulseSV resting state, 46 is unlock threshold."
                )

                NumericInputField(
                    value = count16,
                    onValueChange = { count16 = it },
                    label = "16-Stack Items (Ender Pearls, Eggs, Snowballs)",
                    unit = "1 item = 4 wt",
                    quickPresets = listOf(1, 4, 10, 16),
                    helperText = "Each 16-stack item provides 4x comparator weight."
                )

                NumericInputField(
                    value = count1,
                    onValueChange = { count1 = it },
                    label = "Non-Stackable Items (Swords, Shulker Boxes, Potions)",
                    unit = "1 item = 64 wt",
                    quickPresets = listOf(1, 2, 3, 5),
                    helperText = "Each non-stackable item fills a full stack weight (64)."
                )
            }
        }
    } else {
        SectionCard(title = "${selectedContainer.displayName} Configuration") {
            when (selectedContainer) {
                ContainerType.CRAFTER -> {
                    NumericInputField(
                        value = crafterDisabledSlots,
                        onValueChange = { crafterDisabledSlots = it },
                        label = "Disabled Crafting Slots",
                        min = 0,
                        max = 9,
                        unit = "0 to 9 slots",
                        quickPresets = listOf(0, 1, 3, 5, 9),
                        helperText = "In MCBE, Crafters output SS equal to disabled slots."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BedrockTipCard(
                        title = "Crafter Advantage",
                        description = "In Bedrock, a Crafter with toggled slots provides a 100% constant redstone signal that cannot be drained by hoppers, and it can also be pushed by pistons!"
                    )
                }
                ContainerType.LECTERN -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        NumericInputField(
                            value = lecternPage,
                            onValueChange = { lecternPage = it },
                            label = "Current Open Page",
                            min = 1,
                            max = lecternTotalPages,
                            unit = "Page",
                            quickPresets = listOf(1, 5, 10, 15)
                        )
                        NumericInputField(
                            value = lecternTotalPages,
                            onValueChange = { lecternTotalPages = it },
                            label = "Total Pages in Written Book",
                            min = 1,
                            max = 100,
                            unit = "Pages",
                            quickPresets = listOf(15, 16, 30, 100)
                        )
                    }
                }
                ContainerType.COMPOSTER -> {
                    NumericInputField(
                        value = composterLevel,
                        onValueChange = { composterLevel = it },
                        label = "Compost Fill Level",
                        min = 0,
                        max = 8,
                        unit = "0-8",
                        quickPresets = listOf(0, 2, 4, 7, 8)
                    )
                }
                ContainerType.CAULDRON -> {
                    NumericInputField(
                        value = cauldronLevel,
                        onValueChange = { cauldronLevel = it },
                        label = "Fluid Level",
                        min = 0,
                        max = 3,
                        unit = "0-3",
                        quickPresets = listOf(0, 1, 2, 3)
                    )
                }
                ContainerType.CHISELED_BOOKSHELF -> {
                    NumericInputField(
                        value = chiseledSlot,
                        onValueChange = { chiseledSlot = it },
                        label = "Last Interacted Book Slot",
                        min = 1,
                        max = 6,
                        unit = "Slot 1-6",
                        quickPresets = listOf(1, 2, 3, 4, 5, 6)
                    )
                }
                ContainerType.RESPAWN_ANCHOR -> {
                    NumericInputField(
                        value = anchorCharges,
                        onValueChange = { anchorCharges = it },
                        label = "Glowstone Charges",
                        min = 0,
                        max = 4,
                        unit = "Charges 0-4",
                        quickPresets = listOf(0, 1, 2, 3, 4)
                    )
                }
                ContainerType.CAKE -> {
                    NumericInputField(
                        value = cakeSlices,
                        onValueChange = { cakeSlices = it },
                        label = "Remaining Cake Slices",
                        min = 1,
                        max = 7,
                        unit = "Slices 1-7",
                        quickPresets = listOf(1, 2, 4, 7)
                    )
                }
                else -> {}
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = {
            val title = "${selectedContainer.displayName} SS ${result.signalStrength}"
            val desc = "Signal Strength ${result.signalStrength} output from ${selectedContainer.displayName}"
            val config = "Container: ${selectedContainer.displayName}\nSignal: ${result.signalStrength}\n64-items: $count64\n16-items: $count16\nNon-stackable: $count1\nFullness: ${String.format("%.1f", result.fullnessPercentage)}%"
            onSaveBlueprint(title, "COMPARATOR", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("save_comparator_blueprint_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Save Setup as Blueprint", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun TargetSignalSolverSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var targetSignal by remember { mutableIntStateOf(3) }
    var container by remember { mutableStateOf(ContainerType.HOPPER) }

    SectionCard(title = "Target Signal Selector", subtitle = "Type or select desired analog redstone power (0-15)") {
        NumericInputField(
            value = targetSignal,
            onValueChange = { targetSignal = it },
            label = "Desired Signal Strength",
            min = 0,
            max = 15,
            unit = "SS 0 to 15",
            quickPresets = listOf(1, 2, 3, 5, 10, 15),
            helperText = "Type any signal level from 0 to 15."
        )

        Spacer(modifier = Modifier.height(16.dp))

        RedstoneSignalBar(
            currentSignal = targetSignal,
            onSegmentClick = { targetSignal = it }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text("CONTAINER TYPE", style = MaterialTheme.typography.titleSmall, color = White)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                ContainerType.HOPPER,
                ContainerType.DROPPER_DISPENSER,
                ContainerType.BARREL,
                ContainerType.CHEST,
                ContainerType.DOUBLE_CHEST,
                ContainerType.FURNACE
            ).forEach { c ->
                val isSelected = container == c
                Surface(
                    onClick = { container = c },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PurpleContainer else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) PurplePrimary else DarkCardBorder
                    )
                ) {
                    Text(
                        text = c.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) PurpleBright else OffWhite,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    val breakdown = RedstoneMath.calculateTargetSignalRequirements(container, targetSignal)

    SectionCard(title = "Item Requirements Breakdown", trailingTag = "Exact Solver") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RequirementRow("Minimum Item Weight:", "${breakdown.minWeightNeeded} weight")
            RequirementRow("Maximum Weight Before SS ${targetSignal + 1}:", "${breakdown.maxWeightForSignal} weight")
            RequirementRow("Using 64-Stack Items:", "${breakdown.asStack64} items (${breakdown.asStack64 / 64} stacks + ${breakdown.asStack64 % 64})")
            RequirementRow("Using 16-Stack Items:", "${breakdown.asStack16} items (eggs/pearls)")
            RequirementRow("Using Non-Stackable Items:", "${breakdown.asNonStackable} item(s) = ${breakdown.asNonStackable * 64} wt")

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("RECOMMENDED SLOT LAYOUT", style = MaterialTheme.typography.labelSmall, color = PurpleBright)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(breakdown.slotSuggestion, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = White)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = {
            val title = "${container.displayName} SS $targetSignal Solver"
            val desc = "Setup for Signal Strength $targetSignal: ${breakdown.minWeightNeeded} weight required"
            val config = "Target Signal: $targetSignal\nContainer: ${container.displayName}\nMin items (64): ${breakdown.asStack64}\nMin items (16): ${breakdown.asStack16}\nLayout: ${breakdown.slotSuggestion}"
            onSaveBlueprint(title, "COMPARATOR", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Save Solver Setup to Blueprints", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun ComparatorModesSection() {
    var rearSignal by remember { mutableIntStateOf(10) }
    var sideSignal by remember { mutableIntStateOf(6) }
    var isSubtractionMode by remember { mutableStateOf(false) }

    val outputSignal = if (isSubtractionMode) {
        Math.max(0, rearSignal - sideSignal)
    } else {
        if (rearSignal >= sideSignal) rearSignal else 0
    }

    SectionCard(
        title = if (isSubtractionMode) "Subtraction Mode (Front Torch Lit)" else "Comparison Mode (Front Torch Unlit)",
        subtitle = "Toggle comparator mode to inspect output behavior"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isSubtractionMode = !isSubtractionMode },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSubtractionMode) PurplePrimary else DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isSubtractionMode) "MODE: SUBTRACT (-)" else "MODE: COMPARE (>=)",
                    fontWeight = FontWeight.Bold,
                    color = if (isSubtractionMode) Color.Black else OffWhite
                )
            }

            Text(
                text = "OUTPUT: SS $outputSignal",
                style = MaterialTheme.typography.titleMedium,
                color = PurpleBright,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RedstoneSignalBar(currentSignal = outputSignal)

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            NumericInputField(
                value = rearSignal,
                onValueChange = { rearSignal = it },
                label = "Rear Input (Main Signal)",
                min = 0,
                max = 15,
                unit = "SS 0-15",
                quickPresets = listOf(0, 5, 10, 15)
            )

            NumericInputField(
                value = sideSignal,
                onValueChange = { sideSignal = it },
                label = "Side Input (Comparison / Subtrahend)",
                min = 0,
                max = 15,
                unit = "SS 0-15",
                quickPresets = listOf(0, 3, 6, 12, 15)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (isSubtractionMode) {
                    Text("FORMULA: max(0, Rear - Side)", style = MaterialTheme.typography.labelSmall, color = PurpleBright)
                    Text("max(0, $rearSignal - $sideSignal) = $outputSignal", style = MaterialTheme.typography.bodyMedium, color = OffWhite)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Used for analog pulse decay timers, signal clamps, and subtraction clocks.", style = MaterialTheme.typography.bodySmall, color = GrayText)
                } else {
                    Text("FORMULA: If Rear >= Side then Rear else 0", style = MaterialTheme.typography.labelSmall, color = PurpleBright)
                    Text(if (rearSignal >= sideSignal) "$rearSignal >= $sideSignal -> Passes $rearSignal through" else "$rearSignal < $sideSignal -> Blocked (Outputs 0)", style = MaterialTheme.typography.bodyMedium, color = OffWhite)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Used for threshold detectors and lock circuits.", style = MaterialTheme.typography.bodySmall, color = GrayText)
                }
            }
        }
    }
}

@Composable
private fun RequirementRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = GrayText)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = OffWhite)
    }
}
