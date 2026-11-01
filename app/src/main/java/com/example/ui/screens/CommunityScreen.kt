package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityUser
import com.example.data.model.PostSortOption
import com.example.ui.CommunityViewModel
import com.example.ui.components.AuthModal
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.UserAvatar
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val COMMUNITY_CATEGORIES = listOf(
    "All", "Contraptions", "Clocks", "Logic", "Cannons", "Piston Doors", "Item Sorters", "Farms", "Quirks", "Miscellaneous"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToUserProfile: () -> Unit
) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val activeCommentsPost by viewModel.activePostForComments.collectAsStateWithLifecycle()
    val comments by viewModel.currentPostComments.collectAsStateWithLifecycle()

    var showAuthModal by remember { mutableStateOf(false) }
    val authSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentUser != null) {
                        viewModel.setPostToEdit(null)
                        onNavigateToCreatePost()
                    } else {
                        showAuthModal = true
                    }
                },
                containerColor = PurplePrimary,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("create_post_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Redstone Post")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header: Profile chip & Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Redstone Community",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Share Bedrock circuits, contraptions & logic",
                        fontSize = 12.sp,
                        color = GrayText
                    )
                }

                if (currentUser != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, PurplePrimary, RoundedCornerShape(20.dp))
                            .clickable { onNavigateToUserProfile() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("user_profile_chip"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            photoUrl = currentUser!!.photoUrl,
                            displayName = currentUser!!.displayName,
                            size = 22.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentUser!!.isBanned) "${currentUser!!.displayName.take(8)} (SUSPENDED)" else currentUser!!.displayName.take(12),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentUser!!.isBanned) Color(0xFFF87171) else White
                        )
                    }
                } else {
                    Button(
                        onClick = { showAuthModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("community_sign_in_button")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search creations, logic gates, clocks...", fontSize = 13.sp, color = MutedText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GrayText)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleBright,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = White,
                    unfocusedTextColor = White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("community_search_bar"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
            ) {
                items(COMMUNITY_CATEGORIES) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(category) },
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

            // Sort & Results Count Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${posts.size} creations found",
                    fontSize = 11.sp,
                    color = GrayText
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sort, contentDescription = null, tint = GrayText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = sortOption.label,
                        fontSize = 11.sp,
                        color = PurpleBright,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                val nextSort = when (sortOption) {
                                    PostSortOption.NEWEST -> PostSortOption.MOST_LIKED
                                    PostSortOption.MOST_LIKED -> PostSortOption.MOST_DISCUSSED
                                    PostSortOption.MOST_DISCUSSED -> PostSortOption.NEWEST
                                }
                                viewModel.setSortOption(nextSort)
                            }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            // Posts Feed
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Redstone Creations Found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try a different search term or category" else "Be the first engineer to publish a redstone creation to the cloud!",
                            fontSize = 13.sp,
                            color = GrayText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = {
                                if (currentUser != null) {
                                    viewModel.setPostToEdit(null)
                                    onNavigateToCreatePost()
                                } else {
                                    showAuthModal = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Creation", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        CommunityPostCard(
                            post = post,
                            currentUser = currentUser,
                            onLike = { viewModel.toggleLike(post) },
                            onDislike = { viewModel.toggleDislike(post) },
                            onCommentsClick = { viewModel.openComments(post) },
                            onRequestAuth = { showAuthModal = true }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }
    }

    // Auth Modal
    if (showAuthModal) {
        AuthModal(
            sheetState = authSheetState,
            authManager = viewModel.authManager,
            onDismiss = { showAuthModal = false },
            onSuccess = { showAuthModal = false }
        )
    }

    // Comments Sheet
    if (activeCommentsPost != null) {
        CommentsBottomSheet(
            sheetState = commentsSheetState,
            post = activeCommentsPost!!,
            comments = comments,
            currentUser = currentUser,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { viewModel.addComment(it) },
            onDeleteComment = { viewModel.deleteComment(it) },
            onRequestAuth = { showAuthModal = true }
        )
    }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPost,
    currentUser: CommunityUser?,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onCommentsClick: () -> Unit,
    onRequestAuth: () -> Unit
) {
    val isLiked = currentUser != null && post.likedUserIds.contains(currentUser.uid)
    val isDisliked = currentUser != null && post.dislikedUserIds.contains(currentUser.uid)

    val postFont = when (post.fontFamily) {
        "Monospace" -> FontFamily.Monospace
        "Serif" -> FontFamily.Serif
        else -> FontFamily.Default
    }

    val postTextColor = runCatching {
        Color(android.graphics.Color.parseColor(post.textColorHex))
    }.getOrDefault(Color.White)

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val formattedDate = remember(post.createdAt) { dateFormat.format(Date(post.createdAt)) }

    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row & Category badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(
                        photoUrl = post.authorPhotoUrl,
                        displayName = post.authorName,
                        size = 34.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = White
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 10.sp,
                            color = MutedText
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PurpleContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = post.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title
            val titleSize = when (post.headingStyle) {
                "H1" -> 19.sp
                "H2" -> 17.sp
                "Code" -> 15.sp
                else -> 16.sp
            }
            Text(
                text = post.title,
                fontSize = titleSize,
                fontWeight = if (post.headingStyle == "Code") FontWeight.Medium else FontWeight.Bold,
                color = postTextColor,
                fontFamily = postFont
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Media Preview (if attached)
            if (post.mediaUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = post.mediaUrl,
                        contentDescription = "Post media",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (post.mediaType == "video") {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayCircle,
                                contentDescription = "Play video",
                                tint = White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Post Body Content
            val isCode = post.headingStyle == "Code"
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
                    text = post.content,
                    fontSize = 13.sp,
                    color = if (isCode) PurpleBright else postTextColor.copy(alpha = 0.9f),
                    fontFamily = postFont,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Bar: Like, Dislike, Comments
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkBackground)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / Dislike group
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (currentUser != null) onLike() else onRequestAuth()
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color(0xFFEF4444) else GrayText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.likesCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLiked) Color(0xFFEF4444) else GrayText
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dislike
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (currentUser != null) onDislike() else onRequestAuth()
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                            contentDescription = "Dislike",
                            tint = if (isDisliked) PurpleBright else GrayText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.dislikesCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDisliked) PurpleBright else GrayText
                        )
                    }
                }

                // Comments button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCommentsClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = PurpleBright,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentsCount} Comments",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    }
}
