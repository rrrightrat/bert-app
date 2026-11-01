package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityUser
import com.example.ui.CommunityViewModel
import com.example.ui.components.UserAvatar
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: CommunityViewModel,
    onNavigateBack: () -> Unit
) {
    val isAdminAuth by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allPosts by viewModel.allPostsUnfiltered.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var userSearchQuery by remember { mutableStateOf("") }
    var postSearchQuery by remember { mutableStateOf("") }

    // Dialog state
    var userToBan by remember { mutableStateOf<CommunityUser?>(null) }
    var banReasonInput by remember { mutableStateOf("Violation of redstone community standards") }
    var userToDelete by remember { mutableStateOf<CommunityUser?>(null) }
    var postToDelete by remember { mutableStateOf<CommunityPost?>(null) }

    // Passcode gate state
    var enteredPin by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDC2626)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Admin Operations", fontWeight = FontWeight.Bold, color = White, fontSize = 16.sp)
                            Text(
                                text = if (isAdminAuth) "Clearance Level 5 • Master Admin" else "Security Gateway",
                                color = if (isAdminAuth) Color(0xFF4ADE80) else Color(0xFFF87171),
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("admin_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                actions = {
                    if (isAdminAuth) {
                        IconButton(
                            onClick = { viewModel.lockAdmin() },
                            modifier = Modifier.testTag("admin_lock_session_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock Admin Session", tint = Color(0xFFF87171))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBackground)
        ) {
            if (!isAdminAuth) {
                // Security Gateway Screen
                AdminPasscodeGate(
                    enteredPin = enteredPin,
                    onPinChange = { enteredPin = it },
                    userEmail = currentUser?.email.orEmpty(),
                    masterEmail = viewModel.masterAdminEmail,
                    onUnlock = {
                        val success = viewModel.authenticateAdmin(enteredPin)
                        if (success) {
                            enteredPin = ""
                        }
                    }
                )
            } else {
                // Full Admin Dashboard
                Column(modifier = Modifier.fillMaxSize()) {
                    // Quick Stats Bar
                    AdminStatsHeader(
                        totalUsers = allUsers.size,
                        totalPosts = allPosts.size,
                        bannedUsers = allUsers.count { it.isBanned }
                    )

                    // Navigation Tabs
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = DarkSurface,
                        contentColor = White,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = PurpleBright,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Accounts (${allUsers.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("All Posts (${allPosts.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    when (selectedTabIndex) {
                        0 -> AdminUsersTab(
                            users = allUsers,
                            searchQuery = userSearchQuery,
                            onSearchQueryChange = { userSearchQuery = it },
                            onBanClicked = { userToBan = it },
                            onUnbanClicked = { viewModel.adminUnbanUser(it.uid) },
                            onDeleteClicked = { userToDelete = it }
                        )
                        1 -> AdminPostsTab(
                            posts = allPosts,
                            searchQuery = postSearchQuery,
                            onSearchQueryChange = { postSearchQuery = it },
                            onDeletePost = { postToDelete = it }
                        )
                    }
                }
            }
        }
    }

    // Ban User Dialog
    userToBan?.let { user ->
        AlertDialog(
            onDismissRequest = { userToBan = null },
            icon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Suspend Account", fontWeight = FontWeight.Bold, color = White) },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to ban ${user.displayName} (${user.email.ifEmpty { user.uid }})?",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = banReasonInput,
                        onValueChange = { banReasonInput = it },
                        label = { Text("Suspension Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = White,
                            unfocusedTextColor = White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminBanUser(user.uid, banReasonInput)
                        userToBan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Confirm Suspension", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToBan = null }) {
                    Text("Cancel", color = GrayText)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Delete User Dialog
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Permanently Delete User", fontWeight = FontWeight.Bold, color = White) },
            text = {
                Text(
                    text = "This will permanently remove ${user.displayName}'s account and purge all their published redstone contraptions from the cloud.",
                    color = GrayText,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminDeleteUser(user.uid, alsoPurgePosts = true)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Purge Everything", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel", color = GrayText)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Delete Post Dialog
    postToDelete?.let { post ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Delete Creation", fontWeight = FontWeight.Bold, color = White) },
            text = {
                Text(
                    text = "Remove '${post.title}' by ${post.authorName}? All comments on this post will be removed.",
                    color = GrayText,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminDeletePost(post.id)
                        postToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete Post", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("Cancel", color = GrayText)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun AdminPasscodeGate(
    enteredPin: String,
    onPinChange: (String) -> Unit,
    userEmail: String,
    masterEmail: String,
    onUnlock: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isMasterEmail = userEmail.equals(masterEmail, ignoreCase = true)
    val masterHexKey = "0x42455254"
    val masterBinaryKey = "10101101"
    val masterCombinedKey = "0x42455254_10101101"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6B21A8)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cybernetic Security Shield
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2E0854), Color(0xFF1E1035))
                            )
                        )
                        .border(2.dp, PurpleBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security Gateway",
                        tint = PurpleBright,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "HEX & BINARY CLEARANCE",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = White,
                    letterSpacing = 1.1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Master clearance requires composite hexadecimal and binary cryptographic keys.",
                    fontSize = 12.sp,
                    color = GrayText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Hex and Binary Key Blueprint Display
                Surface(
                    color = DarkBackground,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B1D60)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = FilterOrange, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CRYPTOGRAPHIC SPEC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FilterOrange)
                            }
                            Text("ENTROPY: 64-BIT", fontSize = 9.sp, color = MutedText, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Hex Key Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1F1433))
                                    .border(1.dp, PurpleBright.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    Text("HEX (BERT)", fontSize = 9.sp, color = MutedText)
                                    Text(
                                        masterHexKey,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PurpleBright,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Binary Key Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1F1433))
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    Text("BINARY (0xAD)", fontSize = 9.sp, color = MutedText)
                                    Text(
                                        masterBinaryKey,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4ADE80),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Full Master Composite Key Quick-Insert
                        Surface(
                            onClick = { onPinChange(masterCombinedKey) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF28113B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBright),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("MASTER COMPOSITE KEY:", fontSize = 9.sp, color = GrayText)
                                    Text(
                                        masterCombinedKey,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = White,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PurplePrimary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("AUTO-FILL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                if (userEmail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMasterEmail) Color(0xFF0F2E1E) else DarkSurfaceVariant)
                            .border(1.dp, if (isMasterEmail) Color(0xFF22C55E) else DarkCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Operator: $userEmail",
                                fontSize = 11.sp,
                                color = if (isMasterEmail) Color(0xFF4ADE80) else GrayText,
                                fontWeight = FontWeight.Medium
                            )
                            if (isMasterEmail) {
                                Text("BYPASS ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Passcode Text Input with Monospace font and Eye Toggle
                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = onPinChange,
                    label = { Text("Hex & Binary Passcode") },
                    placeholder = { Text("0x42455254_10101101", color = MutedText, fontFamily = FontFamily.Monospace) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null, tint = PurpleBright, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide Passcode" else "Show Passcode",
                                tint = GrayText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleBright,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_passcode_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onUnlock,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_unlock_button")
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify & Unlock Terminal", fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Master Key Format: 0x<HEX>_<BINARY>",
                    fontSize = 10.sp,
                    color = MutedText,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun AdminStatsHeader(
    totalUsers: Int,
    totalPosts: Int,
    bannedUsers: Int
) {
    Surface(
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdminStatPill(label = "Total Accounts", value = "$totalUsers", color = PurpleBright)
            AdminStatPill(label = "Total Creations", value = "$totalPosts", color = Color(0xFF38BDF8))
            AdminStatPill(label = "Banned Users", value = "$bannedUsers", color = if (bannedUsers > 0) Color(0xFFF87171) else Color(0xFF4ADE80))
        }
    }
}

@Composable
private fun AdminStatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = label, fontSize = 10.sp, color = GrayText)
    }
}

@Composable
private fun AdminUsersTab(
    users: List<CommunityUser>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBanClicked: (CommunityUser) -> Unit,
    onUnbanClicked: (CommunityUser) -> Unit,
    onDeleteClicked: (CommunityUser) -> Unit
) {
    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users
        else users.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true) ||
            it.uid.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search users by name, email, or UID...", fontSize = 13.sp, color = MutedText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .testTag("admin_user_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurpleBright,
                unfocusedBorderColor = DarkCardBorder,
                focusedTextColor = White,
                unfocusedTextColor = White
            ),
            shape = RoundedCornerShape(14.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers, key = { it.uid }) { user ->
                AdminUserCard(
                    user = user,
                    onBan = { onBanClicked(user) },
                    onUnban = { onUnbanClicked(user) },
                    onDelete = { onDeleteClicked(user) }
                )
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: CommunityUser,
    onBan: () -> Unit,
    onUnban: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.isBanned) Color(0xFFEF4444) else DarkCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_user_card_${user.uid}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    photoUrl = user.photoUrl,
                    displayName = user.displayName,
                    size = 44.dp,
                    borderColor = if (user.isBanned) Color(0xFFEF4444) else PurpleBright
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (user.isBanned) Color(0xFF3B151E) else Color(0xFF132F20))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (user.isBanned) "BANNED" else "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user.isBanned) Color(0xFFF87171) else Color(0xFF4ADE80)
                            )
                        }
                    }

                    if (user.email.isNotBlank()) {
                        Text(text = user.email, fontSize = 11.sp, color = GrayText)
                    }
                    Text(
                        text = "UID: ${user.uid} • ${user.provider.uppercase()}",
                        fontSize = 10.sp,
                        color = MutedText,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (user.isBanned && user.banReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2A1015))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Reason: ${user.banReason}",
                        fontSize = 11.sp,
                        color = Color(0xFFFCA5A5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkCardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (user.isBanned) {
                    OutlinedButton(
                        onClick = onUnban,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4ADE80)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_unban_btn_${user.uid}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unban", fontSize = 11.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onBan,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_ban_btn_${user.uid}")
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ban Account", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_delete_user_btn_${user.uid}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 11.sp, color = White)
                }
            }
        }
    }
}

@Composable
private fun AdminPostsTab(
    posts: List<CommunityPost>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onDeletePost: (CommunityPost) -> Unit
) {
    val filteredPosts = remember(posts, searchQuery) {
        if (searchQuery.isBlank()) posts
        else posts.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.content.contains(searchQuery, ignoreCase = true) ||
            it.authorName.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search creations by title, author, or keyword...", fontSize = 13.sp, color = MutedText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .testTag("admin_post_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurpleBright,
                unfocusedBorderColor = DarkCardBorder,
                focusedTextColor = White,
                unfocusedTextColor = White
            ),
            shape = RoundedCornerShape(14.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPosts, key = { it.id }) { post ->
                AdminPostCard(
                    post = post,
                    onDelete = { onDeletePost(post) }
                )
            }
        }
    }
}

@Composable
private fun AdminPostCard(
    post: CommunityPost,
    onDelete: () -> Unit
) {
    val formattedDate = remember(post.createdAt) {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(post.createdAt))
    }

    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_post_card_${post.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(photoUrl = post.authorPhotoUrl, displayName = post.authorName, size = 30.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = post.authorName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = White)
                        Text(text = formattedDate, fontSize = 10.sp, color = MutedText)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PurpleContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = post.category, fontSize = 10.sp, color = PurpleBright, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = post.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content, fontSize = 12.sp, color = GrayText, maxLines = 3)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkCardBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Likes: ${post.likesCount} • Comments: ${post.commentsCount}",
                    fontSize = 11.sp,
                    color = MutedText
                )

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_delete_post_btn_${post.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete Post", fontSize = 11.sp, color = White)
                }
            }
        }
    }
}
