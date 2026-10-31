package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
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
import com.example.ui.theme.PurpleDark
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White
import kotlinx.coroutines.launch

data class FeatureItem(
    val screen: AppScreen,
    val title: String,
    val subtitle: String,
    val category: String,
    val categoryPill: String,
    val icon: ImageVector,
    val tag: String? = null,
    val tabGroup: String = "Popular", // Popular, Essential, Advanced
    val specs: String = "Bedrock 1.21"
)

data class ShowcaseHero(
    val screen: AppScreen,
    val tag: String,
    val title: String,
    val subtitle: String,
    val gradientColors: List<Color>,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    savedBlueprintsCount: Int,
    onSelectScreen: (AppScreen) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTab by remember { mutableStateOf("Popular") }
    var activeHeroPage by remember { mutableIntStateOf(0) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Filter sheet states
    var filterSignalStrength by remember { mutableFloatStateOf(15f) }
    var selectedComplexity by remember { mutableStateOf("All Levels") }

    val allFeatures = remember(savedBlueprintsCount) {
        listOf(
            FeatureItem(
                screen = AppScreen.COMPONENTS,
                title = "Component Database",
                subtitle = "Complete encyclopedia of every single MCBE component: properties, delays & Java differences.",
                category = "KNOWLEDGE",
                categoryPill = "Database",
                icon = Icons.Default.Category,
                tag = "EVERY COMPONENT",
                tabGroup = "Popular",
                specs = "25+ Components"
            ),
            FeatureItem(
                screen = AppScreen.COMPARATOR,
                title = "Comparator & Signals",
                subtitle = "Exact container fullness math, target signal solver, and live subtract simulator.",
                category = "CALCULATORS",
                categoryPill = "Comparator",
                icon = Icons.Default.Tune,
                tag = "CORE",
                tabGroup = "Popular",
                specs = "1-15 Signals"
            ),
            FeatureItem(
                screen = AppScreen.TIMING,
                title = "Timings & Etho Clocks",
                subtitle = "Hopper item transfer timing, repeater delay chain optimizer, comparator pulse faders.",
                category = "CALCULATORS",
                categoryPill = "Clocks",
                icon = Icons.Default.Timer,
                tag = "0.05s TICK",
                tabGroup = "Popular",
                specs = "0.4s / Item"
            ),
            FeatureItem(
                screen = AppScreen.SORTER,
                title = "Item Sorters & Storage",
                subtitle = "ImpulseSV 41+1+1+1+1 filter calculations, safe crosstalk buffer, and hopper throughput.",
                category = "CALCULATORS",
                categoryPill = "Sorters",
                icon = Icons.Default.Inventory2,
                tag = "STORAGE",
                tabGroup = "Essential",
                specs = "41 Item Safe"
            ),
            FeatureItem(
                screen = AppScreen.BALLISTICS,
                title = "Ballistics & TNT Cannons",
                subtitle = "TNT cannon momentum vectors, payload fuse sync, trident killer DPS, boat launchers.",
                category = "CALCULATORS",
                categoryPill = "Cannons",
                icon = Icons.Default.RocketLaunch,
                tag = "PHYSICS",
                tabGroup = "Advanced",
                specs = "Vector Math"
            ),
            FeatureItem(
                screen = AppScreen.LOGIC,
                title = "8-Bit Bus & Logic Gates",
                subtitle = "Decimal/Hex/Binary/Analog conversion with live bit switches, Bedrock logic gate library.",
                category = "COMPUTATION",
                categoryPill = "Logic ALU",
                icon = Icons.Default.Memory,
                tag = "ALU",
                tabGroup = "Advanced",
                specs = "8-Bit Bus"
            ),
            FeatureItem(
                screen = AppScreen.MECHANICS,
                title = "Bedrock Quirks & Rules",
                subtitle = "Searchable guide covering random update order, no quasi-connectivity, movable tile entities.",
                category = "KNOWLEDGE",
                categoryPill = "Quirks",
                icon = Icons.Default.MenuBook,
                tag = "GUIDE",
                tabGroup = "Essential",
                specs = "MCBE Rules"
            ),
            FeatureItem(
                screen = AppScreen.BLUEPRINTS,
                title = "Saved Presets & Notes",
                subtitle = "Manage $savedBlueprintsCount saved setups, favorite circuits, and custom contraption notes.",
                category = "KNOWLEDGE",
                categoryPill = "Presets",
                icon = Icons.Default.Bookmark,
                tag = "$savedBlueprintsCount SAVED",
                tabGroup = "Popular",
                specs = "$savedBlueprintsCount Blueprints"
            ),
            FeatureItem(
                screen = AppScreen.ABOUT,
                title = "About BERT & Community",
                subtitle = "Technical research, community attribution (RR Right Rat), and MCBE redstone manual.",
                category = "KNOWLEDGE",
                categoryPill = "About",
                icon = Icons.Default.Info,
                tag = "v1.0",
                tabGroup = "Essential",
                specs = "Attribution"
            )
        )
    }

    val heroes = remember {
        listOf(
            ShowcaseHero(
                screen = AppScreen.COMPARATOR,
                tag = "FEATURED LAB",
                title = "Comparator & Signal Math",
                subtitle = "Bedrock container fullness, subtraction solver, and live pulse simulation.",
                gradientColors = listOf(EmeraldAccent.copy(alpha = 0.28f), EmeraldContainer.copy(alpha = 0.85f), DarkSurface),
                accentColor = EmeraldAccent
            ),
            ShowcaseHero(
                screen = AppScreen.TIMING,
                tag = "TICK PRECISION",
                title = "Etho Hopper Clock Solver",
                subtitle = "Exact 0.4s hopper transfers and repeater delay chains tailored for Bedrock.",
                gradientColors = listOf(PurplePrimary.copy(alpha = 0.35f), PurpleContainer.copy(alpha = 0.9f), DarkSurface),
                accentColor = PurpleBright
            ),
            ShowcaseHero(
                screen = AppScreen.SORTER,
                tag = "STORAGE SAFE",
                title = "ImpulseSV Item Filters",
                subtitle = "Prevent adjacent slice overflow with exact 41+1+1+1+1 item calculations.",
                gradientColors = listOf(FilterOrange.copy(alpha = 0.32f), PurpleContainer.copy(alpha = 0.8f), DarkSurface),
                accentColor = FilterOrange
            )
        )
    }

    val categories = remember {
        listOf("All", "Database", "Comparator", "Clocks", "Sorters", "Cannons", "Logic ALU", "Quirks", "Presets")
    }

    val tabs = remember {
        listOf("Popular", "Essential", "Advanced", "All")
    }

    val filteredFeatures = allFeatures.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.subtitle.contains(searchQuery, ignoreCase = true) ||
                item.specs.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == "All" || item.categoryPill.equals(selectedCategory, ignoreCase = true)

        val matchesTab = selectedTab == "All" || item.tabGroup.equals(selectedTab, ignoreCase = true)

        matchesSearch && matchesCategory && matchesTab
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Space under top bar
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Bold Headline (Inspired by "Let's find / Your Gadget!" in mockup)
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FilterOrange)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BEDROCK REDSTONE WORKSHOP",
                        style = MaterialTheme.typography.labelSmall,
                        color = FilterOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp
                    )
                }

                Text(
                    text = "Let's build\nYour Contraption!",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, lineHeight = 38.sp),
                    fontWeight = FontWeight.Black,
                    color = White
                )
            }
        }

        // Search Bar with Trailing Orange/Amber Filter Pill (Exact match to mockup)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search circuits, timings, math...",
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
                        .weight(1f)
                        .height(54.dp)
                        .testTag("home_search_field")
                )

                // Trailing Filter Button (Vibrant Orange rounded square pill from mockup)
                Surface(
                    onClick = { showFilterSheet = true },
                    shape = RoundedCornerShape(14.dp),
                    color = FilterOrange,
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("btn_open_filters")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Hero Showcase Carousel Card (Inspired by the green headphone banner in mockup)
        item {
            val currentHero = heroes[activeHeroPage % heroes.size]

            Surface(
                onClick = { onSelectScreen(currentHero.screen) },
                shape = RoundedCornerShape(24.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = currentHero.gradientColors
                            )
                        )
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1.3f)
                        ) {
                            // Tag Chip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkBackground.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, currentHero.accentColor.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = currentHero.tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = currentHero.accentColor,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentHero.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                fontWeight = FontWeight.Bold,
                                color = White
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = currentHero.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayText,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // "Let's Go" action pill
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = currentHero.accentColor,
                                modifier = Modifier.clickable { onSelectScreen(currentHero.screen) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Open Lab",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Visual Hero Thumbnail on the right
                        Box(
                            modifier = Modifier
                                .weight(0.7f)
                                .height(120.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(DarkBackground.copy(alpha = 0.45f))
                                .border(1.dp, currentHero.accentColor.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner_redstone_lab_1793446209557),
                                contentDescription = "Redstone Workshop",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Pagination Dots below Hero Card (• ◦ ◦ from mockup)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                heroes.indices.forEach { index ->
                    val isSelected = index == (activeHeroPage % heroes.size)
                    val color by animateColorAsState(
                        targetValue = if (isSelected) EmeraldAccent else DarkCardBorder,
                        label = "dotColor"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 20.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { activeHeroPage = index }
                    )
                }
            }
        }

        // Categories Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelMedium,
                    color = PurpleBright,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        selectedCategory = "All"
                        selectedTab = "All"
                        searchQuery = ""
                    }
                )
            }
        }

        // Horizontal Category Pills (Rounded Rectangular Pills with Icon + Label from mockup)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { categoryName ->
                    val isSelected = selectedCategory.equals(categoryName, ignoreCase = true)
                    val icon = when (categoryName) {
                        "Database" -> Icons.Default.Category
                        "Comparator" -> Icons.Default.Tune
                        "Clocks" -> Icons.Default.Timer
                        "Sorters" -> Icons.Default.Inventory2
                        "Cannons" -> Icons.Default.RocketLaunch
                        "Logic ALU" -> Icons.Default.Memory
                        "Quirks" -> Icons.Default.MenuBook
                        "Presets" -> Icons.Default.Bookmark
                        else -> Icons.Default.FilterList
                    }

                    Surface(
                        onClick = { selectedCategory = categoryName },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PurplePrimary else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PurpleBright else DarkCardBorder
                        ),
                        modifier = Modifier.testTag("cat_pill_${categoryName.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.Black else GrayText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else White
                            )
                        }
                    }
                }
            }
        }

        // Sub-filter Tabs ("Popular", "Essential", "Advanced", "All" with dot indicator from mockup)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tabName ->
                    val isSelected = selectedTab.equals(tabName, ignoreCase = true)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedTab = tabName }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = tabName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) White else MutedText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) FilterOrange else Color.Transparent)
                        )
                    }
                }
            }
        }

        // 2-Column Gadget / Tool Cards Grid (Inspired by the 2-column gadget cards in mockup)
        if (filteredFeatures.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No contraptions matched your filter",
                            style = MaterialTheme.typography.titleSmall,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try clearing search or switching category tabs.",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "All"
                                selectedTab = "All"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Text("Reset Filters", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Pair items to create a clean, responsive 2-column shelf
            val chunkedFeatures = filteredFeatures.chunked(2)
            items(chunkedFeatures) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val item1 = pair[0]
                    Box(modifier = Modifier.weight(1f)) {
                        GadgetCard(
                            feature = item1,
                            onClick = { onSelectScreen(item1.screen) }
                        )
                    }

                    if (pair.size > 1) {
                        val item2 = pair[1]
                        Box(modifier = Modifier.weight(1f)) {
                            GadgetCard(
                                feature = item2,
                                onClick = { onSelectScreen(item2.screen) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Filter Bottom Sheet (Exact match to bottom-right mockup)
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.65f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(40.dp, 4.dp)
                        .clip(CircleShape)
                        .background(DarkCardBorder)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Sheet Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    TextButton(
                        onClick = {
                            selectedCategory = "All"
                            selectedTab = "All"
                            filterSignalStrength = 15f
                            selectedComplexity = "All Levels"
                            searchQuery = ""
                        }
                    ) {
                        Text("Reset", color = FilterOrange, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Categories Row
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleSmall,
                    color = GrayText,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSel = selectedCategory.equals(cat, ignoreCase = true)
                        Surface(
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) PurplePrimary else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) PurpleBright else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSel) Color.Black else White,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Complexity / Target Signal
                Text(
                    text = "Signal Strength Requirement (0 - 15)",
                    style = MaterialTheme.typography.titleSmall,
                    color = GrayText,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Signal: ${filterSignalStrength.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = CozyLanternAmber
                    )
                    Text(
                        text = if (filterSignalStrength.toInt() == 15) "Full Range" else "Max Filtered",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                }

                Slider(
                    value = filterSignalStrength,
                    onValueChange = { filterSignalStrength = it },
                    valueRange = 0f..15f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = FilterOrange,
                        activeTrackColor = FilterOrange,
                        inactiveTrackColor = DarkCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bedrock Component Tags
                Text(
                    text = "Bedrock Component Tags",
                    style = MaterialTheme.typography.titleSmall,
                    color = GrayText,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                val tagsList = listOf("All Levels", "ImpulseSV Safe", "4-Tick Clock", "0.05s Engine", "No Quasi-QC")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tagsList) { tag ->
                        val isSel = selectedComplexity == tag
                        Surface(
                            onClick = { selectedComplexity = tag },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) FilterOrange else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) FilterOrange else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSel) Color.Black else White,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Apply Filters Button
                Button(
                    onClick = { showFilterSheet = false },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_apply_filters")
                ) {
                    Text(
                        text = "Apply Filters",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

// 2-Column Gadget Card (Inspired by the gadget product cards in the inspiration image)
@Composable
private fun GadgetCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gadget_card_${feature.screen.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Preview container with hero icon and subtle glowing backdrop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PurpleContainer.copy(alpha = 0.5f),
                                DarkSurfaceVariant
                            )
                        )
                    )
                    .border(1.dp, PurplePrimary.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = PurpleBright,
                    modifier = Modifier.size(36.dp)
                )

                // Top right tag badge
                if (feature.tag != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkBackground.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = feature.tag,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category tag
            Text(
                text = feature.categoryPill.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = FilterOrange,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Specs / Subtitle
            Text(
                text = feature.specs,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = GrayText,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Launch",
                    style = MaterialTheme.typography.labelSmall,
                    color = PurpleBright,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PurpleContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = PurpleBright,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
