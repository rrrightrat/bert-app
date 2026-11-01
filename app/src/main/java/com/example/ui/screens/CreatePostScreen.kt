package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CommunityPost
import com.example.ui.CommunityViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GrayText
import com.example.ui.theme.MutedText
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White

private val CATEGORIES = listOf(
    "Contraptions", "Clocks", "Logic", "Cannons", "Piston Doors", "Item Sorters", "Farms", "Quirks", "Miscellaneous"
)

private val HEADING_STYLES = listOf(
    "H1" to "H1 Bold",
    "H2" to "H2 Subtitle",
    "Normal" to "Body Text",
    "Code" to "Terminal/Code"
)

private val FONT_FAMILIES = listOf(
    "Default" to "Modern Sans",
    "Monospace" to "Monospace",
    "Serif" to "Classic Serif"
)

private val TEXT_COLORS = listOf(
    "#FFFFFF" to Color(0xFFFFFFFF),
    "#C084FC" to Color(0xFFC084FC), // Purple
    "#4ADE80" to Color(0xFF4ADE80), // Lime Green
    "#F87171" to Color(0xFFF87171), // Redstone Red
    "#FBBF24" to Color(0xFFFBBF24), // Amber Gold
    "#38BDF8" to Color(0xFF38BDF8)  // Sky Blue
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CommunityViewModel,
    existingPost: CommunityPost? = null,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf(existingPost?.title ?: "") }
    var content by remember { mutableStateOf(existingPost?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(existingPost?.category ?: "Contraptions") }
    var selectedHeading by remember { mutableStateOf(existingPost?.headingStyle ?: "H1") }
    var selectedFont by remember { mutableStateOf(existingPost?.fontFamily ?: "Default") }
    var selectedColorHex by remember { mutableStateOf(existingPost?.textColorHex ?: "#FFFFFF") }
    var mediaUrl by remember { mutableStateOf(existingPost?.mediaUrl ?: "") }
    var mediaType by remember { mutableStateOf(existingPost?.mediaType ?: "none") }

    // Android Zero-Permission Photo/Video Picker
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            mediaUrl = uri.toString()
            mediaType = "image"
        }
    }

    val selectedComposeFont = when (selectedFont) {
        "Monospace" -> FontFamily.Monospace
        "Serif" -> FontFamily.Serif
        else -> FontFamily.Default
    }

    val selectedComposeColor = Color(android.graphics.Color.parseColor(selectedColorHex))

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingPost != null) "Edit Redstone Creation" else "Post Redstone Creation",
                        fontWeight = FontWeight.Bold,
                        color = White,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("create_post_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveOrUpdatePost(
                                title = title,
                                content = content,
                                category = selectedCategory,
                                headingStyle = selectedHeading,
                                fontFamily = selectedFont,
                                textColorHex = selectedColorHex,
                                mediaUrl = mediaUrl,
                                mediaType = mediaType,
                                onSuccess = onNavigateBack
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurplePrimary,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("publish_post_button"),
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (existingPost != null) "Update" else "Publish", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Category Selection
            Text(
                text = "Select Category",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GrayText
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CATEGORIES) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurplePrimary,
                            selectedLabelColor = White,
                            containerColor = DarkSurfaceVariant,
                            labelColor = GrayText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) PurpleBright else DarkCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Creation Title (e.g. 3x3 Seamless Door, 4GT Pulse Gen)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleBright,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedLabelColor = PurpleBright,
                    unfocusedLabelColor = GrayText
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_title_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text Styling Controls Bar
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Text Styling & Typography",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleBright
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Heading Style
                    Text("Heading & Emphasis:", fontSize = 11.sp, color = GrayText)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(HEADING_STYLES) { (key, label) ->
                            val isSelected = selectedHeading == key
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PurplePrimary else DarkSurfaceVariant)
                                    .border(1.dp, if (isSelected) PurpleBright else DarkCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedHeading = key }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) White else GrayText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Font Family
                    Text("Font Family:", fontSize = 11.sp, color = GrayText)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(FONT_FAMILIES) { (key, label) ->
                            val isSelected = selectedFont == key
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PurplePrimary else DarkSurfaceVariant)
                                    .border(1.dp, if (isSelected) PurpleBright else DarkCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedFont = key }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) White else GrayText,
                                    fontFamily = when (key) {
                                        "Monospace" -> FontFamily.Monospace
                                        "Serif" -> FontFamily.Serif
                                        else -> FontFamily.Default
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Text Color Swatches
                    Text("Font Color:", fontSize = 11.sp, color = GrayText)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(TEXT_COLORS) { (hex, color) ->
                            val isSelected = selectedColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        2.dp,
                                        if (isSelected) PurpleBright else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedColorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Detailed Description, Timings, Block List & Instructions") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleBright,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedLabelColor = PurpleBright,
                    unfocusedLabelColor = GrayText
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
                    .testTag("post_content_input"),
                maxLines = 15
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Media Attachment (Photos & Videos)
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Media & Visual Attachments (Photos / Videos)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Attach redstone screenshots, schematics, or video clip links",
                        fontSize = 11.sp,
                        color = GrayText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                mediaPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceVariant,
                                contentColor = White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp), tint = PurpleBright)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick Media", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                mediaUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=800&q=80"
                                mediaType = "image"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceVariant,
                                contentColor = White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp), tint = PurpleBright)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sample Image", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = mediaUrl,
                        onValueChange = {
                            mediaUrl = it
                            if (it.isNotBlank() && mediaType == "none") {
                                mediaType = if (it.contains("mp4", ignoreCase = true) || it.contains("video", ignoreCase = true) || it.contains("youtube", ignoreCase = true)) "video" else "image"
                            }
                        },
                        label = { Text("Or Paste Image / Video URL") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleBright,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedLabelColor = PurpleBright,
                            unfocusedLabelColor = GrayText
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (mediaUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Media Type:", fontSize = 11.sp, color = GrayText)
                            FilterChip(
                                selected = mediaType == "image",
                                onClick = { mediaType = "image" },
                                label = { Text("Photo/Image", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = mediaType == "video",
                                onClick = { mediaType = "video" },
                                label = { Text("Video", fontSize = 11.sp) }
                            )
                            IconButton(onClick = { mediaUrl = ""; mediaType = "none" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Check, contentDescription = "Clear", tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Live WYSIWYG Preview Card
            Text(
                text = "Live Creation Preview (How others will see it)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Tag & Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PurpleContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedCategory,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleBright
                            )
                        }
                        Text("Preview", fontSize = 11.sp, color = MutedText)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title with chosen Heading style
                    val titleSize = when (selectedHeading) {
                        "H1" -> 20.sp
                        "H2" -> 17.sp
                        "Code" -> 15.sp
                        else -> 16.sp
                    }
                    Text(
                        text = title.ifEmpty { "Your Redstone Creation Title" },
                        fontSize = titleSize,
                        fontWeight = if (selectedHeading == "Code") FontWeight.Medium else FontWeight.Bold,
                        color = selectedComposeColor,
                        fontFamily = selectedComposeFont
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Media Preview
                    if (mediaUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = mediaUrl,
                                contentDescription = "Media preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (mediaType == "video") {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PlayCircle,
                                        contentDescription = "Video",
                                        tint = White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Content preview with selected styling
                    val isCode = selectedHeading == "Code"
                    Box(
                        modifier = if (isCode) {
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F0B1E))
                                .border(1.dp, PurpleContainer, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        } else Modifier
                    ) {
                        Text(
                            text = content.ifEmpty { "Detailed specifications and description will appear here with selected font and color." },
                            fontSize = 13.sp,
                            color = if (isCode) PurpleBright else selectedComposeColor.copy(alpha = 0.9f),
                            fontFamily = selectedComposeFont,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
