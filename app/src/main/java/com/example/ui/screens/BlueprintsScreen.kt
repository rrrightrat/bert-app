package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlueprintEntity
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GrayText
import com.example.ui.theme.MutedText
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White

@Composable
fun BlueprintsScreen(
    blueprints: List<BlueprintEntity>,
    onToggleFavorite: (id: Long, current: Boolean) -> Unit,
    onDeleteBlueprint: (id: Long) -> Unit,
    onAddBlueprint: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val categories = listOf("ALL", "COMPARATOR", "TIMING", "SORTER", "BALLISTICS", "MECHANICS")

    val filteredList = blueprints.filter { bp ->
        val matchesCategory = selectedCategory == "ALL" || bp.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                bp.title.contains(searchQuery, ignoreCase = true) ||
                bp.description.contains(searchQuery, ignoreCase = true) ||
                bp.configData.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PurplePrimary,
                contentColor = White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("add_blueprint_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Blueprint")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search blueprints or notes...", style = MaterialTheme.typography.bodyMedium, color = MutedText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
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
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("blueprints_search_field")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PurplePrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) PurplePrimary else DarkCardBorder)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PurpleBright else OffWhite,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${filteredList.size} SAVED BLUEPRINTS & PRESETS",
                style = MaterialTheme.typography.labelSmall,
                color = MutedText,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No blueprints match your filter", style = MaterialTheme.typography.titleMedium, color = OffWhite)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Save setups from the calculators or tap + to create custom build notes",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.id }) { bp ->
                        BlueprintCard(
                            blueprint = bp,
                            onToggleFavorite = { onToggleFavorite(bp.id, bp.isFavorite) },
                            onDelete = { onDeleteBlueprint(bp.id) },
                            onCopy = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("BERT Blueprint", "${bp.title}\n${bp.description}\n\n${bp.configData}")
                                cm.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied blueprint specs to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBlueprintDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, cat, desc, config ->
                onAddBlueprint(title, cat, desc, config)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun BlueprintCard(
    blueprint: BlueprintEntity,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            if (blueprint.isFavorite) 1.5.dp else 1.dp,
            if (blueprint.isFavorite) PurpleBright else DarkCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = blueprint.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = blueprint.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleBright,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (blueprint.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (blueprint.isFavorite) PurpleBright else GrayText
                        )
                    }
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GrayText)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MutedText)
                    }
                }
            }

            if (blueprint.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = blueprint.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = blueprint.configData,
                    style = MaterialTheme.typography.bodySmall,
                    color = OffWhite,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun AddBlueprintDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, desc: String, config: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("COMPARATOR") }
    var description by remember { mutableStateOf("") }
    var configData by remember { mutableStateOf("") }

    val categories = listOf("COMPARATOR", "TIMING", "SORTER", "BALLISTICS", "MECHANICS", "LOGIC")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        titleContentColor = White,
        textContentColor = OffWhite,
        title = { Text("NEW CUSTOM BLUEPRINT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = GrayText) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            onClick = { category = cat },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) PurplePrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) PurplePrimary else DarkCardBorder)
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PurpleBright else OffWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Short Description", color = GrayText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = configData,
                    onValueChange = { configData = it },
                    label = { Text("Configuration / Specs", color = GrayText) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, category, description, configData)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GrayText)
            }
        }
    )
}
