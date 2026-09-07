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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.LifeRecordSummary
import com.example.ui.NewLifeUiState
import com.example.ui.theme.DestinyOutline
import com.example.ui.theme.DestinyPrimary
import com.example.ui.theme.DestinySuccess
import com.example.ui.theme.DestinySurface
import com.example.ui.theme.DestinySurfaceVariant
import com.example.ui.theme.DestinyTextMuted
import com.example.ui.theme.DestinyTextPrimary
import com.example.ui.theme.DestinyTextSecondary

@Composable
fun NewLifeFlowScreen(
    state: NewLifeUiState,
    onBack: () -> Unit,
    onPlayerNameChange: (String) -> Unit,
    onSlotNameChange: (String) -> Unit,
    onCharacterNameChange: (String) -> Unit,
    onBirthYearChange: (Int) -> Unit,
    onIsDormantChange: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // --- Top Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_button")
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
                        text = "NEW LIFE FLOW",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Atomic User → SaveSlot → Life → Character Transaction",
                        fontSize = 12.sp,
                        color = DestinyTextSecondary
                    )
                }
            }
        }

        // --- Instructions Card ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Transaction",
                        tint = DestinyPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Relational Persistence Verification",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DestinyTextPrimary
                        )
                        Text(
                            text = "This flow executes the complete multi-tier data structure required by Prompt 01. It creates an independent Life attached to an unlimited SaveSlot, with core stats written to disk.",
                            fontSize = 12.sp,
                            color = DestinyTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // --- Entity Inputs ---
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "RELATIONAL PARAMETERS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextMuted,
                        letterSpacing = 1.sp
                    )

                    // 1. User Entity
                    OutlinedTextField(
                        value = state.playerName,
                        onValueChange = onPlayerNameChange,
                        label = { Text("1. Player Account (User Entity)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_name_input"),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = DestinyPrimary)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DestinyPrimary,
                            unfocusedBorderColor = DestinyOutline
                        )
                    )

                    // 2. SaveSlot Entity
                    OutlinedTextField(
                        value = state.saveSlotName,
                        onValueChange = onSlotNameChange,
                        label = { Text("2. Save Slot Identifier (SaveSlot Entity)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_slot_name_input"),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Save, contentDescription = null, tint = DestinyPrimary)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DestinyPrimary,
                            unfocusedBorderColor = DestinyOutline
                        )
                    )

                    // 3. Life Entity
                    OutlinedTextField(
                        value = state.characterName,
                        onValueChange = onCharacterNameChange,
                        label = { Text("3. Subject Name (Life Entity)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("character_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DestinyPrimary,
                            unfocusedBorderColor = DestinyOutline
                        )
                    )

                    // 4. Birth Year
                    OutlinedTextField(
                        value = state.birthYear.toString(),
                        onValueChange = { str ->
                            str.toIntOrNull()?.let { onBirthYearChange(it) }
                        },
                        label = { Text("Birth Year") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("birth_year_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DestinyPrimary,
                            unfocusedBorderColor = DestinyOutline
                        )
                    )

                    // 5. NPC Simulation State (Dormant vs Active)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "NPC Simulation State:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DestinyTextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = !state.isDormant,
                                onClick = { onIsDormantChange(false) },
                                label = { Text("Active Simulation") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DestinySuccess.copy(alpha = 0.2f),
                                    selectedLabelColor = DestinySuccess
                                )
                            )

                            FilterChip(
                                selected = state.isDormant,
                                onClick = { onIsDormantChange(true) },
                                label = { Text("Dormant (Lightweight)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Commit Button
                    Button(
                        onClick = onSubmit,
                        enabled = !state.isSubmitting && state.playerName.isNotBlank() && state.characterName.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("commit_life_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DestinyPrimary,
                            contentColor = Color(0xFF021626)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF021626),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Writing to Database...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Commit New Life Record", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // --- Confirmation & Verification Display ---
        if (state.lastCreatedRecord != null) {
            item {
                TransactionReceiptCard(summary = state.lastCreatedRecord)
            }
        }
    }
}

@Composable
fun TransactionReceiptCard(summary: LifeRecordSummary) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DestinySurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DestinySuccess.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = DestinySuccess,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DATABASE WRITE CONFIRMED",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = DestinySuccess
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DestinySuccess.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "ROOM SQLITE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinySuccess,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Entity UUIDs & Foreign Keys Proof
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DetailRow("User PK", summary.user.id)
                DetailRow("SaveSlot PK", summary.saveSlot.id)
                DetailRow("Life PK", summary.life.id)
                DetailRow("Character PK", summary.clientCharacter.id)
                DetailRow("Name / Born", "${summary.life.name} (Year ${summary.life.birthYear})")
                DetailRow(
                    "State",
                    if (summary.life.isDormant) "DORMANT (Lightweight)" else "ACTIVE (Simulated)"
                )
            }

            // Character Stats Preview
            Text(
                text = "CHARACTER CORE ATTRIBUTES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DestinyTextMuted,
                letterSpacing = 1.sp
            )

            val c = summary.clientCharacter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DestinySurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Intelligence: ${c.intelligence}", fontSize = 12.sp, color = DestinyTextPrimary)
                    Text(text = "Discipline: ${c.discipline}", fontSize = 12.sp, color = DestinyTextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Willpower: ${c.willpower}", fontSize = 12.sp, color = DestinyTextPrimary)
                    Text(text = "Ambition: ${c.ambition}", fontSize = 12.sp, color = DestinyTextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Health: ${c.health}", fontSize = 12.sp, color = DestinyTextPrimary)
                    Text(text = "Looks: ${c.looks}", fontSize = 12.sp, color = DestinyTextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Smarts: ${c.smarts}", fontSize = 12.sp, color = DestinyTextPrimary)
                    Text(text = "Happiness: ${c.happiness}", fontSize = 12.sp, color = DestinyTextPrimary)
                }
            }

            // Karma privacy badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF161F2E))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = DestinyPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Karma Encapsulation: Stored in DB table 'characters', excluded from ClientCharacter response.",
                    fontSize = 11.sp,
                    color = DestinyTextSecondary
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = DestinyTextMuted,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = DestinyTextPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}
