package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White

@Composable
fun BallisticsScreen(
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
                modifier = Modifier.testTag("ballistics_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "TNT ACCELERATOR",
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
                            "TRIDENT KILLER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) PurpleBright else GrayText
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "BOAT LAUNCHER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 2) PurpleBright else GrayText
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
            0 -> TNTAcceleratorSection(onSaveBlueprint)
            1 -> TridentKillerSection(onSaveBlueprint)
            2 -> BoatLauncherSection(onSaveBlueprint)
        }
    }
}

@Composable
private fun TNTAcceleratorSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var tntCount by remember { mutableIntStateOf(16) }
    var entityType by remember { mutableStateOf("TNT") }

    val result = RedstoneMath.calculateTNTLaunch(tntCount, entityType)

    SectionCard(
        title = "TNT Ballistics & Cannon Physics",
        subtitle = "Propellant explosion vectors and payload synchronization (MCBE)",
        trailingTag = "${result.tntCount} TNT"
    ) {
        Text("LAUNCH PAYLOAD ENTITY", style = MaterialTheme.typography.labelSmall, color = GrayText)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("TNT", "Boat", "Player", "Trident").forEach { type ->
                val isSelected = entityType == type
                Surface(
                    onClick = { entityType = type },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PurplePrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) PurplePrimary else DarkCardBorder)
                ) {
                    Text(
                        text = type.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PurpleBright else OffWhite,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        NumericInputField(
            value = tntCount,
            onValueChange = { tntCount = it },
            label = "TNT Propellant Count",
            min = 1,
            max = 256,
            unit = "Charges",
            quickPresets = listOf(4, 8, 16, 32, 64, 128),
            helperText = "Type any TNT count. Discharging in water creates impulse without terrain destruction."
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BallisticRow("Estimated Horizontal Reach:", "~${result.estimatedDistanceBlocks.toInt()} blocks")
                BallisticRow("Horizontal Velocity Vector (Vx):", String.format("%.2f m/s", result.estimatedHorizontalVelocity))
                BallisticRow("Vertical Velocity Vector (Vy):", String.format("%.2f m/s", result.estimatedVerticalVelocity))
                BallisticRow("Recommended Payload Delay:", "${result.recommendedPayloadDelayTicks} RT (${result.recommendedPayloadDelayTicks * 2} GT)")
                BallisticRow("Total Propellant Explosion Fuse:", "80 Game Ticks (4.0s)")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        BedrockTipCard(
            title = "Bedrock Water Chamber Mechanics",
            description = "In MCBE, TNT exploding in flowing water, water sources, or waterlogged slabs deals zero block damage while maintaining 100% kinetic impulse to nearby primed entities!"
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = {
            val title = "$tntCount-TNT $entityType Cannon Array"
            val desc = "~${result.estimatedDistanceBlocks.toInt()} blocks range, payload delay ${result.recommendedPayloadDelayTicks} RT"
            val config = "Propellant: $tntCount TNT\nPayload: $entityType\nRange: ~${result.estimatedDistanceBlocks.toInt()} blocks\nPayload Delay: ${result.recommendedPayloadDelayTicks} RT"
            onSaveBlueprint(title, "BALLISTICS", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save Ballistics Setup to Blueprints", color = White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TridentKillerSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var pistonCount by remember { mutableIntStateOf(4) }
    var hasImpaling by remember { mutableStateOf(true) }
    var holdingLooting3 by remember { mutableStateOf(true) }

    val baseDamage = 9.0
    val impalingMultiplier = if (hasImpaling) 2.5 else 1.0
    val effectiveDamage = baseDamage * impalingMultiplier
    val hitsPerSec = when (pistonCount) {
        4 -> 2.0
        3 -> 1.5
        else -> 1.0
    }
    val dps = effectiveDamage * hitsPerSec

    SectionCard(
        title = "Bedrock Trident Killer Engineering",
        subtitle = "Automated mob farm kill chamber with Looting III transfer",
        trailingTag = "$pistonCount Pistons"
    ) {
        NumericInputField(
            value = pistonCount,
            onValueChange = { pistonCount = it },
            label = "Piston Loop Count",
            min = 1,
            max = 8,
            unit = "Pistons",
            quickPresets = listOf(1, 3, 4),
            helperText = "4 pistons in a loop gives the fastest consistent hit rate (~2.0 hits/s)."
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Impaling V (2.5x in water/rain)", style = MaterialTheme.typography.bodyMedium, color = OffWhite)
                Text("Applies to all mobs submerged in water", style = MaterialTheme.typography.labelSmall, color = GrayText)
            }
            Button(
                onClick = { hasImpaling = !hasImpaling },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasImpaling) PurplePrimary else DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (hasImpaling) PurpleBright else DarkCardBorder)
            ) {
                Text(if (hasImpaling) "ON" else "OFF", color = if (hasImpaling) White else OffWhite, style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Player Holding Looting III Sword", style = MaterialTheme.typography.bodyMedium, color = OffWhite)
                Text("Trident kills inherit equipped mainhand enchantments", style = MaterialTheme.typography.labelSmall, color = GrayText)
            }
            Button(
                onClick = { holdingLooting3 = !holdingLooting3 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (holdingLooting3) PurplePrimary else DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (holdingLooting3) PurpleBright else DarkCardBorder)
            ) {
                Text(if (holdingLooting3) "YES" else "NO", color = if (holdingLooting3) White else OffWhite, style = MaterialTheme.typography.labelMedium)
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
                BallisticRow("Damage per Hit:", "${String.format("%.1f", effectiveDamage)} HP")
                BallisticRow("Hit Frequency:", "$hitsPerSec hits/second")
                BallisticRow("Total Chamber DPS:", "${String.format("%.1f", dps)} DPS")
                BallisticRow("Looting III Drop Rate:", if (holdingLooting3) "+300% Mob Drops & Rare Loot" else "Vanilla Base Drops")
                BallisticRow("Durability Cost:", "0 (Trident never breaks in MCBE)")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        BedrockTipCard(
            title = "Crucial Bedrock Feature",
            description = "Tridents pushed by pistons never lose durability. Any mob killed by the trident attributes player-kill XP and drops to the player, inheriting Looting III from your currently equipped weapon!"
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = {
            val title = "$pistonCount-Piston Trident Killer (${String.format("%.1f", dps)} DPS)"
            val desc = "Setup with $pistonCount pistons, ${if (hasImpaling) "Impaling V" else "Standard"}, Looting III active"
            val config = "Pistons: $pistonCount\nDPS: ${String.format("%.1f", dps)}\nLooting III: $holdingLooting3\nImpaling: $hasImpaling"
            onSaveBlueprint(title, "MECHANICS", desc, config)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save Trident Killer Setup to Blueprints", color = White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BoatLauncherSection(
    onSaveBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var iceType by remember { mutableIntStateOf(2) } // 0 = Regular, 1 = Packed, 2 = Blue Ice
    var pistonBoosters by remember { mutableIntStateOf(4) }

    val (iceName, topSpeed) = when (iceType) {
        0 -> "Regular Ice" to "40 m/s (144 km/h)"
        1 -> "Packed Ice" to "40 m/s (144 km/h)"
        else -> "Blue Ice" to "72 m/s (260 km/h - Max)"
    }

    SectionCard(
        title = "Bedrock Boat Launcher & Ice Highway",
        subtitle = "Piston launch acceleration and high-speed nether transport",
        trailingTag = iceName
    ) {
        Text("ICE SURFACE TYPE", style = MaterialTheme.typography.labelSmall, color = GrayText)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                "BLUE ICE (72 m/s)" to 2,
                "PACKED ICE (40 m/s)" to 1,
                "REGULAR ICE" to 0
            ).forEach { (label, type) ->
                val isSelected = iceType == type
                Surface(
                    onClick = { iceType = type },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PurplePrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) PurplePrimary else DarkCardBorder)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PurpleBright else OffWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        NumericInputField(
            value = pistonBoosters,
            onValueChange = { pistonBoosters = it },
            label = "Piston Booster Sequence",
            min = 1,
            max = 16,
            unit = "Pistons",
            quickPresets = listOf(2, 4, 6, 8),
            helperText = "Type number of sequential launch pistons."
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BallisticRow("Maximum Cruising Speed:", topSpeed)
                BallisticRow("Piston Timing Interval:", "2 Redstone Ticks per piston")
                BallisticRow("Launch Acceleration:", "Instant full-speed glide")
                BallisticRow("Nether Equivalent Overworld Speed:", if (iceType == 2) "576 m/s (2,073 km/h)" else "320 m/s")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        BedrockTipCard(
            title = "Piston Timing Caution",
            description = "To launch boats reliably without jamming, space booster pistons 2 blocks apart and delay each consecutive piston by 2 redstone ticks (4 game ticks)."
        )
    }
}

@Composable
private fun BallisticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = GrayText)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OffWhite)
    }
}
