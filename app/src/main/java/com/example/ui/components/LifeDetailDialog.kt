package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.LifeRecordSummary
import com.example.ui.theme.DestinyOutline
import com.example.ui.theme.DestinyPrimary
import com.example.ui.theme.DestinySuccess
import com.example.ui.theme.DestinySurface
import com.example.ui.theme.DestinySurfaceVariant
import com.example.ui.theme.DestinyTextMuted
import com.example.ui.theme.DestinyTextPrimary
import com.example.ui.theme.DestinyTextSecondary

@Composable
fun LifeDetailDialog(
    summary: LifeRecordSummary,
    onDismiss: () -> Unit,
    onToggleDormancy: (lifeId: String, currentIsDormant: Boolean) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = summary.life.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinyTextPrimary
                        )
                        Text(
                            text = "Save Slot: ${summary.saveSlot.slotName} • Account: ${summary.user.username}",
                            fontSize = 12.sp,
                            color = DestinyTextSecondary
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_inspect_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DestinyTextSecondary)
                    }
                }

                // Dormant State Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DestinySurfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Simulation State",
                            fontSize = 12.sp,
                            color = DestinyTextMuted
                        )
                        Text(
                            text = if (summary.life.isDormant) "Dormant (Paused)" else "Active (Simulated)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (summary.life.isDormant) DestinyTextMuted else DestinySuccess
                        )
                    }

                    Button(
                        onClick = { onToggleDormancy(summary.life.id, summary.life.isDormant) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (summary.life.isDormant) "Activate" else "Make Dormant", fontSize = 12.sp)
                    }
                }

                // Attributes List
                Text(
                    text = "CLIENT ATTRIBUTES (RAW KARMA OMITTED)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DestinyTextMuted,
                    letterSpacing = 1.sp
                )

                val c = summary.clientCharacter
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatRow("Intelligence", c.intelligence)
                    StatRow("Discipline", c.discipline)
                    StatRow("Willpower", c.willpower)
                    StatRow("Ambition", c.ambition)
                    StatRow("Health", c.health)
                    StatRow("Looks", c.looks)
                    StatRow("Smarts", c.smarts)
                    StatRow("Happiness", c.happiness)
                }

                // Karma Notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF161F2E))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = DestinyPrimary, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Karma hidden from client API response per architectural rule.",
                        fontSize = 11.sp,
                        color = DestinyTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(name: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, fontSize = 12.sp, color = DestinyTextSecondary)
        Text(text = value.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DestinyTextPrimary, fontFamily = FontFamily.Monospace)
    }
}
