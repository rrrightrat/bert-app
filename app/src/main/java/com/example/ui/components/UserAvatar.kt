package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurplePrimary

/**
 * Reusable UserAvatar component that cleanly renders:
 * - Real HTTP / HTTPS profile image URLs (e.g. from Google Sign-In) via Coil AsyncImage
 * - Emoji avatars (e.g. ⚡, 🍎, 🤖, 💎) in a rounded container
 * - User initials or default icon fallback
 *
 * Never renders raw URL links inside text fields.
 */
@Composable
fun UserAvatar(
    photoUrl: String,
    displayName: String = "",
    size: Dp = 36.dp,
    borderColor: Color? = null,
    borderWidth: Dp = 1.5.dp,
    modifier: Modifier = Modifier
) {
    val cleanUrl = photoUrl.trim()
    val isHttpUrl = cleanUrl.startsWith("http://", ignoreCase = true) ||
            cleanUrl.startsWith("https://", ignoreCase = true) ||
            cleanUrl.startsWith("content://", ignoreCase = true)

    val borderModifier = if (borderColor != null) {
        Modifier.border(borderWidth, borderColor, CircleShape)
    } else {
        Modifier
    }

    if (isHttpUrl) {
        AsyncImage(
            model = cleanUrl,
            contentDescription = displayName.ifEmpty { "User Avatar" },
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .then(borderModifier)
                .testTag("user_avatar_image")
        )
    } else if (cleanUrl.isNotBlank() && cleanUrl.length <= 4) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(PurpleContainer)
                .then(borderModifier)
                .testTag("user_avatar_emoji"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cleanUrl,
                fontSize = (size.value * 0.48f).sp
            )
        }
    } else {
        val initial = displayName.trim().take(1).uppercase()
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(PurplePrimary)
                .then(borderModifier)
                .testTag("user_avatar_fallback"),
            contentAlignment = Alignment.Center
        ) {
            if (initial.isNotBlank()) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = (size.value * 0.46f).sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(size * 0.55f)
                )
            }
        }
    }
}
