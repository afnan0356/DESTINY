package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.LifeDetailDialog
import com.example.ui.screens.ArchitectureInspectorScreen
import com.example.ui.screens.CharacterCreationScreen
import com.example.ui.screens.CharacterProfileScreen
import com.example.ui.screens.DestinyLandingScreen
import com.example.ui.screens.NewLifeFlowScreen
import com.example.ui.screens.RelationshipsScreen
import com.example.ui.theme.DestinyTheme

@Composable
fun DestinyApp(
    viewModel: DestinyViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val allLives by viewModel.allLives.collectAsState()
    val newLifeState by viewModel.newLifeState.collectAsState()
    val selectedLife by viewModel.selectedLifeSummary.collectAsState()
    val clockDisplay by viewModel.clockDisplay.collectAsState()
    val formulaOutcome by viewModel.lastFormulaOutcome.collectAsState()

    // Prompt 02 Character States
    val isSubmittingCharacter by viewModel.isSubmittingCharacter.collectAsState()
    val activeProfileSummary by viewModel.activeProfileSummary.collectAsState()
    val deathCheckResult by viewModel.deathCheckResult.collectAsState()
    val simLogs by viewModel.simLogs.collectAsState()
    val isAging by viewModel.isAging.collectAsState()

    // Prompt 03 Family States
    val relationships by viewModel.relationships.collectAsState()
    val familyEvents by viewModel.familyEvents.collectAsState()
    val isFamilyActionLoading by viewModel.isFamilyActionLoading.collectAsState()
    val familyActionMessage by viewModel.familyActionMessage.collectAsState()

    DestinyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val screen = currentScreen) {
                is DestinyScreen.Landing -> {
                    DestinyLandingScreen(
                        lives = allLives,
                        clockDisplay = clockDisplay,
                        formulaOutcome = formulaOutcome,
                        onNavigate = { viewModel.navigateTo(it) },
                        onAdvanceClock = { viewModel.advanceClock() },
                        onClockResolutionChange = { viewModel.setClockResolution(it) },
                        onInspectLife = { viewModel.inspectLife(it) },
                        onOpenProfile = { viewModel.openCharacterProfile(it) }
                    )
                }

                is DestinyScreen.CharacterCreation -> {
                    CharacterCreationScreen(
                        isSubmitting = isSubmittingCharacter,
                        onBack = { viewModel.navigateTo(DestinyScreen.Landing) },
                        onSubmit = { viewModel.createFullCharacter(it) }
                    )
                }

                is DestinyScreen.CharacterProfile -> {
                    activeProfileSummary?.let { summary ->
                        CharacterProfileScreen(
                            summary = summary,
                            deathResult = deathCheckResult,
                            simLogs = simLogs,
                            isAging = isAging,
                            onBack = { viewModel.navigateTo(DestinyScreen.Landing) },
                            onAgeOneYear = { viewModel.ageActiveCharacter() },
                            onOpenRelationships = { viewModel.navigateTo(DestinyScreen.Relationships(screen.lifeId)) }
                        )
                    } ?: run {
                        // Fallback if summary loading
                        viewModel.openCharacterProfile(screen.lifeId)
                    }
                }

                is DestinyScreen.Relationships, is DestinyScreen.FamilyTree -> {
                    val lifeId = if (screen is DestinyScreen.Relationships) screen.lifeId else (screen as DestinyScreen.FamilyTree).lifeId
                    activeProfileSummary?.let { summary ->
                        RelationshipsScreen(
                            summary = summary,
                            relationships = relationships,
                            familyEvents = familyEvents,
                            isLoading = isFamilyActionLoading,
                            actionMessage = familyActionMessage,
                            onBack = { viewModel.navigateTo(DestinyScreen.CharacterProfile(lifeId)) },
                            onLoadData = { charId, year -> viewModel.loadFamilyData(charId, year) },
                            onMarry = { cId, pId, yr -> viewModel.marry(cId, pId, yr) },
                            onDivorce = { cId, pId, yr -> viewModel.divorce(cId, pId, yr) },
                            onHaveChild = { p1, p2, name, yr -> viewModel.haveChild(p1, p2, name, yr) },
                            onAddEnemy = { cId, name, yr -> viewModel.addEnemy(cId, name, yr) },
                            onTriggerInheritance = { cId, yr -> viewModel.triggerInheritance(cId, yr) },
                            onClearMessage = { viewModel.clearFamilyActionMessage() }
                        )
                    } ?: run {
                        viewModel.openCharacterProfile(lifeId)
                    }
                }

                is DestinyScreen.NewLifeFlow -> {
                    NewLifeFlowScreen(
                        state = newLifeState,
                        onBack = { viewModel.navigateTo(DestinyScreen.Landing) },
                        onPlayerNameChange = { viewModel.updatePlayerName(it) },
                        onSlotNameChange = { viewModel.updateSlotName(it) },
                        onCharacterNameChange = { viewModel.updateCharacterName(it) },
                        onBirthYearChange = { viewModel.updateBirthYear(it) },
                        onIsDormantChange = { viewModel.updateIsDormant(it) },
                        onSubmit = { viewModel.executeNewLifeFlow() }
                    )
                }

                is DestinyScreen.ArchitectureSpec -> {
                    ArchitectureInspectorScreen(
                        onBack = { viewModel.navigateTo(DestinyScreen.Landing) }
                    )
                }
            }

            // Inspect Dialog
            selectedLife?.let { summary ->
                LifeDetailDialog(
                    summary = summary,
                    onDismiss = { viewModel.closeInspect() },
                    onToggleDormancy = { lifeId, currentDormancy ->
                        viewModel.toggleDormancy(lifeId, currentDormancy)
                    }
                )
            }
        }
    }
}
