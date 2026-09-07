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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LifeEntity
import com.example.services.FormulaEngine
import com.example.services.GameClock
import com.example.ui.DestinyScreen
import com.example.ui.theme.DestinyOutline
import com.example.ui.theme.DestinyPrimary
import com.example.ui.theme.DestinySuccess
import com.example.ui.theme.DestinySurface
import com.example.ui.theme.DestinySurfaceVariant
import com.example.ui.theme.DestinyTextMuted
import com.example.ui.theme.DestinyTextPrimary
import com.example.ui.theme.DestinyTextSecondary

@Composable
fun DestinyLandingScreen(
    lives: List<LifeEntity>,
    clockDisplay: String,
    formulaOutcome: FormulaEngine.Outcome?,
    onNavigate: (DestinyScreen) -> Unit,
    onAdvanceClock: () -> Unit,
    onClockResolutionChange: (GameClock.TickResolution) -> Unit,
    onInspectLife: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Header Badge & Title ---
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(DestinySuccess)
                    )
                    Text(
                        text = "BUILD 01 OF 20 • FOUNDATION READY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "DESTINY",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = DestinyTextPrimary,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Ultra-Deep Life Simulation Engine • Core Architecture",
                    fontSize = 14.sp,
                    color = DestinyTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // --- Primary Actions Banner ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Infrastructure Orchestrator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DestinyTextPrimary
                    )

                    Text(
                        text = "Initialize an end-to-end simulation thread. Verifies atomic persistence across User → SaveSlot → Life → Character records with strict Karma encapsulation.",
                        fontSize = 13.sp,
                        color = DestinyTextSecondary,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(DestinyScreen.NewLifeFlow) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("start_new_life_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DestinyPrimary,
                                contentColor = Color(0xFF021626)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Life",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "New Life Flow",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { onNavigate(DestinyScreen.ArchitectureSpec) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("view_spec_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DestinyTextPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Spec",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "System Spec",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // --- Core Architectural Pillars (Prompt 01 Directives) ---
        item {
            Text(
                text = "CORE ARCHITECTURAL STATUS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DestinyTextMuted,
                letterSpacing = 1.sp
            )
        }

        // Pillar 1: Formula Engine Stub
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Formula Engine",
                                tint = DestinyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Formula Engine (5-Layer Hub)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = DestinyTextPrimary
                            )
                        }
                        Text(
                            text = "ACTIVE STUB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinySuccess
                        )
                    }

                    Text(
                        text = "Shared calculation engine accepting: Foundation, Momentum, Influence, World Variables, and Luck. Prompts 02-20 route all math through this core.",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary,
                        lineHeight = 16.sp
                    )

                    if (formulaOutcome != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Probe Score: ${"%.1f".format(formulaOutcome.compositeScore)}/100",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    color = DestinyTextPrimary
                                )
                                Text(
                                    text = formulaOutcome.tier.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DestinyPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pillar 2: GameClock Timekeeper
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Game Clock",
                                tint = DestinyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GameClock (Variable Resolution)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = DestinyTextPrimary
                            )
                        }
                        Text(
                            text = clockDisplay,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = DestinyPrimary
                        )
                    }

                    Text(
                        text = "Defaults to yearly ticks, architected to accept variable zoom (Monthly / Weekly) for active encounters without assuming yearly-only time.",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary,
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAdvanceClock,
                            modifier = Modifier.testTag("advance_clock_button"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Tick",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Tick Clock", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onClockResolutionChange(GameClock.TickResolution.YEARLY) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Yearly", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onClockResolutionChange(GameClock.TickResolution.MONTHLY) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Monthly Zoom", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Pillar 3: NPC Simulation Model (Dormant vs Active) & Karma Encapsulation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Security",
                                tint = DestinySuccess,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NPC Model & Karma Encapsulation",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = DestinyTextPrimary
                            )
                        }
                        Text(
                            text = "ENFORCED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinySuccess
                        )
                    }

                    Text(
                        text = "• NPC Dual States: 'dormant' (lightweight, attributes saved, no annual simulation) vs 'active' (fully simulated).\n• Karma Mandate: Persisted in database schema, strictly omitted from ClientCharacter / UI models.",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // --- Database Persistence Section: Saved Lives in DB ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DATABASE STATE • PERSISTED LIVES (${lives.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DestinyTextMuted,
                    letterSpacing = 1.sp
                )
            }
        }

        if (lives.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DestinySurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DestinyOutline, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No Lives Created Yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = DestinyTextPrimary
                        )
                        Text(
                            text = "Run the 'New Life Flow' to verify the atomic User → SaveSlot → Life → Character transaction in the database.",
                            fontSize = 13.sp,
                            color = DestinyTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(lives, key = { it.id }) { life ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DestinySurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DestinyOutline, RoundedCornerShape(12.dp))
                        .clickable { onInspectLife(life.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = life.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DestinyTextPrimary
                            )
                            Text(
                                text = "Born ${life.birthYear} • Age ${life.currentAge} • ID: ${life.id.take(12)}...",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = DestinyTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (life.isDormant) DestinySurfaceVariant else DestinySuccess.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (life.isDormant) "DORMANT" else "ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (life.isDormant) DestinyTextMuted else DestinySuccess
                            )
                        }
                    }
                }
            }
        }
    }
}
