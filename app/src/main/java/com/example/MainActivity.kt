package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import com.example.ui.AppScreen
import com.example.ui.CommunityViewModel
import com.example.ui.RedstoneViewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BallisticsScreen
import com.example.ui.screens.BlueprintsScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.ComparatorScreen
import com.example.ui.screens.ComponentDatabaseScreen
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ItemSorterScreen
import com.example.ui.screens.LogicScreen
import com.example.ui.screens.MechanicsScreen
import com.example.ui.screens.TimingScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.BERTTheme
import com.example.ui.theme.CozyLanternAmber
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FilterOrange
import com.example.ui.theme.GrayText
import com.example.ui.theme.MutedText
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White
import kotlinx.coroutines.launch

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

// 4-Dot Grid Menu Icon (matching top-left icon in mockup)
@Composable
fun GridDotsMenuIcon(tint: Color = White, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(tint))
                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(tint))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(tint))
                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(tint))
            }
        }
    }
}

@Composable
fun StartSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("app_start_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bert_logo),
                contentDescription = "BERT Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF222222), RoundedCornerShape(24.dp))
                    .testTag("splash_logo")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "BERT",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = White,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bedrock Edition Redstone Toolkit",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = GrayText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BERTApp(
    viewModel: RedstoneViewModel = viewModel(),
    communityViewModel: CommunityViewModel = viewModel()
) {
    var isSplashScreenVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(800L)
        isSplashScreenVisible = false
    }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val blueprints by viewModel.allBlueprints.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showToolsSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            viewModel.snackbarMessage.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
        coroutineScope.launch {
            communityViewModel.message.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Crossfade(targetState = isSplashScreenVisible, label = "splashTransition") { showSplash ->
        if (showSplash) {
            StartSplashScreen()
        } else {
            // Hardware back button smoothly returns to previous screen or closes drawer
            BackHandler(enabled = drawerState.isOpen || currentScreen != AppScreen.HOME) {
                if (drawerState.isOpen) {
                    coroutineScope.launch { drawerState.close() }
                } else {
                    when (currentScreen) {
                        AppScreen.CREATE_POST -> viewModel.navigateTo(AppScreen.COMMUNITY)
                        AppScreen.USER_PROFILE -> viewModel.navigateTo(AppScreen.COMMUNITY)
                        AppScreen.ADMIN -> viewModel.navigateTo(AppScreen.USER_PROFILE)
                        else -> viewModel.navigateTo(AppScreen.HOME)
                    }
                }
            }

            // Modal Navigation Drawer
            ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkSurface,
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .border(androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // User / Engineer Avatar & Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PurplePrimary, FilterOrange)
                                    )
                                )
                                .border(2.dp, CozyLanternAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Hey, Engineer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Bedrock Workshop 1.21",
                                style = MaterialTheme.typography.bodySmall,
                                color = FilterOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = DarkCardBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "WORKSHOP SECTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    val drawerItems = listOf(
                        DrawerItem(AppScreen.HOME, "Home Dashboard", Icons.Default.Home),
                        DrawerItem(AppScreen.COMMUNITY, "Redstone Community", Icons.Default.Forum, badgeText = "Cloud"),
                        DrawerItem(AppScreen.USER_PROFILE, "My Profile & Posts", Icons.Default.Person),
                        DrawerItem(AppScreen.ADMIN, "Admin Control Center", Icons.Default.Security, badgeText = "SECURE"),
                        DrawerItem(AppScreen.COMPONENTS, "Component Database", Icons.Default.Category, badgeText = "25+"),
                        DrawerItem(AppScreen.COMPARATOR, "Comparator & Signals", Icons.Default.Tune),
                        DrawerItem(AppScreen.TIMING, "Timings & Etho Clocks", Icons.Default.Timer),
                        DrawerItem(AppScreen.SORTER, "Item Sorters & Storage", Icons.Default.Inventory2),
                        DrawerItem(AppScreen.BALLISTICS, "Ballistics & TNT Cannons", Icons.Default.RocketLaunch),
                        DrawerItem(AppScreen.LOGIC, "8-Bit Bus & Logic ALU", Icons.Default.Memory),
                        DrawerItem(AppScreen.MECHANICS, "Bedrock Quirks & Rules", Icons.Default.MenuBook),
                        DrawerItem(AppScreen.BLUEPRINTS, "Saved Presets", Icons.Default.Bookmark, badge = blueprints.size),
                        DrawerItem(AppScreen.ABOUT, "About BERT & Community", Icons.Default.Info)
                    )

                    drawerItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) PurpleBright else GrayText
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) White else GrayText
                                )
                            },
                            badge = {
                                if (item.badgeText != null) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = FilterOrange
                                    ) {
                                        Text(
                                            text = item.badgeText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                } else if (item.badge != null && item.badge > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PurplePrimary
                                    ) {
                                        Text(
                                            text = "${item.badge}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            },
                            selected = isSelected,
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.navigateTo(item.screen)
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = PurpleContainer,
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "BERT • BEDROCK TOOLKIT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = FilterOrange
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Precision Bedrock Edition Redstone Engine with full local storage.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = GrayText
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        if (currentScreen == AppScreen.HOME) {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .testTag("nav_grid_menu_button")
                            ) {
                                GridDotsMenuIcon(tint = White)
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    if (currentScreen == AppScreen.CREATE_POST || currentScreen == AppScreen.USER_PROFILE) {
                                        viewModel.navigateTo(AppScreen.COMMUNITY)
                                    } else if (currentScreen == AppScreen.ADMIN) {
                                        viewModel.navigateTo(AppScreen.USER_PROFILE)
                                    } else {
                                        viewModel.navigateTo(AppScreen.HOME)
                                    }
                                },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .testTag("nav_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = White
                                )
                            }
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .border(1.dp, Color(0xFF444444), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "B",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (currentScreen == AppScreen.HOME) "BERT" else currentScreen.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.COMMUNITY) },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("action_community")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = "Community Creations",
                                tint = if (currentScreen == AppScreen.COMMUNITY || currentScreen == AppScreen.CREATE_POST || currentScreen == AppScreen.USER_PROFILE) PurpleBright else GrayText
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.BLUEPRINTS) },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("action_presets")
                        ) {
                            if (blueprints.isNotEmpty()) {
                                BadgedBox(badge = {
                                    Badge(
                                        containerColor = FilterOrange,
                                        contentColor = Color.Black
                                    ) {
                                        Text("${blueprints.size}", fontWeight = FontWeight.Bold)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Saved Presets",
                                        tint = if (currentScreen == AppScreen.BLUEPRINTS) PurpleBright else GrayText
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Saved Presets",
                                    tint = if (currentScreen == AppScreen.BLUEPRINTS) PurpleBright else GrayText
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = DarkBackground
                    )
                )
            },
            // Bottom bar removed as requested, replaced with circular Tools button at bottom right corner (hidden on Community & related screens)
            floatingActionButton = {
                if (currentScreen != AppScreen.COMMUNITY &&
                    currentScreen != AppScreen.CREATE_POST &&
                    currentScreen != AppScreen.USER_PROFILE &&
                    currentScreen != AppScreen.ADMIN
                ) {
                    Surface(
                        onClick = { showToolsSheet = true },
                        shape = CircleShape,
                        color = PurplePrimary,
                        shadowElevation = 10.dp,
                        border = androidx.compose.foundation.BorderStroke(2.dp, PurpleBright),
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(bottom = 8.dp, end = 4.dp)
                            .size(68.dp)
                            .testTag("btn_bottom_tools_circle")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(PurpleBright, PurplePrimary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "Tools",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tools",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DarkBackground)
            ) {
                Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                    when (screen) {
                        AppScreen.HOME -> {
                            HomeScreen(
                                savedBlueprintsCount = blueprints.size,
                                onSelectScreen = { dest -> viewModel.navigateTo(dest) },
                                onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                            )
                        }
                        AppScreen.COMPONENTS -> {
                            ComponentDatabaseScreen()
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
                        AppScreen.COMMUNITY -> {
                            CommunityScreen(
                                viewModel = communityViewModel,
                                onNavigateToCreatePost = {
                                    communityViewModel.setPostToEdit(null)
                                    viewModel.navigateTo(AppScreen.CREATE_POST)
                                },
                                onNavigateToUserProfile = {
                                    viewModel.navigateTo(AppScreen.USER_PROFILE)
                                }
                            )
                        }
                        AppScreen.CREATE_POST -> {
                            CreatePostScreen(
                                viewModel = communityViewModel,
                                existingPost = communityViewModel.postToEdit.collectAsStateWithLifecycle().value,
                                onNavigateBack = { viewModel.navigateTo(AppScreen.COMMUNITY) }
                            )
                        }
                        AppScreen.USER_PROFILE -> {
                            UserProfileScreen(
                                viewModel = communityViewModel,
                                onNavigateBack = { viewModel.navigateTo(AppScreen.COMMUNITY) },
                                onNavigateToCreatePost = {
                                    communityViewModel.setPostToEdit(null)
                                    viewModel.navigateTo(AppScreen.CREATE_POST)
                                },
                                onEditPost = { post ->
                                    communityViewModel.setPostToEdit(post)
                                    viewModel.navigateTo(AppScreen.CREATE_POST)
                                },
                                onNavigateToAdmin = {
                                    viewModel.navigateTo(AppScreen.ADMIN)
                                }
                            )
                        }
                        AppScreen.ADMIN -> {
                            AdminScreen(
                                viewModel = communityViewModel,
                                onNavigateBack = { viewModel.navigateTo(AppScreen.USER_PROFILE) }
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

    // Tools Palette Modal Bottom Sheet (Triggered by the bottom right Tools circle button)
    if (showToolsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showToolsSheet = false },
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
                    .padding(horizontal = 24.dp, vertical = 14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Redstone Tools & Modules",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = "Select a laboratory module or component database",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = PurpleBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                val toolsList = listOf(
                    ToolOption(
                        screen = AppScreen.COMMUNITY,
                        title = "Community Creations",
                        subtitle = "Browse, post, like & comment on community Bedrock redstone contraptions",
                        icon = Icons.Default.Forum,
                        badge = "COMMUNITY"
                    ),
                    ToolOption(
                        screen = AppScreen.USER_PROFILE,
                        title = "Creator Profile & My Posts",
                        subtitle = "Manage your published contraptions, stats and account",
                        icon = Icons.Default.Person
                    ),
                    ToolOption(
                        screen = AppScreen.ADMIN,
                        title = "Admin Control Center",
                        subtitle = "Manage, ban, delete accounts & community creations",
                        icon = Icons.Default.Security,
                        badge = "ADMIN"
                    ),
                    ToolOption(
                        screen = AppScreen.COMPONENTS,
                        title = "Component Database",
                        subtitle = "Every single MCBE redstone component with delays, quirks & Java differences",
                        icon = Icons.Default.Category,
                        badge = "NEW"
                    ),
                    ToolOption(
                        screen = AppScreen.COMPARATOR,
                        title = "Comparator & Container Analyzer",
                        subtitle = "Exact container fullness calculations, target signal solver & subtraction simulator",
                        icon = Icons.Default.Tune
                    ),
                    ToolOption(
                        screen = AppScreen.TIMING,
                        title = "Timings & Etho Clocks",
                        subtitle = "0.4s hopper transfer timing, repeater delay chain optimizer & pulse faders",
                        icon = Icons.Default.Timer
                    ),
                    ToolOption(
                        screen = AppScreen.SORTER,
                        title = "Item Sorters & Storage",
                        subtitle = "ImpulseSV 41+1+1+1+1 filter calculations, safe crosstalk buffer & throughput",
                        icon = Icons.Default.Inventory2
                    ),
                    ToolOption(
                        screen = AppScreen.BALLISTICS,
                        title = "Ballistics & TNT Cannons",
                        subtitle = "TNT cannon momentum vectors, payload fuse sync & trident killer DPS",
                        icon = Icons.Default.RocketLaunch
                    ),
                    ToolOption(
                        screen = AppScreen.LOGIC,
                        title = "8-Bit Bus & Logic Gates",
                        subtitle = "Decimal/Hex/Binary/Analog conversion with live bit switches & logic gate library",
                        icon = Icons.Default.Memory
                    ),
                    ToolOption(
                        screen = AppScreen.MECHANICS,
                        title = "Bedrock Quirks & Rules",
                        subtitle = "Searchable guide covering random update order, movable tile entities & no QC",
                        icon = Icons.Default.MenuBook
                    ),
                    ToolOption(
                        screen = AppScreen.BLUEPRINTS,
                        title = "Saved Presets & Notes",
                        subtitle = "Manage ${blueprints.size} saved setups, favorite circuits & custom contraption notes",
                        icon = Icons.Default.Bookmark,
                        badge = if (blueprints.isNotEmpty()) "${blueprints.size}" else null
                    ),
                    ToolOption(
                        screen = AppScreen.HOME,
                        title = "Home Dashboard",
                        subtitle = "Return to main home overview and featured contraptions",
                        icon = Icons.Default.Home
                    )
                )

                toolsList.forEach { tool ->
                    Surface(
                        onClick = {
                            showToolsSheet = false
                            viewModel.navigateTo(tool.screen)
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (currentScreen == tool.screen) PurpleContainer else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentScreen == tool.screen) PurplePrimary else DarkCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("tool_sheet_item_${tool.screen.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PurpleContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = null,
                                    tint = PurpleBright,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tool.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                    if (tool.badge != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = FilterOrange
                                        ) {
                                            Text(
                                                text = tool.badge,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = tool.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayText,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
        }
    }
}

private data class DrawerItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector,
    val badge: Int? = null,
    val badgeText: String? = null
)

private data class ToolOption(
    val screen: AppScreen,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String? = null
)
