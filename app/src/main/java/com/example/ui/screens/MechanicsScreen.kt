package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BedrockKnowledgeBase
import com.example.data.model.MechanicGuide
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.NetflixCardBorder
import com.example.ui.theme.NetflixDarkSurface
import com.example.ui.theme.NetflixDarkSurfaceVariant
import com.example.ui.theme.NetflixGray
import com.example.ui.theme.NetflixMuted
import com.example.ui.theme.NetflixOffWhite
import com.example.ui.theme.NetflixRed
import com.example.ui.theme.NetflixRedBright
import com.example.ui.theme.NetflixWhite

@Composable
fun MechanicsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "ENGINE", "PISTONS", "STORAGE", "COMBAT_FARMS", "SENSORS")

    val filteredList = BedrockKnowledgeBase.MECHANICS.filter { guide ->
        val matchesCategory = selectedCategory == "ALL" || guide.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                guide.title.contains(searchQuery, ignoreCase = true) ||
                guide.summary.contains(searchQuery, ignoreCase = true) ||
                guide.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Bedrock mechanics, quirks, or tags...", style = MaterialTheme.typography.bodySmall, color = NetflixMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NetflixGray) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = NetflixGray)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NetflixRed,
                unfocusedBorderColor = NetflixCardBorder,
                focusedContainerColor = NetflixDarkSurface,
                unfocusedContainerColor = NetflixDarkSurface,
                cursorColor = NetflixRed,
                focusedTextColor = NetflixWhite,
                unfocusedTextColor = NetflixWhite
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mechanics_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                Surface(
                    onClick = { selectedCategory = cat },
                    shape = RoundedCornerShape(4.dp),
                    color = if (selectedCategory == cat) NetflixRed.copy(alpha = 0.2f) else NetflixDarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCategory == cat) NetflixRed else NetflixCardBorder)
                ) {
                    Text(
                        text = cat.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedCategory == cat) NetflixRedBright else NetflixOffWhite,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${filteredList.size} MCBE MECHANICAL SPECIFICATIONS",
            style = MaterialTheme.typography.labelSmall,
            color = NetflixMuted,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList, key = { it.id }) { guide ->
                MechanicCard(guide = guide)
            }
        }
    }
}

@Composable
private fun MechanicCard(guide: MechanicGuide) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = NetflixDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(NetflixCardBorder),
            width = 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guide.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NetflixWhite,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = NetflixDarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NetflixRed.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = guide.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = NetflixRedBright,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = guide.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = NetflixGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = NetflixDarkSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("JAVA EDITION DIFFERENCE:", style = MaterialTheme.typography.labelSmall, color = AccentAmber)
                    Text(guide.javaDifference, style = MaterialTheme.typography.bodySmall, color = NetflixOffWhite)

                    Spacer(modifier = Modifier.height(2.dp))
                    Text("BEDROCK SOLUTION & BEST PRACTICE:", style = MaterialTheme.typography.labelSmall, color = NetflixRedBright)
                    Text(guide.practicalSolution, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = NetflixWhite)
                }
            }
        }
    }
}
