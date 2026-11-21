package com.example.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ChickenFoodDatabase
import com.example.data.ChickenFoodEntity
import com.example.data.ChickenFoodRepository
import com.example.data.DataStoreDisclaimerStore
import com.example.data.DisclaimerStore
import com.example.data.FirebaseFoodLookupClient
import com.example.data.FoodCategory
import com.example.data.FoodDraft
import com.example.data.FoodLookupClient
import com.example.data.FoodLookupResult
import com.example.data.SafetyVerdict
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DisclaimerGateState {
    data object Loading : DisclaimerGateState
    data object NeedsAcceptance : DisclaimerGateState
    data object Accepted : DisclaimerGateState
}

sealed interface FoodLookupUiState {
    data object Idle : FoodLookupUiState
    data class Loading(val query: String) : FoodLookupUiState
    data class Ready(
        val draft: FoodDraft,
        val existingMatch: ChickenFoodEntity? = null,
        val isFromAiLookup: Boolean = false
    ) : FoodLookupUiState
    data class Error(
        val message: String,
        val fallbackDraft: FoodDraft? = null
    ) : FoodLookupUiState
}

data class FoodGuideUiState(
    val isSeeding: Boolean = true,
    val query: String = "",
    val selectedVerdict: SafetyVerdict? = null,
    val selectedCategory: FoodCategory? = null,
    val foods: List<ChickenFoodEntity> = emptyList(),
    val totalCatalogCount: Int = 0
)

class CluckCheckViewModel(
    private val repository: ChickenFoodRepository,
    private val disclaimerStore: DisclaimerStore,
    private val foodLookupClient: FoodLookupClient = FirebaseFoodLookupClient()
) : ViewModel() {

    val disclaimerState: StateFlow<DisclaimerGateState> = disclaimerStore.isDisclaimerAccepted
        .map { accepted ->
            if (accepted) DisclaimerGateState.Accepted else DisclaimerGateState.NeedsAcceptance
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DisclaimerGateState.Loading
        )

    private val _isSeeding = MutableStateFlow(true)
    private val _query = MutableStateFlow("")
    private val _selectedVerdict = MutableStateFlow<SafetyVerdict?>(null)
    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)
    private val _savedVerdictFilter = MutableStateFlow<SafetyVerdict?>(null)

    private val _lookupQuery = MutableStateFlow("")
    val lookupQuery: StateFlow<String> = _lookupQuery.asStateFlow()

    private val _lookupUiState = MutableStateFlow<FoodLookupUiState>(FoodLookupUiState.Idle)
    val lookupUiState: StateFlow<FoodLookupUiState> = _lookupUiState.asStateFlow()

    val savedVerdictFilter: StateFlow<SafetyVerdict?> = _savedVerdictFilter

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            _isSeeding.value = false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val filteredFoodsFlow: Flow<List<ChickenFoodEntity>> =
        combine(_query, _selectedVerdict, _selectedCategory) { query, verdict, category ->
            Triple(query, verdict, category)
        }.flatMapLatest { (query, verdict, category) ->
            repository.observeFoods(query = query, verdict = verdict, category = category)
        }

    private val allFoodsFlow: Flow<List<ChickenFoodEntity>> =
        repository.observeFoods(query = "", verdict = null, category = null)

    val foodGuideUiState: StateFlow<FoodGuideUiState> = combine(
        _isSeeding,
        _query,
        _selectedVerdict,
        _selectedCategory,
        filteredFoodsFlow,
        allFoodsFlow
    ) { values ->
        val seeding = values[0] as Boolean
        val query = values[1] as String
        val verdict = values[2] as SafetyVerdict?
        val category = values[3] as FoodCategory?
        @Suppress("UNCHECKED_CAST")
        val filtered = values[4] as List<ChickenFoodEntity>
        @Suppress("UNCHECKED_CAST")
        val all = values[5] as List<ChickenFoodEntity>

        FoodGuideUiState(
            isSeeding = seeding && all.isEmpty(),
            query = query,
            selectedVerdict = verdict,
            selectedCategory = category,
            foods = filtered,
            totalCatalogCount = all.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FoodGuideUiState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val savedFoods: StateFlow<List<ChickenFoodEntity>> = _savedVerdictFilter
        .flatMapLatest { verdict -> repository.observeFavorites(verdict) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun observeFoodDetail(id: Int): Flow<ChickenFoodEntity?> = repository.observeFoodById(id)

    fun updateSearchQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun selectVerdict(verdict: SafetyVerdict?) {
        _selectedVerdict.value = if (_selectedVerdict.value == verdict) null else verdict
    }

    fun selectCategory(category: FoodCategory?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun clearFilters() {
        _query.value = ""
        _selectedVerdict.value = null
        _selectedCategory.value = null
    }

    fun selectSavedVerdict(verdict: SafetyVerdict?) {
        _savedVerdictFilter.value = if (_savedVerdictFilter.value == verdict) null else verdict
    }

    fun toggleFavorite(id: Int, currentlyFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id = id, currentlyFavorite = currentlyFavorite)
        }
    }

    fun saveUserNote(id: Int, note: String) {
        viewModelScope.launch {
            repository.saveUserNote(id = id, note = note)
        }
    }

    fun acceptDisclaimer() {
        viewModelScope.launch {
            disclaimerStore.acceptDisclaimer()
        }
    }

    fun prepareFoodLookup(initialQuery: String) {
        val trimmed = initialQuery.trim()
        _lookupQuery.value = trimmed
        if (trimmed.isNotBlank()) {
            lookupFood(trimmed)
        } else {
            _lookupUiState.value = FoodLookupUiState.Idle
        }
    }

    fun updateLookupQuery(query: String) {
        _lookupQuery.value = query
    }

    fun lookupFood(queryOverride: String? = null) {
        val target = (queryOverride ?: _lookupQuery.value).trim()
        if (target.isBlank()) {
            _lookupUiState.value = FoodLookupUiState.Error("Enter a food, scrap, or plant name first.")
            return
        }
        _lookupQuery.value = target
        viewModelScope.launch {
            _lookupUiState.value = FoodLookupUiState.Loading(target)
            val existing = repository.findExistingByName(target)
            if (existing != null) {
                _lookupUiState.value = FoodLookupUiState.Ready(
                    draft = FoodDraft(
                        name = existing.name,
                        aliases = existing.aliases,
                        emoji = existing.emoji,
                        category = existing.category,
                        verdict = existing.verdict,
                        shortVerdict = existing.shortVerdict,
                        whyDetails = existing.whyDetails,
                        preparationSteps = existing.preparationSteps,
                        servingGuidance = existing.servingGuidance,
                        partsToAvoid = existing.partsToAvoid,
                        nutritionalValue = existing.nutritionalValue,
                        macroHighlights = existing.macroHighlights,
                        excessWarning = existing.excessWarning
                    ),
                    existingMatch = existing,
                    isFromAiLookup = false
                )
                return@launch
            }

            when (val result = foodLookupClient.lookupFood(target)) {
                is FoodLookupResult.Success -> {
                    val existingAfterLookup = repository.findExistingByName(result.draft.name)
                    _lookupUiState.value = FoodLookupUiState.Ready(
                        draft = result.draft,
                        existingMatch = existingAfterLookup,
                        isFromAiLookup = true
                    )
                }
                is FoodLookupResult.Error -> {
                    _lookupUiState.value = FoodLookupUiState.Error(
                        message = result.message,
                        fallbackDraft = FoodDraft(
                            name = target.replaceFirstChar { it.uppercase() }
                        )
                    )
                }
            }
        }
    }

    fun startManualDraft(initialName: String = _lookupQuery.value) {
        val cleaned = initialName.trim().replaceFirstChar { it.uppercase() }
        viewModelScope.launch {
            val existing = if (cleaned.isNotBlank()) repository.findExistingByName(cleaned) else null
            _lookupUiState.value = FoodLookupUiState.Ready(
                draft = FoodDraft(name = cleaned),
                existingMatch = existing,
                isFromAiLookup = false
            )
        }
    }

    fun updateDraft(transform: (FoodDraft) -> FoodDraft) {
        val current = _lookupUiState.value
        if (current is FoodLookupUiState.Ready) {
            _lookupUiState.value = current.copy(draft = transform(current.draft))
        }
    }

    fun saveDraftToDatabase(
        saveAsFavorite: Boolean,
        userNote: String,
        onSaved: (ChickenFoodEntity) -> Unit
    ) {
        val current = _lookupUiState.value
        if (current !is FoodLookupUiState.Ready) return
        if (current.draft.name.isBlank() || current.draft.shortVerdict.isBlank()) return

        viewModelScope.launch {
            val saved = repository.saveCustomFood(
                draft = current.draft,
                existingFood = current.existingMatch,
                saveAsFavorite = saveAsFavorite,
                userNote = userNote
            )
            clearFilters()
            _lookupQuery.value = ""
            _lookupUiState.value = FoodLookupUiState.Idle
            onSaved(saved)
        }
    }

    fun deleteCustomFood(id: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCustomFood(id)
            onDeleted()
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val dao = ChickenFoodDatabase.getInstance(application).chickenFoodDao()
            val repo = ChickenFoodRepository(dao)
            val disclaimerStore = DataStoreDisclaimerStore(application.applicationContext)
            return CluckCheckViewModel(repo, disclaimerStore) as T
        }
    }
}
