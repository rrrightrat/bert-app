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
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewCompact
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import coil.compose.AsyncImage
import com.example.R
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
 * Normalized 2D coordinate on canvas (0f..1f)
 */
data class NormPoint(val x: Float, val y: Float)

/**
 * Schematic Photo Layer placed on canvas
 */
data class CanvasPhoto(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri,
    var x: Float = 0.08f,
    var y: Float = 0.20f,
    var scale: Float = 0.44f // relative to canvas width
)

/**
 * Minecraft Tech Spec Callout Card (Reference style from Pocket Pylon / Breeze Stacker)
 */
data class MinecraftTechSpecCard(
    var title: String = "Pocket Pylon (v1.6.2)",
    var credits: String = "Made by @FishTheScholar: Main design\n@morgantic & @psbpsbp: Wiring help\n@farcoolhat: Redirection setup\nSpecial Thanks: Squibble for original pylon",
    var footprint: String = "5 x 6 x 5 (xyz)",
    var specs: String = "A very competitive pylon!\nTrigger Angle: -99.4 13.4\nConfirmation/2fa Angle: -95.0 13.4",
    var version: String = "Bedrock 1.21+",
    var bgColor: Color = Color(0xFF16132A),
    var borderColor: Color = Color(0xFF5A6EA8),
    var borderWidth: Float = 2f,
    var isVisible: Boolean = true
)

/**
 * User defined tag chip
 */
data class MinecraftTag(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val colorHex: String = "#C084FC"
)

/**
 * Pre-engineered Infographic Template inspired by the reference examples
 */
data class TechInfographicPreset(
    val name: String,
    val description: String,
    val icon: String,
    val bgRed: Float,
    val bgGreen: Float,
    val bgBlue: Float,
    val bgAlpha: Float,
    val title: String,
    val specCard: MinecraftTechSpecCard,
    val footnote: String,
    val discordInvite: String,
    val tags: List<String>,
    val accentColor: Color,
    val patternType: String = "solid" // solid, grid, scanlines, radial
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InfographicScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreatePost: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Load custom Minecraft pixel fonts installed from Google Fonts
    val silkscreenFont = remember { FontFamily(Font(R.font.silkscreen, FontWeight.Normal)) }
    val vt323Font = remember { FontFamily(Font(R.font.vt323, FontWeight.Normal)) }

    // --- State Variables ---
    // 4 primary dock tabs: 0 = Templates, 1 = Tech Spec Card, 2 = Photos & Elements, 3 = BG & Canvas
    var selectedTab by remember { mutableIntStateOf(0) }

    // Canvas Aspect Ratio: 1f (1:1 Square), 16f/9f (Landscape), 9f/16f (Reel/Story), 4f/3f (Schematic)
    var canvasRatio by remember { mutableFloatStateOf(1f) }

    // Canvas Background Color (RGBA: R, G, B: 0..255, Alpha: 0..1f)
    var bgRed by remember { mutableFloatStateOf(55f) }    // Pocket Pylon slate grey #374151
    var bgGreen by remember { mutableFloatStateOf(65f) }
    var bgBlue by remember { mutableFloatStateOf(81f) }
    var bgAlpha by remember { mutableFloatStateOf(1.0f) }

    // Background Pattern & Texture effects
    var bgPatternType by remember { mutableStateOf("grid") } // solid, grid, scanlines, vignette
    var vignetteDarkness by remember { mutableFloatStateOf(0.35f) }
    var gridSpacing by remember { mutableFloatStateOf(32f) }

    // Main Heading Title (Retro Minecraft Font)
    var mainTitle by remember { mutableStateOf("Pocket Pylon (v1.6.2)") }
    var titlePos by remember { mutableStateOf(NormPoint(0.06f, 0.05f)) }
    var titleFontType by remember { mutableStateOf("silkscreen") } // silkscreen, vt323, monospace
    var titleColor by remember { mutableStateOf(White) }

    // Tech Spec Card (The iconic dark callout card from the screenshot)
    var specCard by remember {
        mutableStateOf(
            MinecraftTechSpecCard(
                title = "Pocket Pylon (v1.6.2)",
                credits = "Made by @FishTheScholar: Main design\n@morgantic and @psbpsbp: Wiring help\n@farcoolhat: Provided redirection setup\nSpecial Thanks: Squibble for original pylon",
                footprint = "5 x 6 x 5 (xyz)",
                specs = "A very competitive pylon!\nTrigger Angle: -99.4 13.4\nConfirmation/2fa Angle: -95.0 13.4",
                version = "Bedrock 1.21+",
                bgColor = Color(0xFF16132A),
                borderColor = Color(0xFF5A6EA8),
                borderWidth = 2f,
                isVisible = true
            )
        )
    }
    var specCardPos by remember { mutableStateOf(NormPoint(0.48f, 0.16f)) }
    var specCardWidthFrac by remember { mutableFloatStateOf(0.48f) }

    // Discord Invite Pill
    var discordInviteText by remember { mutableStateOf("discord.gg/ysUqubCRGE") }
    var showDiscordPill by remember { mutableStateOf(true) }
    var discordPillPos by remember { mutableStateOf(NormPoint(0.60f, 0.90f)) }

    // Footnote Banner / Machine Note (Bottom Left in reference)
    var footnoteText by remember { mutableStateOf("en to this dropper, it can either be stackable or unstackable.") }
    var showFootnote by remember { mutableStateOf(true) }
    var footnotePos by remember { mutableStateOf(NormPoint(0.05f, 0.90f)) }

    // Custom Tags
    val activeTags = remember {
        mutableStateListOf(
            MinecraftTag(text = "Bedrock 1.21", colorHex = "#38BDF8"),
            MinecraftTag(text = "5x6x5", colorHex = "#C084FC"),
            MinecraftTag(text = "SMP Tested", colorHex = "#4ADE80")
        )
    }
    var tagsPos by remember { mutableStateOf(NormPoint(0.06f, 0.13f)) }
    var newTagInput by remember { mutableStateOf("") }
    var newTagColorHex by remember { mutableStateOf("#C084FC") }

    // Schematic Photos / Renders on Canvas
    val placedPhotos = remember { mutableStateListOf<CanvasPhoto>() }

    // Modal Sheet states
    var showCardEditorSheet by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    // Active drag indicator
    var activeDraggingElement by remember { mutableStateOf<String?>(null) }

    // Photo picker launcher (zero-permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            placedPhotos.add(
                CanvasPhoto(
                    uri = uri,
                    x = if (placedPhotos.isEmpty()) 0.06f else 0.40f,
                    y = if (placedPhotos.isEmpty()) 0.22f else 0.50f,
                    scale = 0.42f
                )
            )
            Toast.makeText(context, "Render added to canvas! Drag to position.", Toast.LENGTH_SHORT).show()
        }
    }

    // Reference Presets exactly based on the user's provided screenshot
    val referencePresets = remember {
        listOf(
            TechInfographicPreset(
                name = "Pocket Pylon",
                description = "Slate background, dark spec card with footprint & trigger angles, Discord pill",
                icon = "📦",
                bgRed = 55f, bgGreen = 65f, bgBlue = 81f, bgAlpha = 1.0f,
                title = "Pocket Pylon (v1.6.2)",
                specCard = MinecraftTechSpecCard(
                    title = "Pocket Pylon (v1.6.2)",
                    credits = "Made by @FishTheScholar: Main design\n@morgantic and @psbpsbp: Wiring help\n@farcoolhat: Provided redirection setup\nSpecial Thanks: Squibble for original pylon",
                    footprint = "5 x 6 x 5 (xyz)",
                    specs = "A very competitive pylon!\nTrigger Angle: -99.4 13.4\nConfirmation/2fa Angle: -95.0 13.4",
                    version = "Bedrock 1.21+",
                    bgColor = Color(0xFF16132A),
                    borderColor = Color(0xFF5A6EA8),
                    borderWidth = 2f
                ),
                footnote = "en to this dropper, it can either be stackable or unstackable.",
                discordInvite = "discord.gg/ysUqubCRGE",
                tags = listOf("5x6x5", "Pylon", "SMP Tested"),
                accentColor = Color(0xFF5A6EA8),
                patternType = "grid"
            ),
            TechInfographicPreset(
                name = "Breeze Stacker",
                description = "Soft coral background, vertical entity delay specs, bullet points & Discord footer",
                icon = "🌪️",
                bgRed = 248f, bgGreen = 165f, bgBlue = 165f, bgAlpha = 1.0f,
                title = "Staggered Breeze Stacker",
                specCard = MinecraftTechSpecCard(
                    title = "Staggered Breeze Stacker",
                    credits = "Designers:\n• @07km",
                    footprint = "1 x 14 x 1",
                    specs = "• Stacks 16 breezes with 10gt of delay\n• Resulting in 2gt per wind charge\n• 16 breezes total\n• 2gt between wind charges",
                    version = "Versions: • 26.2+",
                    bgColor = Color(0xFF161024),
                    borderColor = Color(0xFF7C3AED),
                    borderWidth = 2f
                ),
                footnote = "Join the Java Projectile Tech Discord for more details",
                discordInvite = "discord.gg/ysUqubCRGE",
                tags = listOf("Breeze", "10gt Delay", "26.2+"),
                accentColor = Color(0xFF7C3AED),
                patternType = "solid"
            ),
            TechInfographicPreset(
                name = "Layer Slices",
                description = "Warm parchment backdrop, multiple isometric step slices with credits",
                icon = "📐",
                bgRed = 232f, bgGreen = 216f, bgBlue = 200f, bgAlpha = 1.0f,
                title = "LAYER BY LAYER SLICES",
                specCard = MinecraftTechSpecCard(
                    title = "Assembly Steps",
                    credits = "Credits:\n@ashthescholar, on discord:\nMain machine design",
                    footprint = "3 x 3 x 4",
                    specs = "Build layers bottom-to-top\nObserve repeater tick delays",
                    version = "Bedrock 1.21+",
                    bgColor = Color(0xFF1F1D2B),
                    borderColor = Color(0xFF8B5CF6),
                    borderWidth = 2f
                ),
                footnote = "Credits: @ashthescholar on discord",
                discordInvite = "discord.gg/redstone",
                tags = listOf("Layer Slices", "Step-by-Step", "Tutorial"),
                accentColor = Color(0xFF8B5CF6),
                patternType = "grid"
            ),
            TechInfographicPreset(
                name = "Dual Perspective",
                description = "Deep navy slate, dual isometric perspective renders, clean technical labels",
                icon = "🔍",
                bgRed = 30f, bgGreen = 37f, bgBlue = 48f, bgAlpha = 1.0f,
                title = "DUAL VIEW SCHEMATIC",
                specCard = MinecraftTechSpecCard(
                    title = "Perspective Breakdown",
                    credits = "@TheScholar[@ashthescholar] Main Machine\n@farcoolhat Redirection setup",
                    footprint = "4 x 6 x 4",
                    specs = "Left and Right projection angles\nFully reversible hopper clock",
                    version = "Bedrock 1.21+",
                    bgColor = Color(0xFF111726),
                    borderColor = Color(0xFF38BDF8),
                    borderWidth = 2f
                ),
                footnote = "Renders for your viewing",
                discordInvite = "discord.gg/bert",
                tags = listOf("Dual View", "Front/Back", "Isometric"),
                accentColor = Color(0xFF38BDF8),
                patternType = "grid"
            ),
            TechInfographicPreset(
                name = "Discord Transparent",
                description = "0% Alpha background • High-res floating cutout for Discord sticker or emote",
                icon = "👻",
                bgRed = 0f, bgGreen = 0f, bgBlue = 0f, bgAlpha = 0f,
                title = "DISCORD FLOATING STICKER",
                specCard = MinecraftTechSpecCard(
                    title = "RGBA 32-Bit Spec",
                    credits = "Transparent background PNG\nBlends in Discord dark & light",
                    footprint = "Zero-Alpha",
                    specs = "Floating isometric render cutout\nLossless ARGB_8888 export",
                    version = "Discord Ready",
                    bgColor = Color(0xFF1E1035),
                    borderColor = PurpleBright,
                    borderWidth = 2f
                ),
                footnote = "Discord Transparent Emote • BERT",
                discordInvite = "discord.gg/bedrock",
                tags = listOf("Alpha 0%", "Cutout", "Discord Emote"),
                accentColor = PurpleBright,
                patternType = "solid"
            ),
            TechInfographicPreset(
                name = "Obsidian Tech Lab",
                description = "Obsidian glass backdrop with high-contrast amber/orange specs",
                icon = "🔬",
                bgRed = 17f, bgGreen = 10f, bgBlue = 26f, bgAlpha = 0.95f,
                title = "BEDROCK HOPPER CLOCK",
                specCard = MinecraftTechSpecCard(
                    title = "Item Transfer Math",
                    credits = "Made by @BERT_Engineer\nSignal 15 comparator cycle",
                    footprint = "2 x 6 x 2 (xyz)",
                    specs = "Exact 0.4s hopper transfer cycle\nZero directional tick lag",
                    version = "Bedrock 1.21.50",
                    bgColor = Color(0xFF1E1035),
                    borderColor = FilterOrange,
                    borderWidth = 2f
                ),
                footnote = "Safe 41+1 item overflow prevention layout",
                discordInvite = "discord.gg/redstone",
                tags = listOf("0.4s Cycle", "Signal 15", "Lag Free"),
                accentColor = FilterOrange,
                patternType = "scanlines"
            )
        )
    }

    val currentComposeBgColor = Color(
        red = (bgRed / 255f).coerceIn(0f, 1f),
        green = (bgGreen / 255f).coerceIn(0f, 1f),
        blue = (bgBlue / 255f).coerceIn(0f, 1f),
        alpha = bgAlpha.coerceIn(0f, 1f)
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
                                .background(Brush.linearGradient(listOf(PurpleBright, PurplePrimary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Pixel Studio Compositor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Text(
                                "Pixel Fonts • Spec Card • Discord RGBA",
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
                    // Aspect ratio cycler
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
                        Text(ratioLabel, color = PurpleBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Main Action: Export PNG
                    Button(
                        onClick = { showExportSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(36.dp)
                            .testTag("btn_infographic_export")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
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
            // 1. LIVE COMPOSITOR CANVAS (Touch Draggable)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF090710))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(canvasRatio)
                        .clip(RoundedCornerShape(14.dp))
                        .testTag("compositor_canvas"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PurpleBright.copy(alpha = 0.5f))
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val canvasW = constraints.maxWidth.toFloat()
                        val canvasH = constraints.maxHeight.toFloat()

                        // Layer 1: Transparency Checkerboard (for RGBA alpha)
                        TransparencyCheckerboard(modifier = Modifier.fillMaxSize())

                        // Layer 2: Live Background Color
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(currentComposeBgColor)
                        )

                        // Layer 3: Procedural Pattern (Grid, Scanlines, or Vignette)
                        if (bgPatternType == "grid") {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val spacingPx = 28.dp.toPx()
                                val gridColor = Color.White.copy(alpha = 0.08f)
                                var x = 0f
                                while (x < size.width) {
                                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                                    x += spacingPx
                                }
                                var y = 0f
                                while (y < size.height) {
                                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                                    y += spacingPx
                                }
                            }
                        } else if (bgPatternType == "scanlines") {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                var y = 0f
                                val scanlineColor = Color.Black.copy(alpha = 0.18f)
                                while (y < size.height) {
                                    drawLine(scanlineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
                                    y += 4.dp.toPx()
                                }
                            }
                        }

                        // Vignette darkness overlay
                        if (vignetteDarkness > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = vignetteDarkness)),
                                            center = Offset(canvasW / 2f, canvasH / 2f),
                                            radius = (canvasW.coerceAtLeast(canvasH) * 0.75f)
                                        )
                                    )
                            )
                        }

                        // Helper for draggable touch modifier
                        fun Modifier.makeDraggable(
                            elementId: String,
                            currentPos: NormPoint,
                            onMoved: (NormPoint) -> Unit
                        ): Modifier = this.pointerInput(canvasW, canvasH) {
                            detectDragGestures(
                                onDragStart = { activeDraggingElement = elementId },
                                onDragEnd = { activeDraggingElement = null },
                                onDragCancel = { activeDraggingElement = null }
                            ) { change, dragAmount ->
                                change.consume()
                                val newX = ((currentPos.x * canvasW + dragAmount.x) / canvasW).coerceIn(0.01f, 0.95f)
                                val newY = ((currentPos.y * canvasH + dragAmount.y) / canvasH).coerceIn(0.01f, 0.95f)
                                onMoved(NormPoint(newX, newY))
                            }
                        }

                        // ==========================================
                        // COMPOSITED LAYER: SCHEMATIC RENDERS / PHOTOS
                        // ==========================================
                        if (placedPhotos.isEmpty()) {
                            // Default Isometric Schematic Graphic Placeholder when no photo is added yet
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset((0.08f * canvasW).toInt(), (0.24f * canvasH).toInt())
                                    }
                                    .size(width = (canvasW * 0.38f).dp, height = (canvasH * 0.42f).dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.25f))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PurpleBright, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("TAP TO ADD RENDER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = White, fontFamily = silkscreenFont)
                                    Text("Supports multiple isometric views", fontSize = 8.sp, color = GrayText, textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            placedPhotos.forEach { photo ->
                                Box(
                                    modifier = Modifier
                                        .offset {
                                            IntOffset((photo.x * canvasW).toInt(), (photo.y * canvasH).toInt())
                                        }
                                        .makeDraggable("photo_${photo.id}", NormPoint(photo.x, photo.y)) {
                                            photo.x = it.x
                                            photo.y = it.y
                                        }
                                        .width((canvasW * photo.scale).dp)
                                ) {
                                    AsyncImage(
                                        model = photo.uri,
                                        contentDescription = "Minecraft Schematic Render",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }

                        // ==========================================
                        // COMPOSITED LAYER: TECH SPEC CALLOUT CARD
                        // (The iconic Minecraft Spec Card from the screenshot)
                        // ==========================================
                        if (specCard.isVisible) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset((specCardPos.x * canvasW).toInt(), (specCardPos.y * canvasH).toInt())
                                    }
                                    .makeDraggable("spec_card", specCardPos) { specCardPos = it }
                                    .fillMaxWidth(specCardWidthFrac)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(specCard.bgColor)
                                    .border(specCard.borderWidth.dp, specCard.borderColor, RoundedCornerShape(10.dp))
                                    .clickable { showCardEditorSheet = true }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    // Spec Card Title
                                    Text(
                                        text = specCard.title,
                                        fontFamily = silkscreenFont,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))

                                    // Credits Section
                                    if (specCard.credits.isNotBlank()) {
                                        Text(
                                            text = "Credits:",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrayText
                                        )
                                        Text(
                                            text = specCard.credits,
                                            fontSize = 8.5.sp,
                                            lineHeight = 11.sp,
                                            color = Color(0xFF93C5FD) // Highlighted blue/purple usernames
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                    }

                                    // Footprint
                                    if (specCard.footprint.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Footprint: ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = White)
                                            Text(
                                                specCard.footprint,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = vt323Font,
                                                color = FilterOrange
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }

                                    // Specs / Angles
                                    if (specCard.specs.isNotBlank()) {
                                        Text(
                                            text = specCard.specs,
                                            fontSize = 8.5.sp,
                                            lineHeight = 11.sp,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }

                                    // Version badge
                                    if (specCard.version.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = specCard.version,
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = specCard.borderColor
                                        )
                                    }
                                }
                            }
                        }

                        // ==========================================
                        // COMPOSITED LAYER: MAIN HEADING TITLE
                        // (Minecraft Pixel Font with Drop Shadow)
                        // ==========================================
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset((titlePos.x * canvasW).toInt(), (titlePos.y * canvasH).toInt())
                                }
                                .makeDraggable("main_title", titlePos) { titlePos = it }
                                .clickable { showCardEditorSheet = true }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = mainTitle,
                                fontFamily = when (titleFontType) {
                                    "vt323" -> vt323Font
                                    "monospace" -> FontFamily.Monospace
                                    else -> silkscreenFont
                                },
                                fontSize = if (canvasRatio == 1f) 18.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor,
                                style = TextStyle(
                                    shadow = Shadow(color = Color.Black, blurRadius = 8f)
                                )
                            )
                        }

                        // ==========================================
                        // COMPOSITED LAYER: CUSTOM TAGS
                        // ==========================================
                        if (activeTags.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset((tagsPos.x * canvasW).toInt(), (tagsPos.y * canvasH).toInt())
                                    }
                                    .makeDraggable("tags", tagsPos) { tagsPos = it }
                            ) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth(0.50f)
                                ) {
                                    activeTags.forEach { tag ->
                                        val tagColor = try {
                                            Color(android.graphics.Color.parseColor(tag.colorHex))
                                        } catch (_: Exception) {
                                            PurpleBright
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(tagColor.copy(alpha = 0.22f))
                                                .border(0.8.dp, tagColor.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = tag.text,
                                                fontSize = 8.5.sp,
                                                fontFamily = silkscreenFont,
                                                fontWeight = FontWeight.Bold,
                                                color = tagColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ==========================================
                        // COMPOSITED LAYER: DISCORD INVITE PILL (Bottom Right in Ref)
                        // ==========================================
                        if (showDiscordPill && discordInviteText.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset((discordPillPos.x * canvasW).toInt(), (discordPillPos.y * canvasH).toInt())
                                    }
                                    .makeDraggable("discord_pill", discordPillPos) { discordPillPos = it }
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .border(1.2.dp, Color(0xFF5865F2), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💬", fontSize = 9.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = discordInviteText,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                }
                            }
                        }

                        // ==========================================
                        // COMPOSITED LAYER: FOOTNOTE BANNER (Bottom Left in Ref)
                        // ==========================================
                        if (showFootnote && footnoteText.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset((footnotePos.x * canvasW).toInt(), (footnotePos.y * canvasH).toInt())
                                    }
                                    .makeDraggable("footnote", footnotePos) { footnotePos = it }
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(0.6.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = footnoteText,
                                    fontSize = 8.sp,
                                    fontFamily = silkscreenFont,
                                    color = Color(0xFFD1D5DB)
                                )
                            }
                        }

                        // Drag hint feedback
                        if (activeDraggingElement != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PurpleBright.copy(alpha = 0.9f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Dragging element", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. STREAMLINED 4-TAB CONTROLS DOCK
            // (Designed with least buttons & direct control)
            // ==========================================
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
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
                            text = { Text("Templates", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Spec Card", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Renders & Tags", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("BG & Alpha", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(15.dp)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (selectedTab) {
                        // TAB 0: TEMPLATES (Directly inspired by the reference screenshot)
                        0 -> {
                            Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                                Text(
                                    "Tap a template matching the reference Minecraft infographics:",
                                    fontSize = 11.sp,
                                    color = GrayText
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(referencePresets) { preset ->
                                        Surface(
                                            onClick = {
                                                bgRed = preset.bgRed
                                                bgGreen = preset.bgGreen
                                                bgBlue = preset.bgBlue
                                                bgAlpha = preset.bgAlpha
                                                mainTitle = preset.title
                                                specCard = preset.specCard.copy()
                                                footnoteText = preset.footnote
                                                discordInviteText = preset.discordInvite
                                                bgPatternType = preset.patternType
                                                activeTags.clear()
                                                preset.tags.forEach {
                                                    activeTags.add(MinecraftTag(text = it, colorHex = "#" + Integer.toHexString(preset.accentColor.hashCode()).takeLast(6)))
                                                }
                                                Toast.makeText(context, "Applied: ${preset.name}", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            color = DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, preset.accentColor.copy(alpha = 0.7f)),
                                            modifier = Modifier.width(170.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(preset.icon, fontSize = 16.sp)
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Text(
                                                        preset.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = White,
                                                        fontFamily = silkscreenFont
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    preset.description,
                                                    fontSize = 9.sp,
                                                    color = GrayText,
                                                    lineHeight = 12.sp,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // TAB 1: TECH SPEC CARD (Pocket Pylon / Breeze Stacker Card)
                        1 -> {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Minecraft Tech Spec Card", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White, fontFamily = silkscreenFont)
                                    OutlinedButton(
                                        onClick = { showCardEditorSheet = true },
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBright),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = PurpleBright, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit Full Text", fontSize = 10.sp, color = White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Quick Footprint Input
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = specCard.footprint,
                                        onValueChange = { specCard = specCard.copy(footprint = it) },
                                        label = { Text("Footprint (e.g. 5 x 6 x 5)", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = FilterOrange,
                                            unfocusedBorderColor = DarkCardBorder,
                                            focusedTextColor = White,
                                            unfocusedTextColor = White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = specCard.version,
                                        onValueChange = { specCard = specCard.copy(version = it) },
                                        label = { Text("Version", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(0.7f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PurpleBright,
                                            unfocusedBorderColor = DarkCardBorder,
                                            focusedTextColor = White,
                                            unfocusedTextColor = White
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Card Width Slider
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Card Width: ${(specCardWidthFrac * 100).toInt()}%", fontSize = 10.sp, color = GrayText, modifier = Modifier.width(105.dp))
                                    Slider(
                                        value = specCardWidthFrac,
                                        onValueChange = { specCardWidthFrac = it },
                                        valueRange = 0.35f..0.85f,
                                        colors = SliderDefaults.colors(thumbColor = PurpleBright, activeTrackColor = PurpleBright),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Quick border color swatches for card
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Card Border:", fontSize = 10.sp, color = GrayText, modifier = Modifier.width(75.dp))
                                    listOf(
                                        Color(0xFF5A6EA8) to "Slate",
                                        Color(0xFF7C3AED) to "Violet",
                                        Color(0xFF38BDF8) to "Cyan",
                                        Color(0xFFEF4444) to "Red",
                                        Color(0xFFFB923C) to "Orange",
                                        Color.White to "White"
                                    ).forEach { (color, _) ->
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 6.dp)
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                    width = if (specCard.borderColor == color) 2.dp else 0.dp,
                                                    color = White,
                                                    shape = CircleShape
                                                )
                                                .clickable { specCard = specCard.copy(borderColor = color) }
                                        )
                                    }
                                }
                            }
                        }

                        // TAB 2: RENDERS & USER TAGS
                        2 -> {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // Add Schematic Render Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("+ Add Render", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    if (placedPhotos.isNotEmpty()) {
                                        OutlinedButton(
                                            onClick = { placedPhotos.clear() },
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                            modifier = Modifier.height(38.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Clear Renders", color = Color(0xFFEF4444), fontSize = 10.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Custom Tags Creator
                                Text("Add Custom Redstone Tags:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White, fontFamily = silkscreenFont)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = {
                                            if (newTagInput.isNotBlank()) {
                                                activeTags.add(MinecraftTag(text = newTagInput.trim(), colorHex = newTagColorHex))
                                                newTagInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PurpleBright),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add Tag", tint = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Active Tags with Remove action
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    activeTags.forEach { tag ->
                                        val tagColor = try {
                                            Color(android.graphics.Color.parseColor(tag.colorHex))
                                        } catch (_: Exception) {
                                            PurpleBright
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(tagColor.copy(alpha = 0.2f))
                                                .border(1.dp, tagColor.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text(tag.text, fontSize = 10.sp, color = tagColor, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = White,
                                                modifier = Modifier.size(12.dp).clickable { activeTags.remove(tag) }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Discord Invite Pill and Footnote Quick Fields
                                OutlinedTextField(
                                    value = discordInviteText,
                                    onValueChange = { discordInviteText = it },
                                    label = { Text("Discord Invite URL", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF5865F2),
                                        unfocusedBorderColor = DarkCardBorder,
                                        focusedTextColor = White,
                                        unfocusedTextColor = White
                                    )
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                OutlinedTextField(
                                    value = footnoteText,
                                    onValueChange = { footnoteText = it },
                                    label = { Text("Bottom Footnote Note / Tip", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PurpleBright,
                                        unfocusedBorderColor = DarkCardBorder,
                                        focusedTextColor = White,
                                        unfocusedTextColor = White
                                    )
                                )
                            }
                        }

                        // TAB 3: BG & RGBA SLIDERS (Least buttons, clean sliders)
                        3 -> {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "rgba(${bgRed.toInt()}, ${bgGreen.toInt()}, ${bgBlue.toInt()}, ${(bgAlpha * 100).toInt()}%)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = White,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            if (bgAlpha == 0f) "0% ALPHA (DISCORD TRANSPARENT)" else "ARGB_8888 32-BIT",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (bgAlpha == 0f) Color(0xFF4ADE80) else FilterOrange
                                        )
                                    }

                                    // 1-Tap 0% Alpha button
                                    Surface(
                                        onClick = { bgAlpha = 0f },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (bgAlpha == 0f) Color(0xFF16A34A) else DarkSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (bgAlpha == 0f) Color(0xFF4ADE80) else DarkCardBorder)
                                    ) {
                                        Text("0% Alpha Cutout", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Alpha Slider
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Alpha: ${(bgAlpha * 100).toInt()}%", fontSize = 10.sp, color = White, modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = bgAlpha,
                                        onValueChange = { bgAlpha = it },
                                        valueRange = 0f..1f,
                                        colors = SliderDefaults.colors(thumbColor = PurpleBright, activeTrackColor = PurpleBright),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // RGB Sliders
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Red: ${bgRed.toInt()}", fontSize = 10.sp, color = Color(0xFFF87171), modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = bgRed,
                                        onValueChange = { bgRed = it },
                                        valueRange = 0f..255f,
                                        colors = SliderDefaults.colors(thumbColor = Color(0xFFF87171), activeTrackColor = Color(0xFFF87171)),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Green: ${bgGreen.toInt()}", fontSize = 10.sp, color = Color(0xFF4ADE80), modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = bgGreen,
                                        onValueChange = { bgGreen = it },
                                        valueRange = 0f..255f,
                                        colors = SliderDefaults.colors(thumbColor = Color(0xFF4ADE80), activeTrackColor = Color(0xFF4ADE80)),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Blue: ${bgBlue.toInt()}", fontSize = 10.sp, color = Color(0xFF60A5FA), modifier = Modifier.width(70.dp))
                                    Slider(
                                        value = bgBlue,
                                        onValueChange = { bgBlue = it },
                                        valueRange = 0f..255f,
                                        colors = SliderDefaults.colors(thumbColor = Color(0xFF60A5FA), activeTrackColor = Color(0xFF60A5FA)),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Pattern Mode Selector (Solid, Grid, Scanlines)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Pattern:", fontSize = 10.sp, color = GrayText, modifier = Modifier.width(60.dp))
                                    listOf(
                                        "solid" to "Solid",
                                        "grid" to "Grid",
                                        "scanlines" to "Scanlines"
                                    ).forEach { (mode, label) ->
                                        val isSelected = bgPatternType == mode
                                        Surface(
                                            onClick = { bgPatternType = mode },
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) PurplePrimary else DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PurpleBright else DarkCardBorder),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(label, fontSize = 10.sp, color = if (isSelected) Color.Black else White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODAL BOTTOM SHEET: EDIT HEADINGS & SPEC CARD
    // ==========================================
    if (showCardEditorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCardEditorSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Edit Headings & Specs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    fontFamily = silkscreenFont
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = mainTitle,
                    onValueChange = { mainTitle = it },
                    label = { Text("Infographic Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurpleBright, unfocusedBorderColor = DarkCardBorder, focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = specCard.title,
                    onValueChange = { specCard = specCard.copy(title = it) },
                    label = { Text("Spec Card Heading") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurpleBright, unfocusedBorderColor = DarkCardBorder, focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = specCard.credits,
                    onValueChange = { specCard = specCard.copy(credits = it) },
                    label = { Text("Credits (Designers, Wiring, Helpers)") },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurpleBright, unfocusedBorderColor = DarkCardBorder, focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = specCard.specs,
                    onValueChange = { specCard = specCard.copy(specs = it) },
                    label = { Text("Specs / Angles / Delay (Bullet points)") },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FilterOrange, unfocusedBorderColor = DarkCardBorder, focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showCardEditorSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // ==========================================
    // MODAL BOTTOM SHEET: EXPORT RGBA 32-BIT PNG
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
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    "Export RGBA Infographic",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    fontFamily = silkscreenFont
                )
                Text(
                    "Lossless 32-bit ARGB_8888 PNG preserving pixel typography & alpha transparency",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action 1: Share Directly to Discord
                Button(
                    onClick = {
                        isExporting = true
                        coroutineScope.launch {
                            val bitmap = generateMinecraftInfographicBitmap(
                                context = context,
                                ratio = canvasRatio,
                                bgRed = bgRed, bgGreen = bgGreen, bgBlue = bgBlue, bgAlpha = bgAlpha,
                                bgPattern = bgPatternType,
                                vignette = vignetteDarkness,
                                title = mainTitle,
                                titlePoint = titlePos,
                                specCard = specCard,
                                specCardPoint = specCardPos,
                                specCardWidth = specCardWidthFrac,
                                discordInvite = discordInviteText,
                                discordPoint = discordPillPos,
                                showDiscord = showDiscordPill,
                                footnote = footnoteText,
                                footnotePoint = footnotePos,
                                showFootnote = showFootnote,
                                tags = activeTags,
                                tagsPoint = tagsPos,
                                photos = placedPhotos
                            )
                            val file = saveBitmapToCache(context, bitmap)
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

                // Action 2: Save to Gallery
                OutlinedButton(
                    onClick = {
                        isExporting = true
                        coroutineScope.launch {
                            val bitmap = generateMinecraftInfographicBitmap(
                                context = context,
                                ratio = canvasRatio,
                                bgRed = bgRed, bgGreen = bgGreen, bgBlue = bgBlue, bgAlpha = bgAlpha,
                                bgPattern = bgPatternType,
                                vignette = vignetteDarkness,
                                title = mainTitle,
                                titlePoint = titlePos,
                                specCard = specCard,
                                specCardPoint = specCardPos,
                                specCardWidth = specCardWidthFrac,
                                discordInvite = discordInviteText,
                                discordPoint = discordPillPos,
                                showDiscord = showDiscordPill,
                                footnote = footnoteText,
                                footnotePoint = footnotePos,
                                showFootnote = showFootnote,
                                tags = activeTags,
                                tagsPoint = tagsPos,
                                photos = placedPhotos
                            )
                            val uri = saveBitmapToPictures(context, bitmap)
                            isExporting = false
                            showExportSheet = false
                            if (uri != null) {
                                Toast.makeText(context, "Saved RGBA PNG to Pictures/BERT!", Toast.LENGTH_LONG).show()
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

                // Action 3: Post to BERT Feed
                OutlinedButton(
                    onClick = {
                        isExporting = true
                        coroutineScope.launch {
                            val bitmap = generateMinecraftInfographicBitmap(
                                context = context,
                                ratio = canvasRatio,
                                bgRed = bgRed, bgGreen = bgGreen, bgBlue = bgBlue, bgAlpha = bgAlpha,
                                bgPattern = bgPatternType,
                                vignette = vignetteDarkness,
                                title = mainTitle,
                                titlePoint = titlePos,
                                specCard = specCard,
                                specCardPoint = specCardPos,
                                specCardWidth = specCardWidthFrac,
                                discordInvite = discordInviteText,
                                discordPoint = discordPillPos,
                                showDiscord = showDiscordPill,
                                footnote = footnoteText,
                                footnotePoint = footnotePos,
                                showFootnote = showFootnote,
                                tags = activeTags,
                                tagsPoint = tagsPos,
                                photos = placedPhotos
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

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Transparent Checkerboard Preview
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
 * Generates high-res 32-bit ARGB_8888 PNG using the real Silkscreen and VT323 pixel fonts
 */
private suspend fun generateMinecraftInfographicBitmap(
    context: Context,
    ratio: Float,
    bgRed: Float,
    bgGreen: Float,
    bgBlue: Float,
    bgAlpha: Float,
    bgPattern: String,
    vignette: Float,
    title: String,
    titlePoint: NormPoint,
    specCard: MinecraftTechSpecCard,
    specCardPoint: NormPoint,
    specCardWidth: Float,
    discordInvite: String,
    discordPoint: NormPoint,
    showDiscord: Boolean,
    footnote: String,
    footnotePoint: NormPoint,
    showFootnote: Boolean,
    tags: List<MinecraftTag>,
    tagsPoint: NormPoint,
    photos: List<CanvasPhoto>
): Bitmap = withContext(Dispatchers.IO) {
    val width = 1080
    val height = (1080 / ratio).toInt()

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Clear transparent
    canvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

    // Load Silkscreen and VT323 Typefaces
    val silkscreenTypeface = ResourcesCompat.getFont(context, R.font.silkscreen) ?: Typeface.MONOSPACE
    val vt323Typeface = ResourcesCompat.getFont(context, R.font.vt323) ?: Typeface.MONOSPACE

    // Layer 1: Background RGBA
    val alphaInt = (bgAlpha * 255).toInt().coerceIn(0, 255)
    if (alphaInt > 0) {
        val bgPaint = Paint().apply {
            color = android.graphics.Color.argb(
                alphaInt,
                bgRed.toInt().coerceIn(0, 255),
                bgGreen.toInt().coerceIn(0, 255),
                bgBlue.toInt().coerceIn(0, 255)
            )
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
    }

    // Layer 2: Procedural Pattern (Grid / Scanlines)
    if (bgPattern == "grid" && alphaInt > 0) {
        val gridPaint = Paint().apply {
            color = android.graphics.Color.argb(20, 255, 255, 255)
            strokeWidth = 2f
            isAntiAlias = true
        }
        val step = 48f
        var x = 0f
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
            x += step
        }
        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
            y += step
        }
    } else if (bgPattern == "scanlines" && alphaInt > 0) {
        val scanlinePaint = Paint().apply {
            color = android.graphics.Color.argb(35, 0, 0, 0)
            strokeWidth = 3f
        }
        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, scanlinePaint)
            y += 8f
        }
    }

    // Layer 3: Vignette Gradient
    if (vignette > 0f && alphaInt > 0) {
        val vignettePaint = Paint().apply {
            isAntiAlias = true
            shader = android.graphics.RadialGradient(
                width / 2f, height / 2f,
                width.coerceAtLeast(height) * 0.75f,
                intArrayOf(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.argb((vignette * 180).toInt(), 0, 0, 0)
                ),
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
    }

    // Layer 4: Photos / Schematic Renders
    photos.forEach { photo ->
        try {
            context.contentResolver.openInputStream(photo.uri)?.use { stream ->
                val source = BitmapFactory.decodeStream(stream)
                if (source != null) {
                    val destW = (width * photo.scale).toInt()
                    val destH = (destW * (source.height.toFloat() / source.width)).toInt()
                    val destX = (photo.x * width).toInt()
                    val destY = (photo.y * height).toInt()
                    val destRect = Rect(destX, destY, destX + destW, destY + destH)
                    val pPaint = Paint().apply {
                        isAntiAlias = true
                        isFilterBitmap = true
                    }
                    canvas.drawBitmap(source, null, destRect, pPaint)
                }
            }
        } catch (_: Exception) {}
    }

    // Layer 5: Tech Spec Callout Card (Pocket Pylon / Breeze Stacker Card)
    if (specCard.isVisible) {
        val cardX = specCardPoint.x * width
        val cardY = specCardPoint.y * height
        val cardW = width * specCardWidth

        // Parse card colors
        val cardBgPaint = Paint().apply {
            color = android.graphics.Color.argb(
                (specCard.bgColor.alpha * 255).toInt(),
                (specCard.bgColor.red * 255).toInt(),
                (specCard.bgColor.green * 255).toInt(),
                (specCard.bgColor.blue * 255).toInt()
            )
            isAntiAlias = true
        }

        val cardBorderPaint = Paint().apply {
            color = android.graphics.Color.argb(
                (specCard.borderColor.alpha * 255).toInt(),
                (specCard.borderColor.red * 255).toInt(),
                (specCard.borderColor.green * 255).toInt(),
                (specCard.borderColor.blue * 255).toInt()
            )
            style = Paint.Style.STROKE
            strokeWidth = specCard.borderWidth * 2f
            isAntiAlias = true
        }

        // Draw card content text
        val titlePaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            typeface = silkscreenTypeface
            isFakeBoldText = true
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = android.graphics.Color.argb(255, 156, 163, 175)
            textSize = 20f
            typeface = silkscreenTypeface
            isAntiAlias = true
        }

        val creditsPaint = Paint().apply {
            color = android.graphics.Color.argb(255, 147, 197, 253) // Highlight blue/purple usernames
            textSize = 20f
            isAntiAlias = true
        }

        val specsPaint = Paint().apply {
            color = android.graphics.Color.argb(240, 226, 232, 240)
            textSize = 20f
            isAntiAlias = true
        }

        val footprintPaint = Paint().apply {
            color = android.graphics.Color.argb(255, 251, 146, 60) // Orange
            textSize = 26f
            typeface = vt323Typeface
            isFakeBoldText = true
            isAntiAlias = true
        }

        // Measure card height dynamically
        var textY = cardY + 40f
        textY += 32f // title
        if (specCard.credits.isNotBlank()) {
            textY += 26f + (specCard.credits.lines().size * 28f)
        }
        if (specCard.footprint.isNotBlank()) {
            textY += 32f
        }
        if (specCard.specs.isNotBlank()) {
            textY += (specCard.specs.lines().size * 28f)
        }
        if (specCard.version.isNotBlank()) {
            textY += 32f
        }
        val cardH = (textY - cardY) + 24f

        val cardRect = RectF(cardX, cardY, cardX + cardW, cardY + cardH)
        canvas.drawRoundRect(cardRect, 20f, 20f, cardBgPaint)
        canvas.drawRoundRect(cardRect, 20f, 20f, cardBorderPaint)

        // Draw card texts inside
        var curY = cardY + 44f
        canvas.drawText(specCard.title, cardX + 24f, curY, titlePaint)
        curY += 34f

        if (specCard.credits.isNotBlank()) {
            canvas.drawText("Credits:", cardX + 24f, curY, headerPaint)
            curY += 28f
            specCard.credits.lines().forEach { line ->
                canvas.drawText(line, cardX + 24f, curY, creditsPaint)
                curY += 28f
            }
            curY += 6f
        }

        if (specCard.footprint.isNotBlank()) {
            canvas.drawText("Footprint: ", cardX + 24f, curY, titlePaint)
            val fpWidth = titlePaint.measureText("Footprint: ")
            canvas.drawText(specCard.footprint, cardX + 24f + fpWidth, curY + 2f, footprintPaint)
            curY += 34f
        }

        if (specCard.specs.isNotBlank()) {
            specCard.specs.lines().forEach { line ->
                canvas.drawText(line, cardX + 24f, curY, specsPaint)
                curY += 28f
            }
        }

        if (specCard.version.isNotBlank()) {
            val verPaint = Paint().apply {
                color = android.graphics.Color.argb(200, 168, 85, 247)
                textSize = 18f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            canvas.drawText(specCard.version, cardX + 24f, curY + 8f, verPaint)
        }
    }

    // Layer 6: Main Title (Silkscreen Pixel Font)
    val mainTitlePaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 46f
        typeface = silkscreenTypeface
        isFakeBoldText = true
        isAntiAlias = true
        setShadowLayer(8f, 2f, 2f, android.graphics.Color.BLACK)
    }
    canvas.drawText(title, titlePoint.x * width, titlePoint.y * height + 40f, mainTitlePaint)

    // Layer 7: Custom Tags
    var tagX = tagsPoint.x * width
    val tagY = tagsPoint.y * height
    tags.forEach { tag ->
        val parsed = try {
            android.graphics.Color.parseColor(tag.colorHex)
        } catch (_: Exception) {
            android.graphics.Color.CYAN
        }

        val tagBg = Paint().apply {
            color = android.graphics.Color.argb(60, android.graphics.Color.red(parsed), android.graphics.Color.green(parsed), android.graphics.Color.blue(parsed))
            isAntiAlias = true
        }
        val tagBorder = Paint().apply {
            color = parsed
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        val tagText = Paint().apply {
            color = parsed
            textSize = 22f
            typeface = silkscreenTypeface
            isFakeBoldText = true
            isAntiAlias = true
        }

        val tagW = tagText.measureText(tag.text) + 28f
        val tagRect = RectF(tagX, tagY, tagX + tagW, tagY + 40f)
        canvas.drawRoundRect(tagRect, 8f, 8f, tagBg)
        canvas.drawRoundRect(tagRect, 8f, 8f, tagBorder)
        canvas.drawText(tag.text, tagX + 14f, tagY + 28f, tagText)
        tagX += tagW + 12f
    }

    // Layer 8: Discord Invite Pill (Bottom Right)
    if (showDiscord && discordInvite.isNotBlank()) {
        val dpX = discordPoint.x * width
        val dpY = discordPoint.y * height

        val dTextPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 22f
            typeface = Typeface.MONOSPACE
            isFakeBoldText = true
            isAntiAlias = true
        }
        val pillW = dTextPaint.measureText(discordInvite) + 60f
        val pillRect = RectF(dpX, dpY, dpX + pillW, dpY + 44f)

        val pillBg = Paint().apply {
            color = android.graphics.Color.argb(200, 0, 0, 0)
            isAntiAlias = true
        }
        val pillBorder = Paint().apply {
            color = android.graphics.Color.argb(255, 88, 101, 242) // Discord Blurple
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(pillRect, 12f, 12f, pillBg)
        canvas.drawRoundRect(pillRect, 12f, 12f, pillBorder)
        canvas.drawText("💬 $discordInvite", dpX + 16f, dpY + 30f, dTextPaint)
    }

    // Layer 9: Footnote Banner (Bottom Left)
    if (showFootnote && footnote.isNotBlank()) {
        val fnX = footnotePoint.x * width
        val fnY = footnotePoint.y * height

        val fnPaint = Paint().apply {
            color = android.graphics.Color.argb(220, 209, 213, 219)
            textSize = 20f
            typeface = silkscreenTypeface
            isAntiAlias = true
        }
        canvas.drawText(footnote, fnX, fnY + 24f, fnPaint)
    }

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
        val filename = "bert_minecraft_spec_${System.currentTimeMillis()}.png"
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
            putExtra(Intent.EXTRA_TEXT, "Bedrock Redstone Infographic created with BERT Pixel Studio Compositor")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Infographic to Discord"))
    } catch (e: Exception) {
        Toast.makeText(context, "Error sharing: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
