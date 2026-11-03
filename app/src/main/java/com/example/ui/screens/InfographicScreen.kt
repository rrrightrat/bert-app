package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FilterOrange
import com.example.ui.theme.GrayText
import com.example.ui.theme.MutedText
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

/**
 * Normalized 2D coordinates (0f..1f) representing an element's position on canvas
 */
data class CanvasPoint(
    val x: Float,
    val y: Float
)

/**
 * Custom Tag data model created and managed by the user
 */
data class InfographicTag(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val colorHex: String = "#C084FC"
)

/**
 * High-level layout placement templates
 */
enum class LayoutSnapPreset(val label: String, val icon: String) {
    BOTTOM_STACK("Bottom", "⬇️"),
    TOP_HEADER("Top", "⬆️"),
    CENTER_STAGE("Center", "🎯"),
    SPLIT_CORNERS("Split", "🔀")
}

/**
 * Font typography choices
 */
enum class InfographicFont(val label: String, val composeFont: FontFamily, val androidTypeface: Typeface) {
    SANS_SERIF("Sans", FontFamily.Default, Typeface.SANS_SERIF),
    MONOSPACE("Monospace", FontFamily.Monospace, Typeface.MONOSPACE),
    SERIF("Serif", FontFamily.Serif, Typeface.SERIF)
}

/**
 * 1-Tap Infographic Preset configuration
 */
data class InfographicPreset(
    val name: String,
    val description: String,
    val icon: String,
    val bgRed: Float,
    val bgGreen: Float,
    val bgBlue: Float,
    val bgAlpha: Float,
    val defaultTitle: String,
    val defaultSubtitle: String,
    val defaultPill: String,
    val defaultTags: List<String>,
    val accentColor: Color,
    val layoutPreset: LayoutSnapPreset = LayoutSnapPreset.BOTTOM_STACK,
    val font: InfographicFont = InfographicFont.SANS_SERIF
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InfographicScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreatePost: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- State Variables ---
    // 4 primary dock tabs: 0 = Presets, 1 = Tags & Text, 2 = Placement & Style, 3 = RGBA Canvas
    var selectedTab by remember { mutableIntStateOf(0) }

    // Canvas Aspect Ratio: 1f (1:1 Square), 16f/9f (Landscape), 9f/16f (Story), 4f/3f (Schematic)
    var canvasRatio by remember { mutableFloatStateOf(1f) }

    // RGBA Color state (Red, Green, Blue: 0..255, Alpha: 0..1f)
    var rChannel by remember { mutableFloatStateOf(17f) }
    var gChannel by remember { mutableFloatStateOf(10f) }
    var bChannel by remember { mutableFloatStateOf(26f) }
    var aChannel by remember { mutableFloatStateOf(0.92f) }

    // Vignette / Shadow Overlay Darkness (0f..1f)
    var vignetteDarkness by remember { mutableFloatStateOf(0.75f) }

    // Border Glow & Outline Width (0 = none, 1 = subtle, 2 = bold neon)
    var borderGlowStyle by remember { mutableIntStateOf(1) }
    var accentGlowColor by remember { mutableStateOf(PurpleBright) }

    // Typography
    var selectedFont by remember { mutableStateOf(InfographicFont.SANS_SERIF) }

    // Text headings
    var titleText by remember { mutableStateOf("BEDROCK HOPPER CLOCK") }
    var subtitleText by remember { mutableStateOf("Exact 0.4s item transfer cycle • Zero directional lag") }
    var pillText by remember { mutableStateOf("BEDROCK 1.21 • 15 SIGNAL • SAFE") }
    var watermarkText by remember { mutableStateOf("BERT • Bedrock Infographic") }

    // User Custom Tags (instead of hardcoded observer/piston)
    var userTags by remember {
        mutableStateOf(
            listOf(
                InfographicTag(text = "Hopper Clock", colorHex = "#C084FC"),
                InfographicTag(text = "0.4s Cycle", colorHex = "#FB923C"),
                InfographicTag(text = "Bedrock 1.21", colorHex = "#4ADE80")
            )
        )
    }
    var newTagInput by remember { mutableStateOf("") }
    var newTagColorHex by remember { mutableStateOf("#C084FC") }

    // Repositionable Element Normalized Coordinates (x: 0f..1f, y: 0f..1f)
    // Headline block
    var headlinePos by remember { mutableStateOf(CanvasPoint(0.06f, 0.70f)) }
    // Tags block
    var tagsPos by remember { mutableStateOf(CanvasPoint(0.06f, 0.06f)) }
    // Stats pill block
    var pillPos by remember { mutableStateOf(CanvasPoint(0.06f, 0.62f)) }
    // Watermark / RGBA status block
    var watermarkPos by remember { mutableStateOf(CanvasPoint(0.06f, 0.92f)) }

    // Active drag feedback indicator
    var isDraggingCanvasElement by remember { mutableStateOf(false) }

    // User picked photo or diagram
    var pickedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Sheet states for text edit and export options
    var showTextEditSheet by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var lastExportedFile by remember { mutableStateOf<File?>(null) }

    // Zero-permission Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedPhotoUri = uri
        }
    }

    // Helper to snap element positions based on LayoutSnapPreset
    fun applyLayoutSnap(preset: LayoutSnapPreset) {
        when (preset) {
            LayoutSnapPreset.BOTTOM_STACK -> {
                tagsPos = CanvasPoint(0.06f, 0.06f)
                pillPos = CanvasPoint(0.06f, 0.62f)
                headlinePos = CanvasPoint(0.06f, 0.70f)
                watermarkPos = CanvasPoint(0.06f, 0.92f)
            }
            LayoutSnapPreset.TOP_HEADER -> {
                headlinePos = CanvasPoint(0.06f, 0.08f)
                tagsPos = CanvasPoint(0.06f, 0.28f)
                pillPos = CanvasPoint(0.06f, 0.82f)
                watermarkPos = CanvasPoint(0.06f, 0.92f)
            }
            LayoutSnapPreset.CENTER_STAGE -> {
                tagsPos = CanvasPoint(0.06f, 0.22f)
                headlinePos = CanvasPoint(0.06f, 0.40f)
                pillPos = CanvasPoint(0.06f, 0.64f)
                watermarkPos = CanvasPoint(0.06f, 0.92f)
            }
            LayoutSnapPreset.SPLIT_CORNERS -> {
                headlinePos = CanvasPoint(0.06f, 0.08f)
                tagsPos = CanvasPoint(0.06f, 0.82f)
                pillPos = CanvasPoint(0.55f, 0.82f)
                watermarkPos = CanvasPoint(0.06f, 0.92f)
            }
        }
    }

    // 10 Expanded Built-in Presets
    val presets = remember {
        listOf(
            InfographicPreset(
                name = "Discord Transparent",
                description = "0% Alpha background • Floating cutout for Discord sticker/emote",
                icon = "👻",
                bgRed = 0f, bgGreen = 0f, bgBlue = 0f, bgAlpha = 0f,
                defaultTitle = "DISCORD TRANSPARENT SPEC",
                defaultSubtitle = "Zero-background PNG • Seamless blend into Discord dark/light theme",
                defaultPill = "RGBA TRANSPARENT • 32-BIT",
                defaultTags = listOf("Discord Cutout", "Alpha 0%", "Sticker Ready"),
                accentColor = PurpleBright,
                layoutPreset = LayoutSnapPreset.BOTTOM_STACK,
                font = InfographicFont.MONOSPACE
            ),
            InfographicPreset(
                name = "Redstone Tech Lab",
                description = "Obsidian glass backdrop with high-contrast technical specs",
                icon = "🔬",
                bgRed = 17f, bgGreen = 10f, bgBlue = 26f, bgAlpha = 0.95f,
                defaultTitle = "BEDROCK ETHO CLOCK",
                defaultSubtitle = "Exact 0.4s item transfer cycle with comparator subtraction math",
                defaultPill = "BEDROCK 1.21 • 15 SIGNAL",
                defaultTags = listOf("Hopper Clock", "0.4s Cycle", "Bedrock 1.21"),
                accentColor = FilterOrange,
                layoutPreset = LayoutSnapPreset.BOTTOM_STACK,
                font = InfographicFont.SANS_SERIF
            ),
            InfographicPreset(
                name = "Quirk & Alert",
                description = "Redstone alert style with warning badge and breakdown",
                icon = "⚠️",
                bgRed = 185f, bgGreen = 28f, bgBlue = 28f, bgAlpha = 0.88f,
                defaultTitle = "RANDOM UPDATE ORDER",
                defaultSubtitle = "Bedrock non-deterministic tick resolution warning & fix guide",
                defaultPill = "BEDROCK QUIRK • AVOID TIE",
                defaultTags = listOf("Quirk Warning", "Tick Order", "Avoid Tie"),
                accentColor = Color(0xFFEF4444),
                layoutPreset = LayoutSnapPreset.TOP_HEADER,
                font = InfographicFont.SANS_SERIF
            ),
            InfographicPreset(
                name = "Discord Blurple",
                description = "Discord native theme palette with sleek frosted border",
                icon = "💬",
                bgRed = 43f, bgGreen = 45f, bgBlue = 49f, bgAlpha = 0.95f,
                defaultTitle = "IMPULSESV ITEM SORTER",
                defaultSubtitle = "Safe 41+1+1+1+1 overflow prevention layout and hopper bus",
                defaultPill = "DISCORD SCHEMATIC • BEDROCK",
                defaultTags = listOf("Overflow Safe", "ImpulseSV", "1-Wide Tileable"),
                accentColor = Color(0xFF5865F2),
                layoutPreset = LayoutSnapPreset.BOTTOM_STACK,
                font = InfographicFont.SANS_SERIF
            ),
            InfographicPreset(
                name = "Emerald Logic ALU",
                description = "Clean vibrant logic ALU style with emerald circuit accent",
                icon = "💎",
                bgRed = 6f, bgGreen = 78f, bgBlue = 59f, bgAlpha = 0.92f,
                defaultTitle = "8-BIT FULL ADDER",
                defaultSubtitle = "Carry-lookahead Bedrock binary ALU logic bus with XOR gates",
                defaultPill = "LOGIC ALU • ZERO DELAY",
                defaultTags = listOf("Binary ALU", "Full Adder", "Zero Delay"),
                accentColor = Color(0xFF10B981),
                layoutPreset = LayoutSnapPreset.CENTER_STAGE,
                font = InfographicFont.MONOSPACE
            ),
            InfographicPreset(
                name = "Amethyst Crystal",
                description = "Deep violet vibration resonance style for sculk mechanics",
                icon = "🔮",
                bgRed = 46f, bgGreen = 16f, bgBlue = 101f, bgAlpha = 0.92f,
                defaultTitle = "RESONANCE CALIBRATOR",
                defaultSubtitle = "15-frequency sculk sensor vibration filtering circuit",
                defaultPill = "SCULK VIBRATION • FREQ 1-15",
                defaultTags = listOf("Sculk Sensor", "Vibration 15", "Wireless"),
                accentColor = Color(0xFFC084FC),
                layoutPreset = LayoutSnapPreset.TOP_HEADER,
                font = InfographicFont.SANS_SERIF
            ),
            InfographicPreset(
                name = "Golden 0-Tick",
                description = "High-speed tick timing amber layout with speed stats",
                icon = "⚡",
                bgRed = 113f, bgGreen = 63f, bgBlue = 18f, bgAlpha = 0.92f,
                defaultTitle = "0-TICK TREE FARM",
                defaultSubtitle = "Bedrock piston head bone meal cycling at 20 operations/sec",
                defaultPill = "0-TICK SPEED • 72K LOGS/HR",
                defaultTags = listOf("0-Tick Speed", "Bone Meal", "High Yield"),
                accentColor = Color(0xFFEAB308),
                layoutPreset = LayoutSnapPreset.SPLIT_CORNERS,
                font = InfographicFont.MONOSPACE
            ),
            InfographicPreset(
                name = "Netherite Heavy",
                description = "Industrial steel & gunmetal for blast-proof survival farms",
                icon = "🛡️",
                bgRed = 24f, bgGreen = 24f, bgBlue = 27f, bgAlpha = 0.96f,
                defaultTitle = "WITHER PROOF CAGE",
                defaultSubtitle = "Bedrock ceiling Wither trapping coordinate alignment",
                defaultPill = "WITHER SAFE • 100% CONTAINMENT",
                defaultTags = listOf("Wither Cage", "Survival Safe", "Bedrock Grid"),
                accentColor = Color(0xFF94A3B8),
                layoutPreset = LayoutSnapPreset.BOTTOM_STACK,
                font = InfographicFont.SERIF
            ),
            InfographicPreset(
                name = "Cyan Blueprint",
                description = "Schematic grid layout with coordinate & chunk alignment",
                icon = "📐",
                bgRed = 8f, bgGreen = 47f, bgBlue = 73f, bgAlpha = 0.94f,
                defaultTitle = "CHUNKLOCK PERIMETER",
                defaultSubtitle = "Sub-chunk 16x16 border alignment preventing ticking desync",
                defaultPill = "CHUNK ALIGNED • 16x16",
                defaultTags = listOf("Chunk Border", "16x16 Align", "Anti-Desync"),
                accentColor = Color(0xFF38BDF8),
                layoutPreset = LayoutSnapPreset.SPLIT_CORNERS,
                font = InfographicFont.MONOSPACE
            ),
            InfographicPreset(
                name = "Obsidian Minimal",
                description = "Ultra high-contrast dark room spec for technical schematics",
                icon = "🖤",
                bgRed = 9f, bgGreen = 9f, bgBlue = 11f, bgAlpha = 0.98f,
                defaultTitle = "BEDROCK REDSTONE MATRIX",
                defaultSubtitle = "Crisp monochrome contrast spec sheet for technical documentation",
                defaultPill = "MONOCHROME SPEC • BERT 2026",
                defaultTags = listOf("Monochrome", "Spec Sheet", "Dark Mode"),
                accentColor = Color(0xFFF1F5F9),
                layoutPreset = LayoutSnapPreset.CENTER_STAGE,
                font = InfographicFont.SANS_SERIF
            )
        )
    }

    // Quick tag suggestions for 1-tap addition
    val tagSuggestions = remember {
        listOf(
            "1-Wide Tileable",
            "0-Tick Math",
            "SMP Tested",
            "Bedrock 1.21",
            "Silent Engine",
            "Break-Proof",
            "Low Lag",
            "Survival Friendly",
            "Zero Loss",
            "Anti-Desync"
        )
    }

    val tagColorPalette = remember {
        listOf(
            "#C084FC" to "Purple",
            "#FB923C" to "Orange",
            "#4ADE80" to "Emerald",
            "#EF4444" to "Red",
            "#38BDF8" to "Cyan",
            "#EAB308" to "Gold",
            "#F1F5F9" to "White"
        )
    }

    val currentComposeBgColor = Color(
        red = (rChannel / 255f).coerceIn(0f, 1f),
        green = (gChannel / 255f).coerceIn(0f, 1f),
        blue = (bChannel / 255f).coerceIn(0f, 1f),
        alpha = aChannel.coerceIn(0f, 1f)
    )

    // Formatted RGBA strings for display
    val rgbaString = "rgba(${rChannel.toInt()}, ${gChannel.toInt()}, ${bChannel.toInt()}, ${(aChannel * 100).toInt()}%)"
    val hexString = String.format(
        "#%02X%02X%02X%02X",
        (aChannel * 255).toInt(),
        rChannel.toInt(),
        gChannel.toInt(),
        bChannel.toInt()
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(PurpleBright, PurplePrimary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Infographic Compositor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Text(
                                "Custom Tags • Movable Canvas • RGBA Alpha",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleBright,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_infographic_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                actions = {
                    // Quick Aspect Ratio cycler (1:1 -> 16:9 -> 9:16 -> 4:3)
                    TextButton(
                        onClick = {
                            canvasRatio = when (canvasRatio) {
                                1f -> 16f / 9f
                                16f / 9f -> 9f / 16f
                                9f / 16f -> 4f / 3f
                                else -> 1f
                            }
                        },
                        modifier = Modifier.testTag("btn_toggle_ratio")
                    ) {
                        val ratioLabel = when (canvasRatio) {
                            1f -> "1:1"
                            16f / 9f -> "16:9"
                            9f / 16f -> "9:16"
                            else -> "4:3"
                        }
                        Text(
                            text = ratioLabel,
                            color = PurpleBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Main Action: Export / Share RGBA PNG
                    Button(
                        onClick = { showExportSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(36.dp)
                            .testTag("btn_infographic_export")
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            // ==========================================
            // 1. LIVE COMPOSITOR CANVAS (Interactive Dragging)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0A0812))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer framed container representing the exported image
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(canvasRatio)
                        .clip(RoundedCornerShape(16.dp))
                        .testTag("compositor_canvas"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = when (borderGlowStyle) {
                        0 -> null
                        1 -> androidx.compose.foundation.BorderStroke(1.5.dp, accentGlowColor.copy(alpha = 0.7f))
                        else -> androidx.compose.foundation.BorderStroke(3.dp, accentGlowColor)
                    }
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val canvasWidthPx = constraints.maxWidth.toFloat()
                        val canvasHeightPx = constraints.maxHeight.toFloat()

                        // LAYER 1: Transparency Checkerboard Background
                        TransparencyCheckerboard(modifier = Modifier.fillMaxSize())

                        // LAYER 2: Live RGBA Background Color
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(currentComposeBgColor)
                        )

                        // LAYER 3: Hero Photo / Diagram (if selected)
                        if (pickedPhotoUri != null) {
                            AsyncImage(
                                model = pickedPhotoUri,
                                contentDescription = "Composed Hero Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // LAYER 4: Gradient Vignette Overlay for Text Legibility
                        if (vignetteDarkness > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = vignetteDarkness * 0.4f),
                                                Color.Black.copy(alpha = vignetteDarkness)
                                            )
                                        )
                                    )
                            )
                        }

                        // Helper for draggable modifier on normalized points
                        fun Modifier.draggableElement(
                            point: CanvasPoint,
                            onUpdate: (CanvasPoint) -> Unit
                        ): Modifier = this.pointerInput(canvasWidthPx, canvasHeightPx) {
                            detectDragGestures(
                                onDragStart = { isDraggingCanvasElement = true },
                                onDragEnd = { isDraggingCanvasElement = false },
                                onDragCancel = { isDraggingCanvasElement = false }
                            ) { change, dragAmount ->
                                change.consume()
                                val newX = ((point.x * canvasWidthPx + dragAmount.x) / canvasWidthPx).coerceIn(0.02f, 0.90f)
                                val newY = ((point.y * canvasHeightPx + dragAmount.y) / canvasHeightPx).coerceIn(0.02f, 0.92f)
                                onUpdate(CanvasPoint(newX, newY))
                            }
                        }

                        // ==========================================
                        // MOVABLE ELEMENT 1: CUSTOM USER TAGS BLOCK
                        // ==========================================
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        (tagsPos.x * canvasWidthPx).toInt(),
                                        (tagsPos.y * canvasHeightPx).toInt()
                                    )
                                }
                                .draggableElement(tagsPos) { tagsPos = it }
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.45f))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                if (userTags.isEmpty()) {
                                    Text(
                                        "+ Add tags in dock below",
                                        fontSize = 10.sp,
                                        color = GrayText,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                } else {
                                    userTags.forEach { tag ->
                                        val tagColor = try {
                                            Color(android.graphics.Color.parseColor(tag.colorHex))
                                        } catch (_: Exception) {
                                            PurpleBright
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(tagColor.copy(alpha = 0.22f))
                                                .border(1.dp, tagColor.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = tag.text,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                fontFamily = selectedFont.composeFont,
                                                color = tagColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ==========================================
                        // MOVABLE ELEMENT 2: STATS PILL BLOCK
                        // ==========================================
                        if (pillText.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            (pillPos.x * canvasWidthPx).toInt(),
                                            (pillPos.y * canvasHeightPx).toInt()
                                        )
                                    }
                                    .draggableElement(pillPos) { pillPos = it }
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accentGlowColor.copy(alpha = 0.25f))
                                    .border(1.dp, accentGlowColor.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = pillText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontFamily = selectedFont.composeFont,
                                    color = accentGlowColor
                                )
                            }
                        }

                        // ==========================================
                        // MOVABLE ELEMENT 3: HEADLINE BLOCK (Title & Subtitle)
                        // ==========================================
                        Column(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        (headlinePos.x * canvasWidthPx).toInt(),
                                        (headlinePos.y * canvasHeightPx).toInt()
                                    )
                                }
                                .draggableElement(headlinePos) { headlinePos = it }
                                .fillMaxWidth(0.90f)
                                .clickable { showTextEditSheet = true }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = titleText,
                                fontSize = if (canvasRatio == 1f) 18.sp else 16.sp,
                                fontWeight = FontWeight.Black,
                                color = White,
                                fontFamily = selectedFont.composeFont,
                                lineHeight = 22.sp,
                                style = TextStyle(
                                    shadow = Shadow(color = Color.Black, blurRadius = 8f)
                                )
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = subtitleText,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0),
                                fontFamily = selectedFont.composeFont,
                                lineHeight = 15.sp,
                                style = TextStyle(
                                    shadow = Shadow(color = Color.Black, blurRadius = 5f)
                                )
                            )
                        }

                        // ==========================================
                        // MOVABLE ELEMENT 4: WATERMARK & RGBA PILL
                        // ==========================================
                        Row(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        (watermarkPos.x * canvasWidthPx).toInt(),
                                        (watermarkPos.y * canvasHeightPx).toInt()
                                    )
                                }
                                .draggableElement(watermarkPos) { watermarkPos = it }
                                .fillMaxWidth(0.90f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = watermarkText,
                                fontSize = 9.sp,
                                color = MutedText,
                                fontFamily = FontFamily.Monospace
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (aChannel == 0f) "ALPHA 0% (DISCORD)" else "RGBA",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (aChannel == 0f) Color(0xFF4ADE80) else FilterOrange
                                )
                            }
                        }

                        // Hint when dragging or idle
                        if (isDraggingCanvasElement) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PurpleBright.copy(alpha = 0.85f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "Repositioning element...",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. MINIMALIST 4-TAB CONTROLS DOCK
            // (Designed with least buttons & direct control)
            // ==========================================
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    // 4-Segment Tab Header
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkSurface,
                        contentColor = White,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = PurpleBright,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Presets", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Tags & Text", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Placement", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.OpenWith, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("RGBA Color", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Body
                    when (selectedTab) {
                        // TAB 0: 10 Expanded Presets
                        0 -> {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    "Tap any preset to instantly apply colors, tags, and placement:",
                                    fontSize = 11.sp,
                                    color = GrayText
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(presets) { preset ->
                                        Surface(
                                            onClick = {
                                                rChannel = preset.bgRed
                                                gChannel = preset.bgGreen
                                                bChannel = preset.bgBlue
                                                aChannel = preset.bgAlpha
                                                titleText = preset.defaultTitle
                                                subtitleText = preset.defaultSubtitle
                                                pillText = preset.defaultPill
                                                accentGlowColor = preset.accentColor
                                                selectedFont = preset.font
                                                userTags = preset.defaultTags.map { tagText ->
                                                    InfographicTag(
                                                        text = tagText,
                                                        colorHex = String.format(
                                                            "#%02X%02X%02X",
                                                            (preset.accentColor.red * 255).toInt(),
                                                            (preset.accentColor.green * 255).toInt(),
                                                            (preset.accentColor.blue * 255).toInt()
                                                        )
                                                    )
                                                }
                                                applyLayoutSnap(preset.layoutPreset)
                                                Toast.makeText(context, "Applied: ${preset.name}", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            color = DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, preset.accentColor.copy(alpha = 0.7f)),
                                            modifier = Modifier.width(180.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(preset.icon, fontSize = 18.sp)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        preset.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = White
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    preset.description,
                                                    fontSize = 10.sp,
                                                    color = GrayText,
                                                    lineHeight = 13.sp,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // TAB 1: Custom User Tags & Text
                        1 -> {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // Buttons: Photo picker & Text editor
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                androidx.activity.result.PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("btn_pick_photo")
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (pickedPhotoUri == null) "Pick Photo" else "Change Photo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { showTextEditSheet = true },
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBright),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("btn_edit_text")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = PurpleBright, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Edit Headings", color = White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Custom Tag Input Field
                                Text("Add Your Own Custom Tags:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = newTagInput,
                                        onValueChange = { newTagInput = it },
                                        placeholder = { Text("e.g. 1-Wide, Tileable, 0-Tick", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PurpleBright,
                                            unfocusedBorderColor = DarkCardBorder,
                                            focusedTextColor = White,
                                            unfocusedTextColor = White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (newTagInput.isNotBlank()) {
                                                userTags = userTags + InfographicTag(
                                                    text = newTagInput.trim(),
                                                    colorHex = newTagColorHex
                                                )
                                                newTagInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PurpleBright),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(50.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add Tag", tint = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Tag Color Swatches
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Tag Color:", fontSize = 10.sp, color = GrayText)
                                    tagColorPalette.forEach { (hex, _) ->
                                        val color = Color(android.graphics.Color.parseColor(hex))
                                        val isSelected = newTagColorHex.equals(hex, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                    width = if (isSelected) 2.dp else 0.dp,
                                                    color = if (isSelected) White else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable { newTagColorHex = hex }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Suggestions Row
                                Text("Quick Redstone Suggestions (tap to add):", fontSize = 10.sp, color = GrayText)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(tagSuggestions) { suggestion ->
                                        Surface(
                                            onClick = {
                                                if (userTags.none { it.text.equals(suggestion, ignoreCase = true) }) {
                                                    userTags = userTags + InfographicTag(
                                                        text = suggestion,
                                                        colorHex = newTagColorHex
                                                    )
                                                }
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            color = DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                                        ) {
                                            Text(
                                                "+ $suggestion",
                                                fontSize = 10.sp,
                                                color = PurpleBright,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Active Tags List with Delete Action
                                Text("Active Tags on Canvas (${userTags.size}):", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    userTags.forEach { tag ->
                                        val tagColor = try {
                                            Color(android.graphics.Color.parseColor(tag.colorHex))
                                        } catch (_: Exception) {
                                            PurpleBright
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(tagColor.copy(alpha = 0.2f))
                                                .border(1.dp, tagColor.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(tag.text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove tag",
                                                tint = White,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable {
                                                        userTags = userTags.filter { it.id != tag.id }
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // TAB 2: Placement & Layout Customizability
                        2 -> {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    "Drag elements freely on the canvas, or 1-tap snap locations:",
                                    fontSize = 11.sp,
                                    color = GrayText
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Snap Layout Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LayoutSnapPreset.entries.forEach { snap ->
                                        OutlinedButton(
                                            onClick = { applyLayoutSnap(snap) },
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBright),
                                            modifier = Modifier.weight(1f).height(36.dp)
                                        ) {
                                            Text("${snap.icon} ${snap.label}", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Aspect Ratio Selector
                                Text("Canvas Aspect Ratio:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        1f to "1:1 Square",
                                        16f / 9f to "16:9 Wide",
                                        9f / 16f to "9:16 Reel",
                                        4f / 3f to "4:3 Card"
                                    ).forEach { (ratio, label) ->
                                        val isSelected = (canvasRatio - ratio).let { Math.abs(it) < 0.05f }
                                        Surface(
                                            onClick = { canvasRatio = ratio },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) PurplePrimary else DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PurpleBright else DarkCardBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.Black else White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Typography Choice
                                Text("Typography Font Family:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    InfographicFont.entries.forEach { fontOption ->
                                        val isSelected = selectedFont == fontOption
                                        Surface(
                                            onClick = { selectedFont = fontOption },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) PurplePrimary else DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PurpleBright else DarkCardBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                fontOption.label,
                                                fontSize = 11.sp,
                                                fontFamily = fontOption.composeFont,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.Black else White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Vignette Darkness Slider
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Dark Vignette: ${(vignetteDarkness * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        color = White,
                                        modifier = Modifier.width(130.dp)
                                    )
                                    Slider(
                                        value = vignetteDarkness,
                                        onValueChange = { vignetteDarkness = it },
                                        valueRange = 0f..1f,
                                        colors = SliderDefaults.colors(thumbColor = PurpleBright, activeTrackColor = PurpleBright),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Border Glow Selector
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Border Glow:", fontSize = 11.sp, color = White, modifier = Modifier.width(100.dp))
                                    listOf(
                                        0 to "None",
                                        1 to "Subtle",
                                        2 to "Neon"
                                    ).forEach { (style, label) ->
                                        val isSelected = borderGlowStyle == style
                                        Surface(
                                            onClick = { borderGlowStyle = style },
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) PurplePrimary else DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PurpleBright else DarkCardBorder),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.Black else White,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // TAB 3: RGBA Color & Alpha Sliders
                        3 -> {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // Live RGBA Preview & Hex Code
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(rgbaString, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                                        Text(hexString, fontSize = 10.sp, color = GrayText)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // 1-Tap 0% Alpha (Discord Cutout)
                                        Surface(
                                            onClick = { aChannel = 0f },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (aChannel == 0f) Color(0xFF16A34A) else DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (aChannel == 0f) Color(0xFF4ADE80) else DarkCardBorder)
                                        ) {
                                            Text(
                                                "Alpha 0%",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        // Copy Hex button
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("RGBA Hex", hexString)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Copied $hexString to clipboard!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Hex", tint = PurpleBright, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Alpha Slider (0..1f)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Alpha: ${(aChannel * 100).toInt()}%", fontSize = 10.sp, color = White, modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = aChannel,
                                        onValueChange = { aChannel = it },
                                        valueRange = 0f..1f,
                                        colors = SliderDefaults.colors(thumbColor = PurpleBright, activeTrackColor = PurpleBright),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // RGB Sliders
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Red: ${rChannel.toInt()}", fontSize = 10.sp, color = Color(0xFFF87171), modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = rChannel,
                                        onValueChange = { rChannel = it },
                                        valueRange = 0f..255f,
                                        colors = SliderDefaults.colors(thumbColor = Color(0xFFF87171), activeTrackColor = Color(0xFFF87171)),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Green: ${gChannel.toInt()}", fontSize = 10.sp, color = Color(0xFF4ADE80), modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = gChannel,
                                        onValueChange = { gChannel = it },
                                        valueRange = 0f..255f,
                                        colors = SliderDefaults.colors(thumbColor = Color(0xFF4ADE80), activeTrackColor = Color(0xFF4ADE80)),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Blue: ${bChannel.toInt()}", fontSize = 10.sp, color = Color(0xFF60A5FA), modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = bChannel,
                                        onValueChange = { bChannel = it },
                                        valueRange = 0f..255f,
                                        colors = SliderDefaults.colors(thumbColor = Color(0xFF60A5FA), activeTrackColor = Color(0xFF60A5FA)),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODAL BOTTOM SHEET: EDIT TEXT CALLOUTS
    // ==========================================
    if (showTextEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTextEditSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Edit Infographic Headings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Main Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleBright,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subtitleText,
                    onValueChange = { subtitleText = it },
                    label = { Text("Subtitle / Spec Explanation") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleBright,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pillText,
                    onValueChange = { pillText = it },
                    label = { Text("Stats Pill Text") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FilterOrange,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = watermarkText,
                    onValueChange = { watermarkText = it },
                    label = { Text("Watermark / Signature") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleBright,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showTextEditSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ==========================================
    // MODAL BOTTOM SHEET: EXPORT & SHARE (RGBA)
    // ==========================================
    if (showExportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showExportSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Text(
                    "Export RGBA Infographic",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    "High-resolution 32-bit ARGB_8888 PNG with movable elements & custom tags",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayText
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action 1: Share Directly to Discord / Other Apps
                Button(
                    onClick = {
                        isExporting = true
                        coroutineScope.launch {
                            val bitmap = generateCompositeBitmap(
                                context = context,
                                ratio = canvasRatio,
                                r = rChannel, g = gChannel, b = bChannel, a = aChannel,
                                title = titleText,
                                subtitle = subtitleText,
                                pill = pillText,
                                watermark = watermarkText,
                                tags = userTags,
                                headlinePoint = headlinePos,
                                tagsPoint = tagsPos,
                                pillPoint = pillPos,
                                watermarkPoint = watermarkPos,
                                photoUri = pickedPhotoUri,
                                font = selectedFont,
                                vignette = vignetteDarkness,
                                borderStyle = borderGlowStyle,
                                accentColor = accentGlowColor
                            )
                            val file = saveBitmapToCache(context, bitmap)
                            lastExportedFile = file
                            isExporting = false
                            showExportSheet = false

                            if (file != null) {
                                shareImageFile(context, file)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5865F2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_discord")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share to Discord / Apps", fontWeight = FontWeight.Bold, color = White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Save PNG to Device Gallery / Pictures
                OutlinedButton(
                    onClick = {
                        isExporting = true
                        coroutineScope.launch {
                            val bitmap = generateCompositeBitmap(
                                context = context,
                                ratio = canvasRatio,
                                r = rChannel, g = gChannel, b = bChannel, a = aChannel,
                                title = titleText,
                                subtitle = subtitleText,
                                pill = pillText,
                                watermark = watermarkText,
                                tags = userTags,
                                headlinePoint = headlinePos,
                                tagsPoint = tagsPos,
                                pillPoint = pillPos,
                                watermarkPoint = watermarkPos,
                                photoUri = pickedPhotoUri,
                                font = selectedFont,
                                vignette = vignetteDarkness,
                                borderStyle = borderGlowStyle,
                                accentColor = accentGlowColor
                            )
                            val savedUri = saveBitmapToPictures(context, bitmap)
                            isExporting = false
                            showExportSheet = false
                            if (savedUri != null) {
                                Toast.makeText(context, "Saved RGBA PNG to Pictures/BERT!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Saved to cache", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBright),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_gallery")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = PurpleBright, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save PNG to Gallery", fontWeight = FontWeight.Bold, color = White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 3: Post Directly into BERT Community Feed
                OutlinedButton(
                    onClick = {
                        isExporting = true
                        coroutineScope.launch {
                            val bitmap = generateCompositeBitmap(
                                context = context,
                                ratio = canvasRatio,
                                r = rChannel, g = gChannel, b = bChannel, a = aChannel,
                                title = titleText,
                                subtitle = subtitleText,
                                pill = pillText,
                                watermark = watermarkText,
                                tags = userTags,
                                headlinePoint = headlinePos,
                                tagsPoint = tagsPos,
                                pillPoint = pillPos,
                                watermarkPoint = watermarkPos,
                                photoUri = pickedPhotoUri,
                                font = selectedFont,
                                vignette = vignetteDarkness,
                                borderStyle = borderGlowStyle,
                                accentColor = accentGlowColor
                            )
                            val file = saveBitmapToCache(context, bitmap)
                            isExporting = false
                            showExportSheet = false

                            if (file != null) {
                                onNavigateToCreatePost(file.absolutePath)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FilterOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_attach_community")
                ) {
                    Icon(Icons.Default.Forum, contentDescription = null, tint = FilterOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Post to Community Feed", fontWeight = FontWeight.Bold, color = FilterOrange)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Renders a classic transparent checkerboard background
 */
@Composable
private fun TransparencyCheckerboard(
    modifier: Modifier = Modifier,
    squareSize: Float = 24f,
    lightColor: Color = Color(0xFF26262B),
    darkColor: Color = Color(0xFF19191D)
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val cols = (width / squareSize).toInt() + 1
        val rows = (height / squareSize).toInt() + 1

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val isEven = (row + col) % 2 == 0
                val color = if (isEven) lightColor else darkColor

                drawRect(
                    color = color,
                    topLeft = Offset(col * squareSize, row * squareSize),
                    size = Size(squareSize, squareSize)
                )
            }
        }
    }
}

/**
 * Generates an Android Bitmap in 32-bit ARGB_8888 preserving alpha transparency
 * and painting all movable elements at their exact normalized coordinates
 */
private suspend fun generateCompositeBitmap(
    context: Context,
    ratio: Float,
    r: Float,
    g: Float,
    b: Float,
    a: Float,
    title: String,
    subtitle: String,
    pill: String,
    watermark: String,
    tags: List<InfographicTag>,
    headlinePoint: CanvasPoint,
    tagsPoint: CanvasPoint,
    pillPoint: CanvasPoint,
    watermarkPoint: CanvasPoint,
    photoUri: Uri?,
    font: InfographicFont,
    vignette: Float,
    borderStyle: Int,
    accentColor: Color
): Bitmap = withContext(Dispatchers.IO) {
    val width = 1080
    val height = (1080 / ratio).toInt()

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Clear transparent
    canvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

    // Layer 1: Background RGBA
    val alphaInt = (a * 255).toInt().coerceIn(0, 255)
    if (alphaInt > 0) {
        val bgPaint = Paint().apply {
            color = android.graphics.Color.argb(
                alphaInt,
                r.toInt().coerceIn(0, 255),
                g.toInt().coerceIn(0, 255),
                b.toInt().coerceIn(0, 255)
            )
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
    }

    // Layer 2: Hero Photo (if user selected one)
    if (photoUri != null) {
        try {
            context.contentResolver.openInputStream(photoUri)?.use { input ->
                val sourceBitmap = BitmapFactory.decodeStream(input)
                if (sourceBitmap != null) {
                    val destRect = Rect(30, 30, width - 30, height - 30)
                    val photoPaint = Paint().apply {
                        isAntiAlias = true
                        isFilterBitmap = true
                    }
                    canvas.drawBitmap(sourceBitmap, null, destRect, photoPaint)
                }
            }
        } catch (_: Exception) {}
    }

    // Layer 3: Dark Vignette Gradient
    if (vignette > 0f) {
        val vignettePaint = Paint().apply {
            isAntiAlias = true
            shader = android.graphics.LinearGradient(
                0f, height * 0.35f,
                0f, height.toFloat(),
                intArrayOf(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.argb((vignette * 100).toInt(), 0, 0, 0),
                    android.graphics.Color.argb((vignette * 230).toInt(), 0, 0, 0)
                ),
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
    }

    // Layer 4: Border Frame (if enabled)
    if (borderStyle > 0) {
        val strokeWidth = if (borderStyle == 1) 4f else 10f
        val borderPaint = Paint().apply {
            color = android.graphics.Color.argb(
                240,
                (accentColor.red * 255).toInt(),
                (accentColor.green * 255).toInt(),
                (accentColor.blue * 255).toInt()
            )
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            isAntiAlias = true
        }
        val borderInset = strokeWidth / 2f
        canvas.drawRoundRect(
            RectF(borderInset, borderInset, width - borderInset, height - borderInset),
            24f,
            24f,
            borderPaint
        )
    }

    val typeface = font.androidTypeface

    // Layer 5: Movable Custom Tags Block
    var curTagX = tagsPoint.x * width
    val curTagY = tagsPoint.y * height
    val tagHeight = 44f
    tags.forEach { tag ->
        val parsedColor = try {
            android.graphics.Color.parseColor(tag.colorHex)
        } catch (_: Exception) {
            android.graphics.Color.MAGENTA
        }

        val tagBgPaint = Paint().apply {
            color = android.graphics.Color.argb(
                60,
                android.graphics.Color.red(parsedColor),
                android.graphics.Color.green(parsedColor),
                android.graphics.Color.blue(parsedColor)
            )
            isAntiAlias = true
        }

        val tagStrokePaint = Paint().apply {
            color = parsedColor
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            isAntiAlias = true
        }

        val tagTextPaint = Paint().apply {
            color = parsedColor
            textSize = 24f
            this.typeface = typeface
            isFakeBoldText = true
            isAntiAlias = true
        }

        val textWidth = tagTextPaint.measureText(tag.text)
        val tagWidth = textWidth + 32f

        // Wrap to next line if exceeding width
        if (curTagX + tagWidth > width - 40f) {
            curTagX = tagsPoint.x * width
        }

        val tagRect = RectF(curTagX, curTagY, curTagX + tagWidth, curTagY + tagHeight)
        canvas.drawRoundRect(tagRect, 10f, 10f, tagBgPaint)
        canvas.drawRoundRect(tagRect, 10f, 10f, tagStrokePaint)
        canvas.drawText(tag.text, curTagX + 16f, curTagY + 30f, tagTextPaint)

        curTagX += tagWidth + 14f
    }

    // Layer 6: Movable Stats Pill
    if (pill.isNotBlank()) {
        val pillX = pillPoint.x * width
        val pillY = pillPoint.y * height
        val pillHeight = 46f

        val pillTextPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 26f
            this.typeface = typeface
            isFakeBoldText = true
            isAntiAlias = true
        }
        val pillTextWidth = pillTextPaint.measureText(pill)
        val pillRect = RectF(pillX, pillY, pillX + pillTextWidth + 36f, pillY + pillHeight)

        val pillBgPaint = Paint().apply {
            color = android.graphics.Color.argb(
                180,
                (accentColor.red * 255).toInt(),
                (accentColor.green * 255).toInt(),
                (accentColor.blue * 255).toInt()
            )
            isAntiAlias = true
        }
        canvas.drawRoundRect(pillRect, 10f, 10f, pillBgPaint)
        canvas.drawText(pill, pillX + 18f, pillY + 32f, pillTextPaint)
    }

    // Layer 7: Movable Headline (Title & Subtitle)
    val headX = headlinePoint.x * width
    var headY = headlinePoint.y * height

    val titlePaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 48f
        this.typeface = typeface
        isFakeBoldText = true
        isAntiAlias = true
        setShadowLayer(8f, 2f, 2f, android.graphics.Color.BLACK)
    }
    canvas.drawText(title, headX, headY, titlePaint)
    headY += 46f

    val subtitlePaint = Paint().apply {
        color = android.graphics.Color.argb(235, 226, 232, 240)
        textSize = 28f
        this.typeface = typeface
        isAntiAlias = true
        setShadowLayer(5f, 1f, 1f, android.graphics.Color.BLACK)
    }
    canvas.drawText(subtitle, headX, headY, subtitlePaint)

    // Layer 8: Movable Watermark & RGBA Status
    val wmX = watermarkPoint.x * width
    val wmY = watermarkPoint.y * height

    val watermarkPaint = Paint().apply {
        color = android.graphics.Color.argb(190, 168, 162, 158)
        textSize = 22f
        this.typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }
    canvas.drawText(watermark, wmX, wmY, watermarkPaint)

    val rgbaLabel = if (alphaInt == 0) "ALPHA 0% (DISCORD)" else "RGBA 32-BIT"
    val rgbaPaint = Paint().apply {
        color = if (alphaInt == 0) android.graphics.Color.argb(255, 74, 222, 128) else android.graphics.Color.argb(255, 251, 146, 60)
        textSize = 20f
        this.typeface = Typeface.MONOSPACE
        isFakeBoldText = true
        isAntiAlias = true
    }
    val rgbaMeasure = rgbaPaint.measureText(rgbaLabel)
    canvas.drawText(rgbaLabel, (width - rgbaMeasure - 40f).coerceAtLeast(wmX + 200f), wmY, rgbaPaint)

    bitmap
}

/**
 * Saves bitmap as a temporary PNG file in cache for immediate sharing
 */
private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File? {
    return try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "bert_infographic_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        file
    } catch (_: Exception) {
        null
    }
}

/**
 * Saves RGBA PNG directly to device Pictures folder via MediaStore
 */
private fun saveBitmapToPictures(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val filename = "bert_rgba_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BERT")
            }
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                fos = resolver.openOutputStream(imageUri)
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val bertDir = File(imagesDir, "BERT")
            if (!bertDir.exists()) bertDir.mkdirs()
            val image = File(bertDir, filename)
            fos = FileOutputStream(image)
            imageUri = Uri.fromFile(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        imageUri
    } catch (_: Exception) {
        null
    }
}

/**
 * Shares the generated RGBA PNG to Discord or other apps via Android Share Sheet
 */
private fun shareImageFile(context: Context, file: File) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Bedrock Redstone Infographic (RGBA Transparent) created with BERT")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Infographic to Discord"))
    } catch (e: Exception) {
        Toast.makeText(context, "Error sharing: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
