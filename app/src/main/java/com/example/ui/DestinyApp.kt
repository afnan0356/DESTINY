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
import com.example.ui.screens.DestinyLandingScreen
import com.example.ui.screens.NewLifeFlowScreen
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

    DestinyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                is DestinyScreen.Landing -> {
                    DestinyLandingScreen(
                        lives = allLives,
                        clockDisplay = clockDisplay,
                        formulaOutcome = formulaOutcome,
                        onNavigate = { viewModel.navigateTo(it) },
                        onAdvanceClock = { viewModel.advanceClock() },
                        onClockResolutionChange = { viewModel.setClockResolution(it) },
                        onInspectLife = { viewModel.inspectLife(it) }
                    )
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
