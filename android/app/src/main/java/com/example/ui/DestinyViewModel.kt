package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DestinyDatabase
import com.example.data.local.entity.LifeEntity
import com.example.data.repository.DestinyRepository
import com.example.data.repository.LifeRecordSummary
import com.example.services.AgingService
import com.example.services.DeathService
import com.example.services.FormulaEngine
import com.example.services.GameClock
import com.example.services.LifeService
import com.example.ui.screens.CharacterCreationParams
import com.example.ui.screens.SimulationLogItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DestinyScreen {
    data object Landing : DestinyScreen
    data object CharacterCreation : DestinyScreen
    data class CharacterProfile(val lifeId: String) : DestinyScreen
    data class Relationships(val lifeId: String) : DestinyScreen
    data class FamilyTree(val lifeId: String) : DestinyScreen
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
    val familyService = com.example.services.FamilyService(repository)
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

    // --- Prompt 02 Character Creation & Profile State ---
    private val _isSubmittingCharacter = MutableStateFlow(false)
    val isSubmittingCharacter: StateFlow<Boolean> = _isSubmittingCharacter.asStateFlow()

    private val _activeProfileSummary = MutableStateFlow<LifeRecordSummary?>(null)
    val activeProfileSummary: StateFlow<LifeRecordSummary?> = _activeProfileSummary.asStateFlow()

    private val _deathCheckResult = MutableStateFlow<DeathService.DeathCheckResult?>(null)
    val deathCheckResult: StateFlow<DeathService.DeathCheckResult?> = _deathCheckResult.asStateFlow()

    private val _simLogs = MutableStateFlow<List<SimulationLogItem>>(emptyList())
    val simLogs: StateFlow<List<SimulationLogItem>> = _simLogs.asStateFlow()

    private val _isAging = MutableStateFlow(false)
    val isAging: StateFlow<Boolean> = _isAging.asStateFlow()

    // --- Prompt 03 Family System State ---
    private val _relationships = MutableStateFlow<List<com.example.data.model.Relationship>>(emptyList())
    val relationships: StateFlow<List<com.example.data.model.Relationship>> = _relationships.asStateFlow()

    private val _familyEvents = MutableStateFlow<List<com.example.data.model.FamilyEvent>>(emptyList())
    val familyEvents: StateFlow<List<com.example.data.model.FamilyEvent>> = _familyEvents.asStateFlow()

    private val _isFamilyActionLoading = MutableStateFlow(false)
    val isFamilyActionLoading: StateFlow<Boolean> = _isFamilyActionLoading.asStateFlow()

    private val _familyActionMessage = MutableStateFlow<String?>(null)
    val familyActionMessage: StateFlow<String?> = _familyActionMessage.asStateFlow()

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

    // --- Prompt 02 Character Creation & Aging Methods ---

    fun openCharacterProfile(lifeId: String) {
        viewModelScope.launch {
            val summary = lifeService.getLifeDetails(lifeId)
            _activeProfileSummary.value = summary
            _deathCheckResult.value = null
            _simLogs.value = listOf(
                SimulationLogItem(
                    id = "init",
                    year = summary.life.birthYear,
                    age = summary.life.currentAge,
                    narrative = "Born in ${summary.clientCharacter.birthCity}, ${summary.clientCharacter.birthCountry}. Innate talent: ${summary.clientCharacter.talent}."
                )
            )
            _currentScreen.value = DestinyScreen.CharacterProfile(lifeId)
        }
    }

    fun createFullCharacter(params: CharacterCreationParams) {
        _isSubmittingCharacter.value = true
        viewModelScope.launch {
            try {
                val fullName = "${params.firstName.trim()} ${params.lastName.trim()}".trim()
                val record = lifeService.createNewLife(
                    playerName = "Player_${params.firstName}",
                    saveSlotName = "Slot $fullName",
                    characterName = fullName,
                    birthYear = 2000,
                    isDormant = false,
                    birthCountry = params.country,
                    birthCity = params.city,
                    gender = params.gender,
                    sexuality = params.sexuality,
                    talent = params.talent,
                    eyeStyle = params.eyeStyle,
                    eyeColor = params.eyeColor,
                    skinTone = params.skinTone,
                    browStyle = params.browStyle,
                    facialHairStyle = params.facialHairStyle,
                    facialHairColor = params.facialHairColor,
                    hairStyle = params.hairStyle,
                    hairColor = params.hairColor,
                    intelligence = params.intelligence,
                    discipline = params.discipline,
                    willpower = params.willpower,
                    ambition = params.ambition,
                    health = params.health,
                    looks = params.looks,
                    smarts = params.smarts,
                    happiness = params.happiness,
                    fertility = params.fertility,
                    energy = params.energy,
                    athleticPerformance = params.athleticPerformance
                )

                _activeProfileSummary.value = record
                _deathCheckResult.value = null
                _simLogs.value = listOf(
                    SimulationLogItem(
                        id = "init-${System.currentTimeMillis()}",
                        year = record.life.birthYear,
                        age = 0,
                        narrative = "Born in ${record.clientCharacter.birthCity}, ${record.clientCharacter.birthCountry}. Innate talent: ${record.clientCharacter.talent}."
                    )
                )
                _isSubmittingCharacter.value = false
                _currentScreen.value = DestinyScreen.CharacterProfile(record.life.id)
            } catch (e: Exception) {
                _isSubmittingCharacter.value = false
            }
        }
    }

    fun ageActiveCharacter() {
        val currentSummary = _activeProfileSummary.value ?: return
        if (_deathCheckResult.value?.isDead == true || _isAging.value) return

        _isAging.value = true
        viewModelScope.launch {
            try {
                val life = currentSummary.life
                val character = currentSummary.clientCharacter
                val nextAge = life.currentAge + 1
                val simYear = life.birthYear + nextAge

                // 1. Run AgingService
                val agingResult = AgingService.ageOneYear(character, life.currentAge)

                // 2. Run DeathService
                val deathResult = DeathService.checkMortality(
                    age = nextAge,
                    health = agingResult.newStats.health,
                    character = character
                )

                // 3. Update DB
                lifeService.updateLifeAge(life.id, nextAge)
                lifeService.updateCharacterStats(
                    characterId = character.id,
                    health = agingResult.newStats.health,
                    fertility = agingResult.newStats.fertility,
                    energy = agingResult.newStats.energy,
                    athleticPerformance = agingResult.newStats.athleticPerformance
                )

                val updatedSummary = lifeService.getLifeDetails(life.id)
                _activeProfileSummary.value = updatedSummary
                _deathCheckResult.value = deathResult

                val newLog = SimulationLogItem(
                    id = "$simYear-${System.currentTimeMillis()}",
                    year = simYear,
                    age = nextAge,
                    narrative = if (deathResult.isDead) "DECEASED at age $nextAge: ${deathResult.cause}" else agingResult.narrative,
                    isFatal = deathResult.isDead
                )
                _simLogs.value = listOf(newLog) + _simLogs.value

                // If fatal, run inheritance hook automatically!
                if (deathResult.isDead) {
                    try {
                        val inheritanceResult = familyService.executeInheritance(character.id, simYear)
                        val inheritanceLog = SimulationLogItem(
                            id = "inherit-${System.currentTimeMillis()}",
                            year = simYear,
                            age = nextAge,
                            narrative = inheritanceResult.description
                        )
                        _simLogs.value = listOf(inheritanceLog) + _simLogs.value
                    } catch (_: Exception) {}
                }
            } finally {
                _isAging.value = false
            }
        }
    }

    // --- Prompt 03 Family System Methods ---

    fun loadFamilyData(characterId: String, currentYear: Int = 2026) {
        viewModelScope.launch {
            _isFamilyActionLoading.value = true
            try {
                var currentRels = familyService.getRelationships(characterId)
                if (currentRels.isEmpty()) {
                    // Seed initial family circle (Parents & Friend)
                    seedInitialFamily(characterId, currentYear)
                    currentRels = familyService.getRelationships(characterId)
                }
                _relationships.value = currentRels
                _familyEvents.value = familyService.getFamilyEvents(characterId)
            } finally {
                _isFamilyActionLoading.value = false
            }
        }
    }

    private suspend fun seedInitialFamily(characterId: String, currentYear: Int) {
        val mainChar = repository.getCharacterById(characterId) ?: return

        // 1. Father (Dormant NPC)
        val fatherSummary = repository.createNewLife(
            playerName = "Player_Dad",
            saveSlotName = "FamilySlot",
            characterName = "Arthur ${mainChar.birthCountry.take(4)}",
            birthYear = currentYear - 48,
            isDormant = true,
            birthCountry = mainChar.birthCountry,
            birthCity = mainChar.birthCity,
            gender = "Male",
            health = 75,
            intelligence = 70,
            looks = 65
        )
        repository.saveRelationship(
            characterId = characterId,
            relatedCharacterId = fatherSummary.clientCharacter.id,
            type = "Parent",
            relationshipStrength = 90,
            status = "Active",
            startedAt = currentYear - 20
        )
        repository.saveRelationship(
            characterId = fatherSummary.clientCharacter.id,
            relatedCharacterId = characterId,
            type = "Child",
            relationshipStrength = 95,
            status = "Active",
            startedAt = currentYear - 20
        )

        // 2. Mother (Dormant NPC)
        val motherSummary = repository.createNewLife(
            playerName = "Player_Mom",
            saveSlotName = "FamilySlot",
            characterName = "Eleanor ${mainChar.birthCountry.take(4)}",
            birthYear = currentYear - 46,
            isDormant = true,
            birthCountry = mainChar.birthCountry,
            birthCity = mainChar.birthCity,
            gender = "Female",
            health = 80,
            intelligence = 78,
            looks = 70
        )
        repository.saveRelationship(
            characterId = characterId,
            relatedCharacterId = motherSummary.clientCharacter.id,
            type = "Parent",
            relationshipStrength = 92,
            status = "Active",
            startedAt = currentYear - 20
        )
        repository.saveRelationship(
            characterId = motherSummary.clientCharacter.id,
            relatedCharacterId = characterId,
            type = "Child",
            relationshipStrength = 98,
            status = "Active",
            startedAt = currentYear - 20
        )

        // 3. Close Friend / Potential Partner (Dormant NPC)
        val friendSummary = repository.createNewLife(
            playerName = "Player_Partner",
            saveSlotName = "FamilySlot",
            characterName = if (mainChar.gender == "Male") "Sophia Sterling" else "Liam Sterling",
            birthYear = currentYear - 22,
            isDormant = true, // Starts dormant; marriage promotes to active!
            birthCountry = mainChar.birthCountry,
            birthCity = mainChar.birthCity,
            gender = if (mainChar.gender == "Male") "Female" else "Male",
            health = 88,
            intelligence = 82,
            looks = 85
        )
        repository.saveRelationship(
            characterId = characterId,
            relatedCharacterId = friendSummary.clientCharacter.id,
            type = "BestFriend",
            relationshipStrength = 85,
            status = "Active",
            startedAt = currentYear - 5
        )

        repository.logFamilyEvent(
            characterId = characterId,
            relatedCharacterId = null,
            eventType = "FamilyCircle",
            gameYear = currentYear,
            description = "Established close family and social circle."
        )
    }

    fun marry(characterId: String, partnerCharacterId: String, gameYear: Int) {
        viewModelScope.launch {
            _isFamilyActionLoading.value = true
            try {
                val result = familyService.marry(characterId, partnerCharacterId, gameYear)
                _familyActionMessage.value = "✓ Marriage successful! ${if (result.promotedFromDormant) "Partner promoted from Dormant to Active." else ""}"
                loadFamilyData(characterId, gameYear)
                // Refresh character profile if currently active
                val life = _activeProfileSummary.value?.life
                if (life != null) {
                    _activeProfileSummary.value = lifeService.getLifeDetails(life.id)
                }
            } catch (e: Exception) {
                _familyActionMessage.value = "Error during marriage: ${e.localizedMessage}"
            } finally {
                _isFamilyActionLoading.value = false
            }
        }
    }

    fun divorce(characterId: String, partnerCharacterId: String, gameYear: Int) {
        viewModelScope.launch {
            _isFamilyActionLoading.value = true
            try {
                val result = familyService.divorce(characterId, partnerCharacterId, gameYear)
                _familyActionMessage.value = "✓ Divorce finalized. Assets split 50/50 ($${result.characterBalance.toString()} each). Now recorded as Ex."
                loadFamilyData(characterId, gameYear)
                // Refresh profile balance
                val life = _activeProfileSummary.value?.life
                if (life != null) {
                    _activeProfileSummary.value = lifeService.getLifeDetails(life.id)
                }
            } catch (e: Exception) {
                _familyActionMessage.value = "Error during divorce: ${e.localizedMessage}"
            } finally {
                _isFamilyActionLoading.value = false
            }
        }
    }

    fun haveChild(parent1Id: String, parent2Id: String, childName: String, gameYear: Int) {
        viewModelScope.launch {
            _isFamilyActionLoading.value = true
            try {
                val result = familyService.haveChild(parent1Id, parent2Id, childName, gameYear)
                _familyActionMessage.value = "👶 Welcomed ${result.child.id}! Genetic modifiers: Health (${result.child.geneticHealthModifier}), Int (${result.child.geneticIntelligenceModifier}), Looks (${result.child.geneticLooksModifier})."
                loadFamilyData(parent1Id, gameYear)
            } catch (e: Exception) {
                _familyActionMessage.value = "Error having child: ${e.localizedMessage}"
            } finally {
                _isFamilyActionLoading.value = false
            }
        }
    }

    fun addEnemy(characterId: String, name: String, gameYear: Int) {
        viewModelScope.launch {
            _isFamilyActionLoading.value = true
            try {
                val enemyChar = repository.createNewLife(
                    playerName = "Enemy_NPC",
                    saveSlotName = "EnemySlot",
                    characterName = name,
                    birthYear = gameYear - 25,
                    isDormant = true,
                    health = 70,
                    intelligence = 75
                )
                val rel = familyService.addEnemy(
                    characterId = characterId,
                    enemyCharacterId = enemyChar.clientCharacter.id,
                    gameYear = gameYear,
                    enemyInfluence = FormulaEngine.Influence(socialCapital = 65.0, familyLeverage = 60.0)
                )
                _familyActionMessage.value = "⚠️ Enemy added! Sabotage chance: ${rel.sabotageChance}% (calculated via FormulaEngine Influence layer)."
                loadFamilyData(characterId, gameYear)
            } catch (e: Exception) {
                _familyActionMessage.value = "Error adding enemy: ${e.localizedMessage}"
            } finally {
                _isFamilyActionLoading.value = false
            }
        }
    }

    fun triggerInheritance(deceasedCharacterId: String, gameYear: Int) {
        viewModelScope.launch {
            _isFamilyActionLoading.value = true
            try {
                val result = familyService.executeInheritance(deceasedCharacterId, gameYear)
                _familyActionMessage.value = "✓ Inheritance executed: ${result.description}"
                loadFamilyData(deceasedCharacterId, gameYear)
                val life = _activeProfileSummary.value?.life
                if (life != null) {
                    _activeProfileSummary.value = lifeService.getLifeDetails(life.id)
                }
            } catch (e: Exception) {
                _familyActionMessage.value = "Error executing inheritance: ${e.localizedMessage}"
            } finally {
                _isFamilyActionLoading.value = false
            }
        }
    }

    fun clearFamilyActionMessage() {
        _familyActionMessage.value = null
    }

    // --- Save Export / Import (Zero Backend) ---
    private val _backupStatusMessage = MutableStateFlow<String?>(null)
    val backupStatusMessage: StateFlow<String?> = _backupStatusMessage.asStateFlow()

    private val _exportedJson = MutableStateFlow<String?>(null)
    val exportedJson: StateFlow<String?> = _exportedJson.asStateFlow()

    private val _isBackupLoading = MutableStateFlow(false)
    val isBackupLoading: StateFlow<Boolean> = _isBackupLoading.asStateFlow()

    fun exportSaveData() {
        viewModelScope.launch {
            _isBackupLoading.value = true
            try {
                val json = repository.exportSaveBackup()
                _exportedJson.value = json
                _backupStatusMessage.value = "✓ Save exported successfully (${json.length} chars)"
            } catch (e: Exception) {
                _backupStatusMessage.value = "Export failed: ${e.localizedMessage}"
            } finally {
                _isBackupLoading.value = false
            }
        }
    }

    fun importSaveData(jsonString: String) {
        viewModelScope.launch {
            _isBackupLoading.value = true
            try {
                val result = repository.importSaveBackup(jsonString)
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 0
                    _backupStatusMessage.value = "✓ Save imported successfully! Restored $count entities."
                    _exportedJson.value = null
                } else {
                    _backupStatusMessage.value = "Import error: ${result.exceptionOrNull()?.localizedMessage}"
                }
            } catch (e: Exception) {
                _backupStatusMessage.value = "Import error: ${e.localizedMessage}"
            } finally {
                _isBackupLoading.value = false
            }
        }
    }

    fun clearBackupStatusMessage() {
        _backupStatusMessage.value = null
    }

    fun clearExportedJson() {
        _exportedJson.value = null
    }
}
