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

import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun DestinyLandingScreen(
    lives: List<LifeEntity>,
    clockDisplay: String,
    formulaOutcome: FormulaEngine.Outcome?,
    backupStatusMessage: String? = null,
    exportedJson: String? = null,
    isBackupLoading: Boolean = false,
    onNavigate: (DestinyScreen) -> Unit,
    onAdvanceClock: () -> Unit,
    onClockResolutionChange: (GameClock.TickResolution) -> Unit,
    onInspectLife: (String) -> Unit,
    onOpenProfile: (String) -> Unit = {},
    onExportSave: () -> Unit = {},
    onImportSave: (String) -> Unit = {},
    onClearBackupMessage: () -> Unit = {},
    onClearExportedJson: () -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    val showImportDialog = remember { mutableStateOf(false) }
    val importInputText = remember { mutableStateOf("") }
    val copiedNotice = remember { mutableStateOf(false) }

    // Export Dialog
    if (exportedJson != null) {
        AlertDialog(
            onDismissRequest = {
                copiedNotice.value = false
                onClearExportedJson()
            },
            title = {
                Text(
                    text = "Export Save Data (.json)",
                    fontWeight = FontWeight.Bold,
                    color = DestinyTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Complete portable backup of your local Room SQLite database (Users, SaveSlots, Lives, Characters, Relationships, and FamilyEvents).",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(DestinySurfaceVariant, RoundedCornerShape(8.dp))
                            .border(1.dp, DestinyOutline, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = exportedJson,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DestinyTextPrimary
                        )
                    }
                    if (copiedNotice.value) {
                        Text(
                            text = "✓ Copied to clipboard!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinySuccess
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportedJson))
                        copiedNotice.value = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary, contentColor = Color(0xFF021626))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy JSON")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        copiedNotice.value = false
                        onClearExportedJson()
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog.value) {
        AlertDialog(
            onDismissRequest = { showImportDialog.value = false },
            title = {
                Text(
                    text = "Import Save Data (.json)",
                    fontWeight = FontWeight.Bold,
                    color = DestinyTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Paste a valid Destiny JSON backup below. This will restore all records directly into your local Room database.",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary
                    )
                    OutlinedTextField(
                        value = importInputText.value,
                        onValueChange = { importInputText.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        placeholder = { Text("Paste JSON here...", fontSize = 12.sp) },
                        maxLines = 10
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importInputText.value.isNotBlank()) {
                            onImportSave(importInputText.value.trim())
                            showImportDialog.value = false
                            importInputText.value = ""
                        }
                    },
                    enabled = importInputText.value.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary, contentColor = Color(0xFF021626))
                ) {
                    Text("Restore Database")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showImportDialog.value = false
                        importInputText.value = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
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

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(DestinyScreen.CharacterCreation) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_character_creation_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DestinyPrimary,
                                contentColor = Color(0xFF021626)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Character",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Character Creator (7 Steps)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onNavigate(DestinyScreen.NewLifeFlow) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("start_new_life_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = DestinyTextPrimary
                                )
                            ) {
                                Text(
                                    text = "Quick Flow",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { onNavigate(DestinyScreen.ArchitectureSpec) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("view_spec_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = DestinyTextPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Spec",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "System Spec",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Local Storage & Backup (Zero Backend Architecture) ---
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Local Storage & Data Backup",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DestinyTextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DestinySuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Zero Backend • Local Room",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DestinySuccess
                            )
                        }
                    }

                    Text(
                        text = "All your life simulation data is owned exclusively by your device. Export a complete JSON snapshot anytime for manual cloud backup or cross-device transfer.",
                        fontSize = 13.sp,
                        color = DestinyTextSecondary,
                        lineHeight = 18.sp
                    )

                    backupStatusMessage?.let { msg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DestinySurfaceVariant)
                                .border(1.dp, DestinyPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = DestinyTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = DestinyTextMuted,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onClearBackupMessage() }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onExportSave() },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("export_save_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DestinyPrimary,
                                contentColor = Color(0xFF021626)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isBackupLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export Save",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBackupLoading) "Exporting..." else "Export Save",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showImportDialog.value = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("import_save_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DestinyTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isBackupLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = "Import Save",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Import Save",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
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
