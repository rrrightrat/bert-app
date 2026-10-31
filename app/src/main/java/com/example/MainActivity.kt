package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.RedstoneViewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.BallisticsScreen
import com.example.ui.screens.BlueprintsScreen
import com.example.ui.screens.ComparatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ItemSorterScreen
import com.example.ui.screens.LogicScreen
import com.example.ui.screens.MechanicsScreen
import com.example.ui.screens.TimingScreen
import com.example.ui.theme.BERTTheme
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BERTTheme(darkTheme = true) {
                BERTApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BERTApp(viewModel: RedstoneViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val blueprints by viewModel.allBlueprints.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Hardware back returns to HOME screen if on a sub-tool
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NetflixBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (currentScreen != AppScreen.HOME) {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.HOME) },
                            modifier = Modifier.testTag("nav_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Home",
                                tint = NetflixWhite
                            )
                        }
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(NetflixRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "B",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentScreen == AppScreen.HOME) "BERT" else currentScreen.title.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NetflixWhite,
                            letterSpacing = 1.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.ABOUT) },
                        modifier = Modifier.testTag("action_about")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About BERT",
                            tint = if (currentScreen == AppScreen.ABOUT) NetflixRedBright else NetflixGray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NetflixBlack
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NetflixDarkSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(
                        androidx.compose.foundation.BorderStroke(1.dp, NetflixCardBorder),
                        RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    )
                    .testTag("main_navigation_bar")
            ) {
                val navItems = listOf(
                    NavItem(AppScreen.HOME, "Home", Icons.Default.Home),
                    NavItem(AppScreen.COMPARATOR, "Compare", Icons.Default.Tune),
                    NavItem(AppScreen.TIMING, "Timings", Icons.Default.Timer),
                    NavItem(AppScreen.SORTER, "Sorters", Icons.Default.Inventory2),
                    NavItem(AppScreen.BALLISTICS, "Cannons", Icons.Default.RocketLaunch),
                    NavItem(AppScreen.MECHANICS, "Quirks", Icons.Default.MenuBook),
                    NavItem(AppScreen.BLUEPRINTS, "Presets", Icons.Default.Bookmark, badgeCount = blueprints.size)
                )

                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = {
                            if (item.badgeCount != null && item.badgeCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = NetflixRed) {
                                        Text("${item.badgeCount}", color = Color.White)
                                    }
                                }) {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            } else {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        },
                        label = {
                            Text(
                                text = item.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NetflixRedBright,
                            selectedTextColor = NetflixWhite,
                            indicatorColor = NetflixDarkSurfaceVariant,
                            unselectedIconColor = NetflixMuted,
                            unselectedTextColor = NetflixMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${item.screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NetflixBlack)
        ) {
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            savedBlueprintsCount = blueprints.size,
                            onSelectScreen = { dest -> viewModel.navigateTo(dest) }
                        )
                    }
                    AppScreen.COMPARATOR -> {
                        ComparatorScreen(
                            onSaveBlueprint = { title, cat, desc, config ->
                                viewModel.saveBlueprint(title, cat, desc, config)
                            }
                        )
                    }
                    AppScreen.TIMING -> {
                        TimingScreen(
                            onSaveBlueprint = { title, cat, desc, config ->
                                viewModel.saveBlueprint(title, cat, desc, config)
                            }
                        )
                    }
                    AppScreen.SORTER -> {
                        ItemSorterScreen(
                            onSaveBlueprint = { title, cat, desc, config ->
                                viewModel.saveBlueprint(title, cat, desc, config)
                            }
                        )
                    }
                    AppScreen.BALLISTICS -> {
                        BallisticsScreen(
                            onSaveBlueprint = { title, cat, desc, config ->
                                viewModel.saveBlueprint(title, cat, desc, config)
                            }
                        )
                    }
                    AppScreen.LOGIC -> {
                        LogicScreen()
                    }
                    AppScreen.MECHANICS -> {
                        MechanicsScreen()
                    }
                    AppScreen.BLUEPRINTS -> {
                        BlueprintsScreen(
                            blueprints = blueprints,
                            onToggleFavorite = { id, current -> viewModel.toggleFavorite(id, current) },
                            onDeleteBlueprint = { id -> viewModel.deleteBlueprint(id) },
                            onAddBlueprint = { title, cat, desc, config ->
                                viewModel.saveBlueprint(title, cat, desc, config)
                            }
                        )
                    }
                    AppScreen.ABOUT -> {
                        AboutScreen()
                    }
                }
            }
        }
    }
}

private data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int? = null
)
