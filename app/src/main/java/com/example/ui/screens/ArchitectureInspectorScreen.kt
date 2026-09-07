package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DestinyOutline
import com.example.ui.theme.DestinyPrimary
import com.example.ui.theme.DestinySuccess
import com.example.ui.theme.DestinySurface
import com.example.ui.theme.DestinySurfaceVariant
import com.example.ui.theme.DestinyTextMuted
import com.example.ui.theme.DestinyTextPrimary
import com.example.ui.theme.DestinyTextSecondary

@Composable
fun ArchitectureInspectorScreen(
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Top Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("spec_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DestinyTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SYSTEM ARCHITECTURE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Destiny Foundation Blueprint • Prompts 01 of 20",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary
                    )
                }
            }
        }

        // --- Relational Data Architecture ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountTree, contentDescription = null, tint = DestinyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Relational Save Hierarchy",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DestinyTextPrimary
                        )
                    }

                    Text(
                        text = "Designed for infinite playthroughs and branching family lineages without single-save limitations.",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DestinySurfaceVariant)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SchemaNode(level = 0, name = "User Entity", desc = "Real-world player identity [id, username, createdAt]")
                        SchemaNode(level = 1, name = "SaveSlot Entity", desc = "1-to-Many slot container [id, userId, slotName, timestamps]")
                        SchemaNode(level = 2, name = "Life Entity", desc = "Playthrough thread [id, saveSlotId, name, birthYear, currentAge, isDormant]")
                        SchemaNode(level = 3, name = "Character Entity", desc = "Core attributes [intelligence, discipline, willpower, ambition, health, looks, smarts, happiness, karma]")
                    }
                }
            }
        }

        // --- NPC Simulation Model (Dormant vs Active) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DestinyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NPC Simulation Model (Dormant vs Active)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DestinyTextPrimary
                        )
                    }

                    Text(
                        text = "To scale to tens of thousands of simulated world citizens without memory bloat or CPU choke:",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "• DORMANT NPC: Full stat sheet is persisted in the database, but annual tick simulation loops bypass this entity. Relationships, career advancements, and micro-events remain paused until awakened.",
                            fontSize = 12.sp,
                            color = DestinyTextSecondary,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "• ACTIVE NPC: Entity enters the player's immediate sphere (family, rival, boss, spouse). Ticks execute full formula resolutions and event cascades.",
                            fontSize = 12.sp,
                            color = DestinyTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // --- Formula Engine Specifications ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "5-Layer Formula Engine Specs",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DestinyTextPrimary
                    )

                    Text(
                        text = "Every future system (Career, Family, Crime, Politics, Military, etc.) calls this central engine:",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DestinySurfaceVariant)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FormulaLayerRow("1. Character Foundation", "35% default", "Intelligence, Discipline, Willpower, Ambition, Health, Looks, Smarts, Happiness")
                        FormulaLayerRow("2. Momentum", "20% default", "Trajectory score (-1.0 to 1.0), streak continuity, fatigue")
                        FormulaLayerRow("3. Influence", "15% default", "Social capital, family wealth tier, faction leverage")
                        FormulaLayerRow("4. World Variables", "15% default", "Macroeconomic index, political stability, unrest rating")
                        FormulaLayerRow("5. Luck", "15% default", "Stochastic roll modified by internal Karma")
                    }
                }
            }
        }

        // --- Karma Privacy Guarantee ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinySuccess.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Karma Privacy",
                        tint = DestinySuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Karma Privacy Mandate Enforced",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DestinySuccess
                        )
                        Text(
                            text = "Karma is stored in Room database 'characters' table, but is strictly filtered out at the Data Repository boundary into ClientCharacter. No API response or UI Composable ever sees the raw Karma value.",
                            fontSize = 12.sp,
                            color = DestinyTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // --- Roadmap ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NEXT STEPS (PROMPTS 02-20)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextMuted,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "• Prompt 02: Character System depth (traits, childhood, genetics)\n• Prompt 03: Family & Relationships\n• Prompt 04: Education & Academics\n• Prompt 05-20: Career, Business, Economy, Politics, Military, Crime, Sports, Fame, Healthcare, and World Simulation.\n\nAll subsequent prompts will dock into this foundation without refactoring.",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SchemaNode(level: Int, name: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 12).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "└─ ", color = DestinyPrimary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        Column {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DestinyTextPrimary)
            Text(text = desc, fontSize = 10.sp, color = DestinyTextMuted, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun FormulaLayerRow(name: String, weight: String, details: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = DestinyTextPrimary)
            Text(text = weight, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DestinyPrimary)
        }
        Text(text = details, fontSize = 11.sp, color = DestinyTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
    }
}
