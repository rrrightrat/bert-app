package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.NetflixBlack
import com.example.ui.theme.NetflixCardBorder
import com.example.ui.theme.NetflixDarkSurface
import com.example.ui.theme.NetflixDarkSurfaceVariant
import com.example.ui.theme.NetflixGray
import com.example.ui.theme.NetflixMuted
import com.example.ui.theme.NetflixOffWhite
import com.example.ui.theme.NetflixRed
import com.example.ui.theme.NetflixRedBright
import com.example.ui.theme.NetflixWhite

data class FeatureItem(
    val screen: AppScreen,
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val tag: String? = null
)

@Composable
fun HomeScreen(
    savedBlueprintsCount: Int,
    onSelectScreen: (AppScreen) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val allFeatures = remember {
        listOf(
            FeatureItem(
                screen = AppScreen.COMPARATOR,
                title = "Comparator & Container Analyzer",
                subtitle = "Exact container fullness math, target signal reverse solver, and mode simulator.",
                category = "CALCULATORS",
                icon = Icons.Default.Tune,
                tag = "CORE"
            ),
            FeatureItem(
                screen = AppScreen.TIMING,
                title = "Timings & Etho Clocks",
                subtitle = "Item-to-tick conversion, repeater delay chains, comparator pulse faders, burnout safety.",
                category = "CALCULATORS",
                icon = Icons.Default.Timer,
                tag = "MCBE ENGINE"
            ),
            FeatureItem(
                screen = AppScreen.SORTER,
                title = "Item Sorters & Storage",
                subtitle = "ImpulseSV 41+1+1+1+1 filter setup, overflow safety buffer, and hopper throughput.",
                category = "CALCULATORS",
                icon = Icons.Default.Inventory2,
                tag = "STORAGE"
            ),
            FeatureItem(
                screen = AppScreen.BALLISTICS,
                title = "Ballistics & TNT Accelerator",
                subtitle = "TNT cannon impulse vectors, payload fuse sync, trident killer DPS, boat launchers.",
                category = "CALCULATORS",
                icon = Icons.Default.RocketLaunch,
                tag = "PHYSICS"
            ),
            FeatureItem(
                screen = AppScreen.LOGIC,
                title = "8-Bit Bus & Logic Gates",
                subtitle = "Interactive binary switches, decimal/hex/analog conversion, Bedrock logic gate library.",
                category = "COMPUTATION",
                icon = Icons.Default.Memory,
                tag = "ALU"
            ),
            FeatureItem(
                screen = AppScreen.MECHANICS,
                title = "Bedrock Mechanics Encyclopedia",
                subtitle = "Deep reference on random update order, no quasi-connectivity, movable tile entities.",
                category = "KNOWLEDGE",
                icon = Icons.Default.MenuBook,
                tag = "GUIDE"
            ),
            FeatureItem(
                screen = AppScreen.BLUEPRINTS,
                title = "Saved Presets & Blueprints",
                subtitle = "Manage $savedBlueprintsCount saved contraptions, favorite setups, and custom build notes.",
                category = "KNOWLEDGE",
                icon = Icons.Default.Bookmark,
                tag = "$savedBlueprintsCount SAVED"
            ),
            FeatureItem(
                screen = AppScreen.ABOUT,
                title = "About BERT & Community",
                subtitle = "Technical background, community credits (RR Right Rat), and engineering specs.",
                category = "KNOWLEDGE",
                icon = Icons.Default.Info,
                tag = "v1.0"
            )
        )
    }

    val filteredFeatures = allFeatures.filter {
        searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.subtitle.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Netflix-style Hero Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NetflixDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NetflixCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    NetflixRed.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NetflixRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BEDROCK EDITION REDSTONE TOOLKIT",
                                style = MaterialTheme.typography.labelSmall,
                                color = NetflixRedBright,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "WELCOME TO BERT",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = NetflixWhite,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Select a feature below to begin precise tick calculations, container analysis, or contraption synchronization.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NetflixGray
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search redstone tools, comparators, clocks, or mechanics...",
                        style = MaterialTheme.typography.bodySmall,
                        color = NetflixMuted
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NetflixGray)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = NetflixGray)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NetflixRed,
                    unfocusedBorderColor = NetflixCardBorder,
                    focusedContainerColor = NetflixDarkSurface,
                    unfocusedContainerColor = NetflixDarkSurface,
                    cursorColor = NetflixRed,
                    focusedTextColor = NetflixWhite,
                    unfocusedTextColor = NetflixWhite
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_field")
            )
        }

        // Categorized Feature Sections
        val categories = listOf("CALCULATORS", "COMPUTATION", "KNOWLEDGE")
        categories.forEach { cat ->
            val itemsInCat = filteredFeatures.filter { it.category == cat }
            if (itemsInCat.isNotEmpty()) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                color = NetflixGray,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "${itemsInCat.size} TOOLS",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = NetflixMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                items(itemsInCat) { feature ->
                    FeatureCard(
                        feature = feature,
                        onClick = { onSelectScreen(feature.screen) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = NetflixDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NetflixCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("feature_card_${feature.screen.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(NetflixDarkSurfaceVariant)
                    .border(1.dp, NetflixCardBorder, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = NetflixRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NetflixWhite
                    )
                    if (feature.tag != null) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = NetflixDarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NetflixRed.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = feature.tag,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = NetflixRedBright,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = feature.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NetflixGray,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = NetflixMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
