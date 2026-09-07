package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DestinyDatabase
import com.example.data.local.entity.LifeEntity
import com.example.data.repository.DestinyRepository
import com.example.data.repository.LifeRecordSummary
import com.example.services.FormulaEngine
import com.example.services.GameClock
import com.example.services.LifeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DestinyScreen {
    data object Landing : DestinyScreen
    data object NewLifeFlow : DestinyScreen
    data object ArchitectureSpec : DestinyScreen
}

data class NewLifeUiState(
    val playerName: String = "Player_Zero",
    val saveSlotName: String = "Slot Alpha",
    val characterName: String = "Julian Vance",
    val birthYear: Int = 2000,
    val isDormant: Boolean = false,
    val isSubmitting: Boolean = false,
    val lastCreatedRecord: LifeRecordSummary? = null,
    val statusMessage: String? = null
)

class DestinyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DestinyDatabase.getDatabase(application)
    private val repository = DestinyRepository(db.destinyDao())
    val lifeService = LifeService(repository)
    val gameClock = GameClock(initialYear = 2026, initialResolution = GameClock.TickResolution.YEARLY)

    val allLives: StateFlow<List<LifeEntity>> = repository.allLives.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentScreen = MutableStateFlow<DestinyScreen>(DestinyScreen.Landing)
    val currentScreen: StateFlow<DestinyScreen> = _currentScreen.asStateFlow()

    private val _newLifeState = MutableStateFlow(NewLifeUiState())
    val newLifeState: StateFlow<NewLifeUiState> = _newLifeState.asStateFlow()

    private val _selectedLifeSummary = MutableStateFlow<LifeRecordSummary?>(null)
    val selectedLifeSummary: StateFlow<LifeRecordSummary?> = _selectedLifeSummary.asStateFlow()

    private val _clockDisplay = MutableStateFlow(gameClock.formatDisplay())
    val clockDisplay: StateFlow<String> = _clockDisplay.asStateFlow()

    private val _lastFormulaOutcome = MutableStateFlow<FormulaEngine.Outcome?>(null)
    val lastFormulaOutcome: StateFlow<FormulaEngine.Outcome?> = _lastFormulaOutcome.asStateFlow()

    init {
        // Evaluate initial sample through FormulaEngine to verify pipeline
        runFormulaProbe()
    }

    fun navigateTo(screen: DestinyScreen) {
        _currentScreen.value = screen
    }

    fun updatePlayerName(name: String) {
        _newLifeState.value = _newLifeState.value.copy(playerName = name)
    }

    fun updateSlotName(slot: String) {
        _newLifeState.value = _newLifeState.value.copy(saveSlotName = slot)
    }

    fun updateCharacterName(name: String) {
        _newLifeState.value = _newLifeState.value.copy(characterName = name)
    }

    fun updateBirthYear(year: Int) {
        _newLifeState.value = _newLifeState.value.copy(birthYear = year)
    }

    fun updateIsDormant(dormant: Boolean) {
        _newLifeState.value = _newLifeState.value.copy(isDormant = dormant)
    }

    fun executeNewLifeFlow() {
        val current = _newLifeState.value
        _newLifeState.value = current.copy(isSubmitting = true, statusMessage = "Persisting User → SaveSlot → Life → Character...")

        viewModelScope.launch {
            try {
                val record = lifeService.createNewLife(
                    playerName = current.playerName,
                    saveSlotName = current.saveSlotName,
                    characterName = current.characterName,
                    birthYear = current.birthYear,
                    isDormant = current.isDormant
                )

                _newLifeState.value = current.copy(
                    isSubmitting = false,
                    lastCreatedRecord = record,
                    statusMessage = "✓ Transaction confirmed: Written to database with ID: ${record.life.id}"
                )
            } catch (e: Exception) {
                _newLifeState.value = current.copy(
                    isSubmitting = false,
                    statusMessage = "Database error: ${e.localizedMessage}"
                )
            }
        }
    }

    fun inspectLife(lifeId: String) {
        viewModelScope.launch {
            val summary = lifeService.getLifeDetails(lifeId)
            _selectedLifeSummary.value = summary
        }
    }

    fun closeInspect() {
        _selectedLifeSummary.value = null
    }

    fun toggleDormancy(lifeId: String, currentIsDormant: Boolean) {
        viewModelScope.launch {
            lifeService.setNpcSimulationState(lifeId, !currentIsDormant)
            // Refresh inspect if open
            if (_selectedLifeSummary.value?.life?.id == lifeId) {
                _selectedLifeSummary.value = lifeService.getLifeDetails(lifeId)
            }
        }
    }

    fun advanceClock() {
        gameClock.advanceTick(1)
        _clockDisplay.value = gameClock.formatDisplay()
    }

    fun setClockResolution(resolution: GameClock.TickResolution) {
        if (resolution == GameClock.TickResolution.YEARLY) {
            gameClock.exitEventZoom()
        } else {
            gameClock.enterEventZoom(resolution)
        }
        _clockDisplay.value = gameClock.formatDisplay()
    }

    fun runFormulaProbe() {
        val testOutcome = FormulaEngine.evaluate(
            characterFoundation = FormulaEngine.CharacterFoundation(
                intelligence = 72.0,
                discipline = 65.0,
                willpower = 70.0,
                ambition = 80.0,
                health = 85.0,
                looks = 60.0,
                smarts = 75.0,
                happiness = 68.0
            ),
            momentum = FormulaEngine.Momentum(trajectoryScore = 0.25),
            influence = FormulaEngine.Influence(socialCapital = 40.0),
            worldVariables = FormulaEngine.WorldVariables(economicCycleIndex = 1.1)
        )
        _lastFormulaOutcome.value = testOutcome
    }
}
