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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.repository.LifeRecordSummary
import com.example.services.DeathService
import com.example.ui.components.CharacterAvatar
import com.example.ui.theme.DestinyOutline
import com.example.ui.theme.DestinyPrimary
import com.example.ui.theme.DestinySuccess
import com.example.ui.theme.DestinySurface
import com.example.ui.theme.DestinySurfaceVariant
import com.example.ui.theme.DestinyTextMuted
import com.example.ui.theme.DestinyTextPrimary
import com.example.ui.theme.DestinyTextSecondary

data class SimulationLogItem(
    val id: String,
    val year: Int,
    val age: Int,
    val narrative: String,
    val isFatal: Boolean = false
)

@Composable
fun CharacterProfileScreen(
    summary: LifeRecordSummary,
    deathResult: DeathService.DeathCheckResult?,
    simLogs: List<SimulationLogItem>,
    isAging: Boolean,
    onBack: () -> Unit,
    onAgeOneYear: () -> Unit,
    onOpenRelationships: () -> Unit
) {
    val life = summary.life
    val character = summary.clientCharacter
    val isDead = deathResult?.isDead == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("character_profile_screen"),
        contentPadding = PaddingValues(top = 24.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Top Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DestinyTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DestinySurfaceVariant)
                            .border(1.dp, DestinyOutline, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$${character.bankBalance}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinySuccess,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isDead) Color(0xFFEF4444).copy(alpha = 0.15f)
                                else DestinySuccess.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (isDead) Color(0xFFEF4444).copy(alpha = 0.4f)
                                else DestinySuccess.copy(alpha = 0.4f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isDead) "DECEASED" else "ACTIVE LIFE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDead) Color(0xFFF87171) else DestinySuccess
                        )
                    }
                }
            }
        }

        // --- Character Hero Profile Card ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DestinySurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DestinyOutline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CharacterAvatar(
                            skinTone = character.skinTone,
                            eyeStyle = character.eyeStyle,
                            eyeColor = character.eyeColor,
                            browStyle = character.browStyle,
                            hairStyle = character.hairStyle,
                            hairColor = character.hairColor,
                            facialHairStyle = character.facialHairStyle,
                            facialHairColor = character.facialHairColor,
                            size = 80.dp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = life.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = DestinyTextPrimary
                            )
                            Text(
                                text = "Age ${life.currentAge} • Born ${life.birthYear} in ${character.birthCity}, ${character.birthCountry}",
                                fontSize = 12.sp,
                                color = DestinyTextSecondary
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DestinyPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = character.gender,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DestinyPrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DestinySurfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = character.sexuality,
                                        fontSize = 11.sp,
                                        color = DestinyTextSecondary
                                    )
                                }
                                if (character.talent != "None") {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DestinySuccess.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = character.talent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DestinySuccess
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Age 1 Year Action Button
                    Button(
                        onClick = onAgeOneYear,
                        enabled = !isDead && !isAging,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("age_one_year_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDead) DestinySurfaceVariant else DestinyPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = when {
                                isDead -> "Life Concluded"
                                isAging -> "Simulating Year..."
                                else -> "Age 1 Year (+1 Tick)"
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (isDead) DestinyTextMuted else MaterialTheme.colorScheme.background
                        )
                    }

                    // Relationships & Family Action Button
                    OutlinedButton(
                        onClick = onOpenRelationships,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_relationships_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Relationships & Family Circle",
                            fontWeight = FontWeight.SemiBold,
                            color = DestinyPrimary
                        )
                    }
                }
            }
        }

        // --- Death Notice Card (if character deceased) ---
        if (isDead && deathResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261214)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFF87171).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("death_notice_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Deceased",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "OBITUARY NOTICE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171),
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "${life.name} passed away at the age of ${life.currentAge} (Year ${life.birthYear + life.currentAge}).",
                            fontSize = 13.sp,
                            color = DestinyTextPrimary
                        )
                        Text(
                            text = "Cause of Death: ${deathResult.cause}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            }
        }

        // --- Attributes Section ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VITAL & COGNITIVE ATTRIBUTES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Privacy Shield Active",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DestinyTextSecondary
                    )
                }

                val attributes = listOf(
                    "Health" to (character.health to DestinySuccess),
                    "Energy" to (character.energy to DestinyPrimary),
                    "Athletic Performance" to (character.athleticPerformance to Color(0xFFF59E0B)),
                    "Fertility" to (character.fertility to Color(0xFFEC4899)),
                    "Happiness" to (character.happiness to Color(0xFFEAB308)),
                    "Intelligence" to (character.intelligence to Color(0xFF818CF8)),
                    "Smarts" to (character.smarts to Color(0xFF60A5FA)),
                    "Discipline" to (character.discipline to Color(0xFF2DD4BF)),
                    "Willpower" to (character.willpower to Color(0xFFA78BFA)),
                    "Ambition" to (character.ambition to Color(0xFFC084FC)),
                    "Looks" to (character.looks to Color(0xFFFB7185))
                )

                attributes.forEach { (label, pair) ->
                    val (score, barColor) = pair
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DestinySurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DestinyOutline, RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DestinyTextPrimary
                                )
                                Text(
                                    text = "$score",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = DestinyTextPrimary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { score.coerceIn(0, 100) / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = barColor,
                                trackColor = DestinySurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- Yearly Simulation Log Ledger ---
        item {
            Text(
                text = "SIMULATION EVENT LEDGER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DestinyTextMuted,
                letterSpacing = 1.sp
            )
        }

        if (simLogs.isEmpty()) {
            item {
                Text(
                    text = "No simulation events recorded yet. Press 'Age 1 Year' to begin time simulation.",
                    fontSize = 12.sp,
                    color = DestinyTextSecondary
                )
            }
        } else {
            items(simLogs, key = { it.id }) { log ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (log.isFatal) Color(0xFF261214) else DestinySurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (log.isFatal) Color(0xFFF87171).copy(alpha = 0.5f) else DestinyOutline,
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Year ${log.year} (Age ${log.age})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (log.isFatal) Color(0xFFFCA5A5) else DestinyPrimary
                        )
                        Text(
                            text = log.narrative,
                            fontSize = 12.sp,
                            color = if (log.isFatal) Color(0xFFFECDD3) else DestinyTextSecondary
                        )
                    }
                }
            }
        }
    }
}
