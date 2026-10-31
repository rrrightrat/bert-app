package com.example.ui.screens

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
fun AboutScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Schematic Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = NetflixDarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(NetflixCardBorder),
                width = 1.dp
            )
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.banner_redstone_lab_1793446209557),
                    contentDescription = "Advanced Redstone Lab Schematic Bedrock Edition",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BERT // BEDROCK EDITION REDSTONE TOOLKIT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NetflixWhite,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The premier computational suite for Minecraft Bedrock Edition (MCBE) redstone engineering, timing synchronization, and technical automation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NetflixGray
                    )
                }
            }
        }

        SectionCard(title = "Community Inspiration & Attribution") {
            Text(
                text = "Inspired by the original BERT project and YouTube technical research by RR Right Rat (@rightrat). Engineered to handle the unique quirks of the Bedrock redstone engine.",
                style = MaterialTheme.typography.bodySmall,
                color = NetflixOffWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = NetflixDarkSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Original Concept:", style = MaterialTheme.typography.bodySmall, color = NetflixGray)
                        Text("RR Right Rat (BERT)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NetflixRedBright)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Engine Target:", style = MaterialTheme.typography.bodySmall, color = NetflixGray)
                        Text("Minecraft Bedrock Edition (MCBE)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NetflixWhite)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Version Support:", style = MaterialTheme.typography.bodySmall, color = NetflixGray)
                        Text("1.21+ Tricky Trials & Up", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AccentAmber)
                    }
                }
            }
        }

        SectionCard(title = "Core Toolkit Modules") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ModuleItem("Comparator & Container Analyzer", "Interactive fullness math, target signal reverse solver, 15-segment LED gauge, Crafter & Lectern logic.")
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

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ModuleItem(title: String, desc: String) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = NetflixRedBright)
        Text(desc, style = MaterialTheme.typography.bodySmall, color = NetflixGray)
    }
}
