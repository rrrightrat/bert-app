package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComponentCategory
import com.example.data.model.RedstoneComponent
import com.example.data.model.RedstoneComponentDatabase
import com.example.ui.theme.CozyLanternAmber
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.FilterOrange
import com.example.ui.theme.GrayText
import com.example.ui.theme.MutedText
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurpleContainer
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White

@Composable
fun ComponentDatabaseScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ComponentCategory?>(null) }
    var filterMovableOnly by remember { mutableStateOf(false) }
    var filterAnalogOnly by remember { mutableStateOf(false) }

    val allComponents = remember { RedstoneComponentDatabase.ALL_COMPONENTS }

    val filteredList = allComponents.filter { comp ->
        val matchesSearch = searchQuery.isBlank() ||
                comp.name.contains(searchQuery, ignoreCase = true) ||
                comp.description.contains(searchQuery, ignoreCase = true) ||
                comp.bedrockQuirks.contains(searchQuery, ignoreCase = true) ||
                comp.javaDifferences.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == null || comp.category == selectedCategory
        val matchesMovable = !filterMovableOnly || comp.movableByPiston
        val matchesAnalog = !filterAnalogOnly || comp.signalOutput.contains("Analog", ignoreCase = true)

        matchesSearch && matchesCategory && matchesMovable && matchesAnalog
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Header Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    PurpleContainer.copy(alpha = 0.6f),
                                    DarkSurface
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
                                    .background(FilterOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MCBE 1.21 COMPONENT ENCYCLOPEDIA",
                                style = MaterialTheme.typography.labelSmall,
                                color = FilterOrange,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Redstone Component Database",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.Bold,
                            color = White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Detailed mechanical breakdown of every redstone component in Minecraft Bedrock Edition. Includes exact tick delays, signal equations, movable tile entity properties, and Java differences.",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ComponentStatBadge(label = "COMPONENTS", value = "${allComponents.size}")
                            ComponentStatBadge(label = "MOVABLE BY PISTON", value = "${allComponents.count { it.movableByPiston }}")
                            ComponentStatBadge(label = "BEDROCK ENGINE", value = "1.21")
                        }
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
                        "Search components (e.g. Piston, Crafter, Copper Bulb)...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PurpleBright,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GrayText)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    cursorColor = PurplePrimary,
                    focusedTextColor = White,
                    unfocusedTextColor = White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("component_search_field")
            )
        }

        // Category Filter Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isAll = selectedCategory == null
                    Surface(
                        onClick = { selectedCategory = null },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAll) PurplePrimary else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAll) PurpleBright else DarkCardBorder
                        ),
                        modifier = Modifier.testTag("cat_filter_all")
                    ) {
                        Text(
                            text = "All (${allComponents.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAll) Color.Black else White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                items(ComponentCategory.values().filter { it != ComponentCategory.ALL }) { cat ->
                    val isSelected = selectedCategory == cat
                    val count = allComponents.count { it.category == cat }
                    Surface(
                        onClick = { selectedCategory = if (isSelected) null else cat },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PurplePrimary else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PurpleBright else DarkCardBorder
                        )
                    ) {
                        Text(
                            text = "${cat.displayName} ($count)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Quick Quirk Toggles (Movable by Piston / Analog)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { filterMovableOnly = !filterMovableOnly },
                    shape = RoundedCornerShape(10.dp),
                    color = if (filterMovableOnly) EmeraldContainer else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (filterMovableOnly) EmeraldAccent else DarkCardBorder
                    ),
                    modifier = Modifier.testTag("filter_movable")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (filterMovableOnly) EmeraldAccent else GrayText)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Movable by Piston",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (filterMovableOnly) EmeraldAccent else GrayText
                        )
                    }
                }

                Surface(
                    onClick = { filterAnalogOnly = !filterAnalogOnly },
                    shape = RoundedCornerShape(10.dp),
                    color = if (filterAnalogOnly) PurpleContainer else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (filterAnalogOnly) PurpleBright else DarkCardBorder
                    ),
                    modifier = Modifier.testTag("filter_analog")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (filterAnalogOnly) PurpleBright else GrayText)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Analog Output (0-15)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (filterAnalogOnly) PurpleBright else GrayText
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${filteredList.size} Results",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
            }
        }

        // Component List
        items(filteredList, key = { it.id }) { component ->
            ComponentDetailCard(component = component)
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun ComponentDetailCard(component: RedstoneComponent) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (expanded) PurplePrimary.copy(alpha = 0.5f) else DarkCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("comp_card_${component.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Name, Category Chip, Expand Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = component.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = component.category.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = FilterOrange,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (component.movableByPiston) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "PISTON MOVABLE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = EmeraldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = PurpleBright
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Short Description
            Text(
                text = component.description,
                style = MaterialTheme.typography.bodySmall,
                color = GrayText,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Specs Pill Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickSpecPill(label = "Signal", value = component.signalOutput.take(16))
                QuickSpecPill(label = "Delay", value = component.delay.take(16))
            }

            // Expanded In-Depth Mechanical Details
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HorizontalDivider(color = DarkCardBorder)

                    // Bedrock Specific Quirks Block (Crucial MCBE Rules)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FilterOrange.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FilterOrange.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = FilterOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BEDROCK EDITION SPECIFICS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FilterOrange,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = component.bedrockQuirks,
                                style = MaterialTheme.typography.bodySmall,
                                color = White,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Java vs Bedrock Differences
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PurpleContainer.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = PurpleBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "JAVA VS BEDROCK DIFFERENCES",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleBright,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = component.javaDifferences,
                                style = MaterialTheme.typography.bodySmall,
                                color = White,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Pro Tips
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldContainer.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = EmeraldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ENGINEERING PRO-TIP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldAccent,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = component.proTips,
                                style = MaterialTheme.typography.bodySmall,
                                color = White,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Properties Key-Value Table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TECHNICAL ATTRIBUTES",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayText,
                            fontWeight = FontWeight.Bold
                        )

                        component.propertiesList.forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayText
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickSpecPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MutedText
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ComponentStatBadge(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PurpleBright
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = GrayText
            )
        }
    }
}
