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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.FamilyEvent
import com.example.data.model.Relationship
import com.example.data.model.deriveFriendTier
import com.example.data.repository.LifeRecordSummary
import com.example.ui.theme.DestinyError
import com.example.ui.theme.DestinyOutline
import com.example.ui.theme.DestinyPrimary
import com.example.ui.theme.DestinySuccess
import com.example.ui.theme.DestinySurface
import com.example.ui.theme.DestinySurfaceVariant
import com.example.ui.theme.DestinyTextMuted
import com.example.ui.theme.DestinyTextPrimary
import com.example.ui.theme.DestinyTextSecondary
import java.util.Locale

@Composable
fun RelationshipsScreen(
    summary: LifeRecordSummary,
    relationships: List<Relationship>,
    familyEvents: List<FamilyEvent>,
    isLoading: Boolean,
    actionMessage: String?,
    onBack: () -> Unit,
    onLoadData: (String, Int) -> Unit,
    onMarry: (String, String, Int) -> Unit,
    onDivorce: (String, String, Int) -> Unit,
    onHaveChild: (String, String, String, Int) -> Unit,
    onAddEnemy: (String, String, Int) -> Unit,
    onTriggerInheritance: (String, Int) -> Unit,
    onClearMessage: () -> Unit
) {
    val life = summary.life
    val character = summary.clientCharacter
    val currentYear = life.birthYear + life.currentAge

    LaunchedEffect(character.id) {
        onLoadData(character.id, currentYear)
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Categories, 1: Family Tree, 2: Drama Log
    var showMarryConfirmDialog by remember { mutableStateOf<Relationship?>(null) }
    var showDivorceConfirmDialog by remember { mutableStateOf<Relationship?>(null) }
    var showChildDialog by remember { mutableStateOf<Relationship?>(null) }
    var showAddEnemyDialog by remember { mutableStateOf(false) }
    var showInheritanceDialog by remember { mutableStateOf(false) }

    var childNameInput by remember { mutableStateOf("") }
    var enemyNameInput by remember { mutableStateOf("") }

    val spouse = relationships.find { it.type == "Spouse" && it.status == "Active" }
    val children = relationships.filter { it.type == "Child" && it.status != "Deceased" }
    val loveRelationships = relationships.filter { it.type in listOf("Spouse", "Ex") }
    val specialRelationships = relationships.filter { it.type in listOf("Enemy", "Mentor", "Parent", "Sibling") }
    val friendRelationships = relationships.filter { it.type in listOf("Friend", "BestFriend") }
    val lateRelationships = relationships.filter { it.status == "Deceased" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("relationships_screen")
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("relationships_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DestinyTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Relationships & Family",
                        style = MaterialTheme.typography.titleLarge,
                        color = DestinyTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${life.name} • Year $currentYear (Age ${life.currentAge})",
                        style = MaterialTheme.typography.bodySmall,
                        color = DestinyTextSecondary
                    )
                }
            }

            // Bank Balance Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DestinySurfaceVariant)
                    .border(1.dp, DestinyOutline, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$${character.bankBalance}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DestinySuccess,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // --- Action Feedback Message Banner ---
        if (actionMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DestinySurfaceVariant)
                    .border(1.dp, DestinyPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = DestinyTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onClearMessage,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = DestinyTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // --- View Mode Tabs ---
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = DestinyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Categories (${relationships.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Family Tree") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Drama Log (${familyEvents.size})") }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DestinyPrimary)
            }
        } else {
            when (selectedTab) {
                0 -> {
                    // Category List View
                    CategoriesView(
                        character = character,
                        spouse = spouse,
                        children = children,
                        loveRelationships = loveRelationships,
                        specialRelationships = specialRelationships,
                        friendRelationships = friendRelationships,
                        lateRelationships = lateRelationships,
                        onOpenMarry = { showMarryConfirmDialog = it },
                        onOpenDivorce = { showDivorceConfirmDialog = it },
                        onOpenHaveChild = { showChildDialog = it },
                        onOpenAddEnemy = { showAddEnemyDialog = true },
                        onOpenInheritance = { showInheritanceDialog = true }
                    )
                }
                1 -> {
                    // Family Tree View (Hierarchical list)
                    FamilyTreeView(
                        character = character,
                        life = life,
                        relationships = relationships
                    )
                }
                2 -> {
                    // Drama Events Log View
                    FamilyDramaLogView(events = familyEvents)
                }
            }
        }
    }

    // --- Marriage Confirmation Dialog ---
    if (showMarryConfirmDialog != null) {
        val partner = showMarryConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showMarryConfirmDialog = null },
            title = { Text("Propose Marriage", color = DestinyTextPrimary) },
            text = {
                Text(
                    text = "Are you sure you want to enter into marriage with ${partner.relatedCharacter?.id ?: "Partner"}? " +
                            "If dormant, this NPC will be promoted to active simulation state.",
                    color = DestinyTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onMarry(character.id, partner.relatedCharacterId, currentYear)
                        showMarryConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary),
                    modifier = Modifier.testTag("confirm_marriage_button")
                ) {
                    Text("Confirm Marriage", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMarryConfirmDialog = null }) {
                    Text("Cancel", color = DestinyTextMuted)
                }
            },
            containerColor = DestinySurface
        )
    }

    // --- Divorce Confirmation Dialog ---
    if (showDivorceConfirmDialog != null) {
        val partner = showDivorceConfirmDialog!!
        val splitEst = character.bankBalance / 2
        AlertDialog(
            onDismissRequest = { showDivorceConfirmDialog = null },
            title = { Text("Finalize Divorce", color = DestinyError) },
            text = {
                Text(
                    text = "Are you sure you want to divorce ${partner.relatedCharacter?.id ?: "Spouse"}? " +
                            "All marital assets will be split 50/50 (estimated $$splitEst share), " +
                            "and relationship will transition to Ex.",
                    color = DestinyTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDivorce(character.id, partner.relatedCharacterId, currentYear)
                        showDivorceConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestinyError),
                    modifier = Modifier.testTag("confirm_divorce_button")
                ) {
                    Text("Confirm Divorce", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDivorceConfirmDialog = null }) {
                    Text("Cancel", color = DestinyTextMuted)
                }
            },
            containerColor = DestinySurface
        )
    }

    // --- Have Child Dialog ---
    if (showChildDialog != null) {
        val partner = showChildDialog!!
        AlertDialog(
            onDismissRequest = { showChildDialog = null },
            title = { Text("Have a Child", color = DestinyTextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Blend genetics with ${partner.relatedCharacter?.id ?: "Partner"} via FormulaEngine. Child will inherit weighted parental traits with luck variance.",
                        color = DestinyTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = childNameInput,
                        onValueChange = { childNameInput = it },
                        label = { Text("Child Name") },
                        placeholder = { Text("e.g., Benjamin Vance") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("child_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = childNameInput.ifBlank { "Child_${System.currentTimeMillis() % 1000}" }
                        onHaveChild(character.id, partner.relatedCharacterId, name, currentYear)
                        childNameInput = ""
                        showChildDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary),
                    modifier = Modifier.testTag("confirm_have_child_button")
                ) {
                    Text("Welcome Baby", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChildDialog = null }) {
                    Text("Cancel", color = DestinyTextMuted)
                }
            },
            containerColor = DestinySurface
        )
    }

    // --- Add Enemy Dialog ---
    if (showAddEnemyDialog) {
        AlertDialog(
            onDismissRequest = { showAddEnemyDialog = false },
            title = { Text("Establish Rivalry / Enemy", color = DestinyTextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Enemy will have sabotage chance calculated based on their Influence capital via FormulaEngine.",
                        color = DestinyTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = enemyNameInput,
                        onValueChange = { enemyNameInput = it },
                        label = { Text("Enemy Name") },
                        placeholder = { Text("e.g., Victor Blackwood") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enemy_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = enemyNameInput.ifBlank { "Rival_${System.currentTimeMillis() % 1000}" }
                        onAddEnemy(character.id, name, currentYear)
                        enemyNameInput = ""
                        showAddEnemyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary),
                    modifier = Modifier.testTag("confirm_add_enemy_button")
                ) {
                    Text("Add Enemy", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEnemyDialog = false }) {
                    Text("Cancel", color = DestinyTextMuted)
                }
            },
            containerColor = DestinySurface
        )
    }

    // --- Inheritance Simulation Dialog ---
    if (showInheritanceDialog) {
        AlertDialog(
            onDismissRequest = { showInheritanceDialog = false },
            title = { Text("Execute Inheritance Hook", color = DestinyTextPrimary) },
            text = {
                Text(
                    text = "Distribute the deceased's bank balance of $${character.bankBalance}. " +
                            "Asset split divides equally among living children; if none, inherits to spouse; otherwise transfers to Estate.",
                    color = DestinyTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTriggerInheritance(character.id, currentYear)
                        showInheritanceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary),
                    modifier = Modifier.testTag("confirm_inheritance_button")
                ) {
                    Text("Execute Distribution", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInheritanceDialog = false }) {
                    Text("Cancel", color = DestinyTextMuted)
                }
            },
            containerColor = DestinySurface
        )
    }
}

// -----------------------------------------------------------------------------------------
// Category View Component
// -----------------------------------------------------------------------------------------

@Composable
fun CategoriesView(
    character: com.example.data.model.ClientCharacter,
    spouse: Relationship?,
    children: List<Relationship>,
    loveRelationships: List<Relationship>,
    specialRelationships: List<Relationship>,
    friendRelationships: List<Relationship>,
    lateRelationships: List<Relationship>,
    onOpenMarry: (Relationship) -> Unit,
    onOpenDivorce: (Relationship) -> Unit,
    onOpenHaveChild: (Relationship) -> Unit,
    onOpenAddEnemy: () -> Unit,
    onOpenInheritance: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Category 1: Love ---
        item {
            CategoryHeader(title = "Love", count = loveRelationships.size)
        }

        if (spouse != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DestinySurface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_spouse_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Spouse",
                                    tint = DestinyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Spouse (${spouse.relatedCharacter?.id ?: spouse.relatedCharacterId})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DestinyTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Married since ${spouse.startedAt}",
                                style = MaterialTheme.typography.bodySmall,
                                color = DestinyTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        RelationshipStrengthBar(strength = spouse.relationshipStrength)

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { onOpenHaveChild(spouse) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("have_child_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = DestinyPrimary)
                            ) {
                                Text("Have Child", color = Color.White)
                            }

                            OutlinedButton(
                                onClick = { onOpenDivorce(spouse) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("divorce_button")
                            ) {
                                Text("Divorce", color = DestinyError)
                            }
                        }
                    }
                }
            }
        }

        // Show Exes or unmarried prompt
        val exes = loveRelationships.filter { it.type == "Ex" }
        if (exes.isNotEmpty()) {
            items(exes) { ex ->
                RelationshipItemCard(relationship = ex)
            }
        }

        if (spouse == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DestinySurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Unmarried",
                            style = MaterialTheme.typography.titleMedium,
                            color = DestinyTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "You are not currently married. You can propose to an eligible friend or partner below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DestinyTextSecondary
                        )
                    }
                }
            }
        }

        // --- Category 2: Children ---
        item {
            CategoryHeader(title = "Children", count = children.size)
        }

        if (children.isEmpty()) {
            item {
                EmptyStateCard(message = "No children yet. Have a child with your spouse to pass on genetic modifiers.")
            }
        } else {
            items(children) { childRel ->
                ChildItemCard(childRel = childRel)
            }
        }

        // --- Category 3: Special (Enemies, Parents, Mentors) ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryHeader(title = "Special", count = specialRelationships.size)
                TextButton(onClick = onOpenAddEnemy) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Enemy", color = DestinyPrimary)
                }
            }
        }

        items(specialRelationships) { rel ->
            RelationshipItemCard(relationship = rel)
        }

        // --- Category 4: Friends ---
        item {
            CategoryHeader(title = "Friends", count = friendRelationships.size)
        }

        if (friendRelationships.isEmpty()) {
            item {
                EmptyStateCard(message = "No friends recorded in your circle.")
            }
        } else {
            items(friendRelationships) { friendRel ->
                FriendItemCard(
                    friendRel = friendRel,
                    canMarry = (spouse == null),
                    onMarry = { onOpenMarry(friendRel) }
                )
            }
        }

        // --- Category 5: Late Relationships & Inheritance ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryHeader(title = "Late Relationships", count = lateRelationships.size)
                OutlinedButton(onClick = onOpenInheritance) {
                    Text("Simulate Inheritance", color = DestinySuccess, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (lateRelationships.isEmpty()) {
            item {
                EmptyStateCard(message = "No deceased relatives or partners in your records.")
            }
        } else {
            items(lateRelationships) { lateRel ->
                RelationshipItemCard(relationship = lateRel)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Family Tree View (Hierarchical List)
// -----------------------------------------------------------------------------------------

@Composable
fun FamilyTreeView(
    character: com.example.data.model.ClientCharacter,
    life: com.example.data.local.entity.LifeEntity,
    relationships: List<Relationship>
) {
    val parents = relationships.filter { it.type == "Parent" }
    val spouse = relationships.find { it.type == "Spouse" && it.status == "Active" }
    val siblings = relationships.filter { it.type == "Sibling" }
    val children = relationships.filter { it.type == "Child" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Hierarchical Family Lineage",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Parent/Child/Sibling structural connections and lineage branches.",
                style = MaterialTheme.typography.bodySmall,
                color = DestinyTextSecondary
            )
        }

        // Tier 1: Generation 1 (Parents)
        item {
            FamilyTreeNodeCard(
                tierLabel = "Generation 1 • Parents",
                nodes = parents.map { "${it.relatedCharacter?.id ?: it.relatedCharacterId} (${it.status})" },
                emptyText = "Parents not documented in current branch"
            )
        }

        // Tier 2: Generation 2 (You & Spouse & Siblings)
        item {
            val meAndSiblings = mutableListOf("${life.name} [You]")
            if (spouse != null) {
                meAndSiblings.add("Spouse: ${spouse.relatedCharacter?.id ?: spouse.relatedCharacterId}")
            }
            siblings.forEach { sib ->
                meAndSiblings.add("Sibling: ${sib.relatedCharacter?.id ?: sib.relatedCharacterId}")
            }

            FamilyTreeNodeCard(
                tierLabel = "Generation 2 • Current Generation",
                nodes = meAndSiblings,
                emptyText = "No current generation nodes"
            )
        }

        // Tier 3: Generation 3 (Children)
        item {
            FamilyTreeNodeCard(
                tierLabel = "Generation 3 • Offspring & Descendants",
                nodes = children.map {
                    val modDesc = if (it.relatedCharacter?.geneticHealthModifier != null) {
                        " (Genetics: H:${String.format(Locale.US, "%+.1f", it.relatedCharacter.geneticHealthModifier)})"
                    } else ""
                    "${it.relatedCharacter?.id ?: it.relatedCharacterId}$modDesc"
                },
                emptyText = "No children born to this branch yet"
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// Family Drama Events Log View
// -----------------------------------------------------------------------------------------

@Composable
fun FamilyDramaLogView(events: List<FamilyEvent>) {
    if (events.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No family drama milestones logged yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = DestinyTextMuted
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DestinySurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when (event.eventType) {
                            "Marriage" -> Icons.Default.Favorite
                            "Divorce" -> Icons.Default.Warning
                            "Birth" -> Icons.Default.CheckCircle
                            "Death" -> Icons.Default.Info
                            else -> Icons.Default.Person
                        }
                        val tint = when (event.eventType) {
                            "Marriage" -> DestinyPrimary
                            "Divorce" -> DestinyError
                            "Birth" -> DestinySuccess
                            else -> DestinyTextSecondary
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = event.eventType,
                            tint = tint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = event.eventType.uppercase(Locale.ROOT),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tint,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Year ${event.gameYear}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DestinyTextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DestinyTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Helper UI Components
// -----------------------------------------------------------------------------------------

@Composable
fun CategoryHeader(title: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = DestinyTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(DestinySurfaceVariant)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = DestinyTextSecondary
            )
        }
    }
}

@Composable
fun RelationshipStrengthBar(strength: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Relationship Strength",
                style = MaterialTheme.typography.labelSmall,
                color = DestinyTextMuted
            )
            Text(
                text = "$strength / 100",
                style = MaterialTheme.typography.labelSmall,
                color = DestinyTextSecondary,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { strength / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (strength >= 70) DestinySuccess else if (strength >= 40) DestinyPrimary else DestinyError,
            trackColor = DestinySurfaceVariant
        )
    }
}

@Composable
fun RelationshipItemCard(relationship: Relationship) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DestinySurface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = relationship.relatedCharacter?.id ?: relationship.relatedCharacterId,
                        style = MaterialTheme.typography.titleSmall,
                        color = DestinyTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${relationship.type} • Status: ${relationship.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DestinyTextMuted
                    )
                }

                if (relationship.sabotageChance != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DestinyError.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Sabotage: ${String.format(Locale.US, "%.1f", relationship.sabotageChance)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = DestinyError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            RelationshipStrengthBar(strength = relationship.relationshipStrength)
        }
    }
}

@Composable
fun ChildItemCard(childRel: Relationship) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DestinySurface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = childRel.relatedCharacter?.id ?: childRel.relatedCharacterId,
                        style = MaterialTheme.typography.titleSmall,
                        color = DestinyTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Born Year ${childRel.startedAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DestinyTextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DestinySuccess.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Genetics Blended",
                        style = MaterialTheme.typography.labelSmall,
                        color = DestinySuccess,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            val char = childRel.relatedCharacter
            if (char != null && char.geneticHealthModifier != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GeneticBadge(label = "Health", value = char.geneticHealthModifier)
                    GeneticBadge(label = "Int", value = char.geneticIntelligenceModifier)
                    GeneticBadge(label = "Looks", value = char.geneticLooksModifier)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            RelationshipStrengthBar(strength = childRel.relationshipStrength)
        }
    }
}

@Composable
fun GeneticBadge(label: String, value: Double?) {
    if (value == null) return
    val formatted = String.format(Locale.US, "%+.1f", value)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DestinySurfaceVariant)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label: $formatted",
            style = MaterialTheme.typography.labelSmall,
            color = if (value >= 0) DestinySuccess else DestinyError,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
    }
}

@Composable
fun FriendItemCard(
    friendRel: Relationship,
    canMarry: Boolean,
    onMarry: () -> Unit
) {
    val tier = deriveFriendTier(friendRel.type, friendRel.relationshipStrength)
    Card(
        colors = CardDefaults.cardColors(containerColor = DestinySurface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = friendRel.relatedCharacter?.id ?: friendRel.relatedCharacterId,
                        style = MaterialTheme.typography.titleSmall,
                        color = DestinyTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Tier: $tier • Since Year ${friendRel.startedAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DestinyTextMuted
                    )
                }

                if (canMarry) {
                    OutlinedButton(
                        onClick = onMarry,
                        modifier = Modifier.testTag("marry_button")
                    ) {
                        Text("Propose", color = DestinyPrimary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            RelationshipStrengthBar(strength = friendRel.relationshipStrength)
        }
    }
}

@Composable
fun FamilyTreeNodeCard(
    tierLabel: String,
    nodes: List<String>,
    emptyText: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DestinySurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tierLabel,
                style = MaterialTheme.typography.titleSmall,
                color = DestinyPrimary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = DestinyOutline.copy(alpha = 0.5f)
            )

            if (nodes.isEmpty()) {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodySmall,
                    color = DestinyTextMuted
                )
            } else {
                nodes.forEach { node ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(DestinyPrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = node,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DestinyTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DestinySurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = DestinyTextMuted
            )
        }
    }
}
