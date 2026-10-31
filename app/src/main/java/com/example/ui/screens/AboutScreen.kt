package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BedrockTipCard
import com.example.ui.components.SectionCard
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GrayText
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PurpleBright
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.White

@Composable
fun AboutScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Schematic Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.banner_redstone_lab_1793446209557),
                    contentDescription = "Advanced Redstone Lab Schematic Bedrock Edition",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "BERT // BEDROCK EDITION REDSTONE TOOLKIT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The premier computational suite for Minecraft Bedrock Edition (MCBE) redstone engineering, timing synchronization, and technical automation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayText
                    )
                }
            }
        }

        SectionCard(title = "Community Inspiration & Attribution") {
            Text(
                text = "Inspired by the original BERT project and YouTube technical research by RR Right Rat (@rrrightrat). Engineered specifically to handle the unique quirks of the Bedrock redstone engine.",
                style = MaterialTheme.typography.bodyMedium,
                color = OffWhite
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Original Concept:", style = MaterialTheme.typography.bodySmall, color = GrayText)
                        Text("RR Right Rat (BERT)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PurpleBright)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Engine Target:", style = MaterialTheme.typography.bodySmall, color = GrayText)
                        Text("Minecraft Bedrock Edition (MCBE)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = White)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Version Support:", style = MaterialTheme.typography.bodySmall, color = GrayText)
                        Text("1.21+ Tricky Trials & Up", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AccentAmber)
                    }
                }
            }
        }

        SectionCard(title = "Core Toolkit Modules") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleItem("Comparator & Container Analyzer", "Interactive fullness math, target signal reverse solver, 15-segment purple LED gauge, Crafter & Lectern logic.")
                ModuleItem("Timings & Etho Clocks", "Item duration solver, optimal repeater delay chains, comparator pulse faders, torch burnout safety.")
                ModuleItem("Item Sorter Engineering", "ImpulseSV 41-item filter safety limits, overflow bleed thresholds, hopper throughput rates.")
                ModuleItem("Ballistics & TNT Physics", "TNT cannon propellant vectors, payload fuse synchronization, 4-piston Trident Killer DPS calculations.")
                ModuleItem("Computational Redstone", "8-bit multi-base converter (Dec/Hex/Bin/Analog), Bedrock logic gates & flip-flops.")
                ModuleItem("Bedrock Mechanics Encyclopedia", "Searchable guide covering random update order, no quasi-connectivity, movable tile entities, and trident looting.")
                ModuleItem("Local Blueprints & Presets", "Save, favorite, copy, and export contraption specifications locally.")
            }
        }

        BedrockTipCard(
            title = "Engine Rule #1 for Bedrock Builders",
            description = "Never assume simultaneous piston firing is deterministic! When building double piston extenders or sequential doors, always place a 1-tick repeater between stages to guarantee correct execution order."
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ModuleItem(title: String, desc: String) {
    Column {
        Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PurpleBright)
        Spacer(modifier = Modifier.height(2.dp))
        Text(desc, style = MaterialTheme.typography.bodySmall, color = GrayText)
    }
}
