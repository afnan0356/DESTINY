package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CharacterData
import com.example.ui.components.CharacterAvatar
import com.example.ui.theme.DestinyOutline
import com.example.ui.theme.DestinyPrimary
import com.example.ui.theme.DestinySuccess
import com.example.ui.theme.DestinySurface
import com.example.ui.theme.DestinySurfaceVariant
import com.example.ui.theme.DestinyTextMuted
import com.example.ui.theme.DestinyTextPrimary
import com.example.ui.theme.DestinyTextSecondary

data class CharacterCreationParams(
    val country: String = "United States",
    val city: String = "New York",
    val firstName: String = "Julian",
    val lastName: String = "Vance",
    val gender: String = "Male",
    val sexuality: String = "Heterosexual",
    val talent: String = "None",
    val skinTone: String = "Fair",
    val eyeStyle: String = "Almond",
    val eyeColor: String = "Brown",
    val browStyle: String = "Straight",
    val facialHairStyle: String = "Clean Shaven",
    val facialHairColor: String = "Black",
    val hairStyle: String = "Short Crop",
    val hairColor: String = "Black",
    val intelligence: Int = 75,
    val discipline: Int = 68,
    val willpower: Int = 70,
    val ambition: Int = 78,
    val health: Int = 90,
    val looks: Int = 72,
    val smarts: Int = 75,
    val happiness: Int = 80,
    val fertility: Int = 85,
    val energy: Int = 95,
    val athleticPerformance: Int = 70
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterCreationScreen(
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmit: (CharacterCreationParams) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var params by remember { mutableStateOf(CharacterCreationParams()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val availableCities = CharacterData.COUNTRIES_AND_CITIES[params.country] ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("character_creation_screen"),
        contentPadding = PaddingValues(top = 24.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Top Navigation & Progress Header ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (step > 1) step-- else onBack()
                        },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DestinyTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DestinyPrimary.copy(alpha = 0.15f))
                            .border(1.dp, DestinyPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "STEP $step OF 7",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinyPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { step / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = DestinyPrimary,
                    trackColor = DestinySurfaceVariant
                )
            }
        }

        // --- STEP 1: Country Selection ---
        if (step == 1) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Select Birth Country",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary
                    )
                    Text(
                        text = "Geopolitical baselines influence starting currency, opportunities, and societal stability.",
                        fontSize = 13.sp,
                        color = DestinyTextSecondary
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CharacterData.COUNTRIES.forEach { c ->
                            val isSelected = params.country == c
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DestinyPrimary.copy(alpha = 0.15f) else DestinySurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) DestinyPrimary else DestinyOutline,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        val newCities = CharacterData.COUNTRIES_AND_CITIES[c] ?: emptyList()
                                        val newCity = newCities.firstOrNull() ?: "Metro"
                                        params = params.copy(country = c, city = newCity)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .testTag("country_option_$c")
                            ) {
                                Text(
                                    text = c,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- STEP 2: City Selection ---
        if (step == 2) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Select Birth City",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary
                    )
                    Text(
                        text = "Cities within ${params.country}. Sets the municipal environment and local crime/economic indices.",
                        fontSize = 13.sp,
                        color = DestinyTextSecondary
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableCities.forEach { cty ->
                            val isSelected = params.city == cty
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DestinyPrimary.copy(alpha = 0.15f) else DestinySurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) DestinyPrimary else DestinyOutline,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        params = params.copy(city = cty)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .testTag("city_option_$cty")
                            ) {
                                Text(
                                    text = cty,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- STEP 3: Identity & Name ---
        if (step == 3) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Legal Identity & Name",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary
                    )
                    Text(
                        text = "The designated identity under which records will be registered across world systems.",
                        fontSize = 13.sp,
                        color = DestinyTextSecondary
                    )

                    OutlinedTextField(
                        value = params.firstName,
                        onValueChange = { params = params.copy(firstName = it) },
                        label = { Text("First Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("first_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DestinyPrimary,
                            unfocusedBorderColor = DestinyOutline
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = params.lastName,
                        onValueChange = { params = params.copy(lastName = it) },
                        label = { Text("Last Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("last_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DestinyPrimary,
                            unfocusedBorderColor = DestinyOutline
                        ),
                        singleLine = true
                    )

                    OutlinedButton(
                        onClick = {
                            val pair = CharacterData.getRandomName(params.gender)
                            params = params.copy(firstName = pair.first, lastName = pair.second)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("randomize_name_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Randomize",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Randomize Name")
                    }
                }
            }
        }

        // --- STEP 4: Gender & Sexuality ---
        if (step == 4) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        text = "Gender & Sexuality",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "GENDER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinyTextMuted
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CharacterData.GENDERS.forEach { g ->
                                val isSelected = params.gender == g
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) DestinyPrimary.copy(alpha = 0.15f) else DestinySurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) DestinyPrimary else DestinyOutline,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { params = params.copy(gender = g) }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = g,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SEXUALITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DestinyTextMuted
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CharacterData.SEXUALITIES.forEach { s ->
                                val isSelected = params.sexuality == s
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) DestinyPrimary.copy(alpha = 0.15f) else DestinySurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) DestinyPrimary else DestinyOutline,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { params = params.copy(sexuality = s) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = s,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- STEP 5: Special Talent ---
        if (step == 5) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Special Talent",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary
                    )
                    Text(
                        text = "Inherent natural gifts modifying skill acquisition and career trajectory coefficients.",
                        fontSize = 13.sp,
                        color = DestinyTextSecondary
                    )

                    CharacterData.TALENTS.forEach { t ->
                        val isSelected = params.talent == t.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DestinyPrimary.copy(alpha = 0.15f) else DestinySurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isSelected) DestinyPrimary else DestinyOutline,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { params = params.copy(talent = t.id) }
                                .testTag("talent_option_${t.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = t.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = DestinyTextPrimary
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = DestinyPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = t.desc,
                                    fontSize = 12.sp,
                                    color = DestinyTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- STEP 6: Appearance Customization & Live Avatar Preview ---
        if (step == 6) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Appearance Customization",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DestinyTextPrimary
                    )

                    // Live Visual Preview Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DestinySurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DestinyOutline, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CharacterAvatar(
                                skinTone = params.skinTone,
                                eyeStyle = params.eyeStyle,
                                eyeColor = params.eyeColor,
                                browStyle = params.browStyle,
                                hairStyle = params.hairStyle,
                                hairColor = params.hairColor,
                                facialHairStyle = params.facialHairStyle,
                                facialHairColor = params.facialHairColor,
                                size = 130.dp
                            )
                            Text(
                                text = "${params.skinTone} • ${params.hairStyle} (${params.hairColor})",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = DestinyPrimary
                            )
                        }
                    }

                    // Skin Tone Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Skin Tone", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DestinyTextPrimary)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CharacterData.SKIN_TONES.forEach { s ->
                                val isSelected = params.skinTone == s.name
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DestinyPrimary.copy(alpha = 0.2f) else DestinySurface)
                                        .border(1.dp, if (isSelected) DestinyPrimary else DestinyOutline, RoundedCornerShape(8.dp))
                                        .clickable { params = params.copy(skinTone = s.name) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(s.name, fontSize = 12.sp, color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary)
                                }
                            }
                        }
                    }

                    // Eye Style & Color
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Eye Style", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DestinyTextPrimary)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                CharacterData.EYE_STYLES.forEach { style ->
                                    val isSelected = params.eyeStyle == style
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) DestinyPrimary.copy(alpha = 0.2f) else DestinySurface)
                                            .border(1.dp, if (isSelected) DestinyPrimary else DestinyOutline, RoundedCornerShape(6.dp))
                                            .clickable { params = params.copy(eyeStyle = style) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(style, fontSize = 11.sp, color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary)
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Eye Color", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DestinyTextPrimary)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                CharacterData.EYE_COLORS.forEach { color ->
                                    val isSelected = params.eyeColor == color.name
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) DestinyPrimary.copy(alpha = 0.2f) else DestinySurface)
                                            .border(1.dp, if (isSelected) DestinyPrimary else DestinyOutline, RoundedCornerShape(6.dp))
                                            .clickable { params = params.copy(eyeColor = color.name) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(color.name, fontSize = 11.sp, color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // Hair Style & Color
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Hair Style", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DestinyTextPrimary)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CharacterData.HAIR_STYLES.forEach { hs ->
                                val isSelected = params.hairStyle == hs
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DestinyPrimary.copy(alpha = 0.2f) else DestinySurface)
                                        .border(1.dp, if (isSelected) DestinyPrimary else DestinyOutline, RoundedCornerShape(8.dp))
                                        .clickable { params = params.copy(hairStyle = hs) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(hs, fontSize = 12.sp, color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary)
                                }
                            }
                        }
                    }

                    // Hair Color
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Hair Color", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DestinyTextPrimary)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CharacterData.HAIR_COLORS.forEach { hc ->
                                val isSelected = params.hairColor == hc.name
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DestinyPrimary.copy(alpha = 0.2f) else DestinySurface)
                                        .border(1.dp, if (isSelected) DestinyPrimary else DestinyOutline, RoundedCornerShape(8.dp))
                                        .clickable { params = params.copy(hairColor = hc.name) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(hc.name, fontSize = 12.sp, color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary)
                                }
                            }
                        }
                    }

                    // Facial Hair Style
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Facial Hair", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DestinyTextPrimary)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CharacterData.FACIAL_HAIR_STYLES.forEach { fhs ->
                                val isSelected = params.facialHairStyle == fhs
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DestinyPrimary.copy(alpha = 0.2f) else DestinySurface)
                                        .border(1.dp, if (isSelected) DestinyPrimary else DestinyOutline, RoundedCornerShape(8.dp))
                                        .clickable { params = params.copy(facialHairStyle = fhs) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(fhs, fontSize = 12.sp, color = if (isSelected) DestinyTextPrimary else DestinyTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- STEP 7: Starting Attributes (Karma is strictly omitted) ---
        if (step == 7) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Starting Attributes",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = DestinyTextPrimary
                            )
                            Text(
                                text = "Baseline vital & cognitive scores (0-100).",
                                fontSize = 12.sp,
                                color = DestinyTextSecondary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val r = { min: Int, max: Int -> (min..max).random() }
                                params = params.copy(
                                    intelligence = r(45, 92),
                                    discipline = r(40, 88),
                                    willpower = r(42, 90),
                                    ambition = r(45, 95),
                                    health = r(70, 98),
                                    looks = r(40, 92),
                                    smarts = r(45, 92),
                                    happiness = r(50, 90),
                                    fertility = r(75, 98),
                                    energy = r(85, 100),
                                    athleticPerformance = r(40, 90)
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("roll_distribution_button")
                        ) {
                            Text("🎲 Roll", fontSize = 12.sp)
                        }
                    }

                    // Attributes Slider List
                    listOf(
                        "Health" to (params.health to { v: Int -> params = params.copy(health = v) }),
                        "Energy" to (params.energy to { v: Int -> params = params.copy(energy = v) }),
                        "Athletic Performance" to (params.athleticPerformance to { v: Int -> params = params.copy(athleticPerformance = v) }),
                        "Fertility" to (params.fertility to { v: Int -> params = params.copy(fertility = v) }),
                        "Happiness" to (params.happiness to { v: Int -> params = params.copy(happiness = v) }),
                        "Intelligence" to (params.intelligence to { v: Int -> params = params.copy(intelligence = v) }),
                        "Smarts" to (params.smarts to { v: Int -> params = params.copy(smarts = v) }),
                        "Discipline" to (params.discipline to { v: Int -> params = params.copy(discipline = v) }),
                        "Willpower" to (params.willpower to { v: Int -> params = params.copy(willpower = v) }),
                        "Ambition" to (params.ambition to { v: Int -> params = params.copy(ambition = v) }),
                        "Looks" to (params.looks to { v: Int -> params = params.copy(looks = v) })
                    ).forEach { (label, pair) ->
                        val (value, setter) = pair
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DestinySurface),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DestinyOutline, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, fontSize = 13.sp, color = DestinyTextPrimary)
                                    Text(
                                        "$value",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = DestinyPrimary
                                    )
                                }
                                Slider(
                                    value = value.toFloat(),
                                    onValueChange = { setter(it.toInt()) },
                                    valueRange = 1f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = DestinyPrimary,
                                        activeTrackColor = DestinyPrimary,
                                        inactiveTrackColor = DestinySurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    // Privacy Assurance Notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DestinySurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "🔒 Privacy Mandate: Internal morality and karma indicators are computed strictly server-side and are completely excluded from client representations.",
                            fontSize = 11.sp,
                            color = DestinyTextMuted
                        )
                    }
                }
            }
        }

        // --- Error Message Banner ---
        errorMessage?.let { msg ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = msg,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // --- Bottom Navigation Buttons ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (step > 1) step-- else onBack()
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("previous_step_button")
                ) {
                    Text(if (step == 1) "Cancel" else "Previous")
                }

                if (step < 7) {
                    Button(
                        onClick = {
                            if (step == 3 && (params.firstName.isBlank() || params.lastName.isBlank())) {
                                errorMessage = "Please enter both first and last names."
                            } else {
                                errorMessage = null
                                step++
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary),
                        modifier = Modifier.testTag("next_step_button")
                    ) {
                        Text("Next", color = MaterialTheme.colorScheme.background, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            errorMessage = null
                            onSubmit(params)
                        },
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DestinySuccess),
                        modifier = Modifier.testTag("create_character_submit_button")
                    ) {
                        Text(
                            if (isSubmitting) "Creating..." else "Initialize Life",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
