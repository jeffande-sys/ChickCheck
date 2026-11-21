package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChickenFoodEntity
import com.example.data.FoodCategory
import com.example.data.FoodDraft
import com.example.data.SafetyVerdict
import com.example.ui.FoodLookupUiState
import com.example.ui.components.VerdictBadge
import com.example.ui.components.verdictGradientBrush
import com.example.ui.components.verdictIcon
import com.example.ui.theme.LocalCluckSemanticColors

private val SampleLookupFoods = listOf(
    "Mango",
    "Quinoa",
    "Dandelion",
    "Pomegranate",
    "Chia seeds"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddFoodScreen(
    initialQuery: String,
    lookupQuery: String,
    lookupUiState: FoodLookupUiState,
    onPrepareLookup: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onLookupClick: () -> Unit,
    onManualEntryClick: (String) -> Unit,
    onUpdateDraft: ((FoodDraft) -> FoodDraft) -> Unit,
    onSaveFood: (saveAsFavorite: Boolean, userNote: String) -> Unit,
    onViewExisting: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var saveAsFavorite by rememberSaveable { mutableStateOf(false) }
    var flockNote by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(initialQuery) {
        onPrepareLookup(initialQuery)
    }

    val backdropBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFCF9F2),
            MaterialTheme.colorScheme.background,
            Color(0xFFF2EBDC)
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_food_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("add_food_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFCF9F2)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backdropBrush)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_food_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Lookup Input Card
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = lookupQuery,
                            onValueChange = onQueryChange,
                            label = { Text(stringResource(R.string.lookup_input_label)) },
                            placeholder = { Text(stringResource(R.string.lookup_input_placeholder)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    onLookupClick()
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lookup_food_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    onLookupClick()
                                },
                                enabled = lookupQuery.isNotBlank() && lookupUiState !is FoodLookupUiState.Loading,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("lookup_food_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.lookup_action_button))
                            }

                            OutlinedButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    onManualEntryClick(lookupQuery)
                                },
                                enabled = lookupUiState !is FoodLookupUiState.Loading,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("manual_entry_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.enter_manually_button))
                            }
                        }

                        if (lookupUiState is FoodLookupUiState.Idle) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Try looking up:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SampleLookupFoods.forEach { sample ->
                                        SuggestionChip(
                                            onClick = {
                                                onQueryChange(sample)
                                                onLookupClick()
                                            },
                                            label = { Text(sample) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                when (lookupUiState) {
                    FoodLookupUiState.Idle -> Unit

                    is FoodLookupUiState.Loading -> {
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    text = "Evaluating \"${lookupUiState.query}\"…",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = stringResource(R.string.lookup_loading_message),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    is FoodLookupUiState.Error -> {
                        val dangerPalette = LocalCluckSemanticColors.current.dangerous
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = dangerPalette.container),
                            border = BorderStroke(1.dp, dangerPalette.border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lookup_error_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.ErrorOutline,
                                        contentDescription = null,
                                        tint = dangerPalette.content
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Lookup Could Not Complete",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = dangerPalette.content
                                    )
                                }
                                Text(
                                    text = lookupUiState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                OutlinedButton(
                                    onClick = {
                                        onManualEntryClick(
                                            lookupUiState.fallbackDraft?.name ?: lookupQuery
                                        )
                                    },
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text("Fill Out & Save Manually")
                                }
                            }
                        }
                    }

                    is FoodLookupUiState.Ready -> {
                        val draft = lookupUiState.draft
                        val existing = lookupUiState.existingMatch
                        val verdictPalette = LocalCluckSemanticColors.current.forVerdict(draft.verdict)

                        if (existing != null) {
                            ExistingFoodNoticeCard(
                                existing = existing,
                                onViewExisting = { onViewExisting(existing.id) }
                            )
                        }

                        // Preview Banner
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            border = BorderStroke(1.dp, verdictPalette.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = verdictPalette.container,
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = draft.emoji.ifBlank { "🌾" },
                                                fontSize = 30.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = draft.name.ifBlank { "New Flock Food" },
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        VerdictBadge(verdict = draft.verdict, useFullTitle = true)
                                    }
                                }

                                if (draft.shortVerdict.isNotBlank()) {
                                    Text(
                                        text = draft.shortVerdict,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Editable Details Card
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (lookupUiState.isFromAiLookup) {
                                        "Review & Edit Before Saving"
                                    } else {
                                        "Food Details"
                                    },
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = draft.name,
                                        onValueChange = { value ->
                                            onUpdateDraft { it.copy(name = value) }
                                        },
                                        label = { Text("Food Name *") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("food_draft_name_input")
                                    )
                                    OutlinedTextField(
                                        value = draft.emoji,
                                        onValueChange = { value ->
                                            onUpdateDraft { it.copy(emoji = value) }
                                        },
                                        label = { Text("Icon") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .width(86.dp)
                                            .testTag("food_draft_emoji_input")
                                    )
                                }

                                Text(
                                    text = "Safety Verdict",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SafetyVerdict.entries.forEach { verdict ->
                                        val palette = LocalCluckSemanticColors.current.forVerdict(verdict)
                                        val selected = draft.verdict == verdict
                                        Surface(
                                            onClick = { onUpdateDraft { it.copy(verdict = verdict) } },
                                            shape = RoundedCornerShape(50),
                                            color = if (selected) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
                                            border = BorderStroke(
                                                width = if (selected) 2.dp else 1.dp,
                                                color = if (selected) MaterialTheme.colorScheme.onBackground else palette.border
                                            ),
                                            modifier = Modifier.testTag("draft_verdict_${verdict.name}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .then(
                                                        if (selected) {
                                                            Modifier.background(verdictGradientBrush(palette.solid))
                                                        } else {
                                                            Modifier
                                                        }
                                                    )
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                                            ) {
                                                Icon(
                                                    imageVector = verdictIcon(verdict),
                                                    contentDescription = null,
                                                    tint = if (selected) Color.White else palette.content,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = verdict.shortLabel.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (selected) Color.White else palette.content
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = "Category",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FoodCategory.entries.forEach { category ->
                                        FilterChip(
                                            selected = draft.category == category,
                                            onClick = { onUpdateDraft { it.copy(category = category) } },
                                            label = { Text(category.label) }
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = draft.shortVerdict,
                                    onValueChange = { value ->
                                        onUpdateDraft { it.copy(shortVerdict = value) }
                                    },
                                    label = { Text("Short Summary *") },
                                    placeholder = { Text("e.g., Wholesome treat when pitted and peeled") },
                                    maxLines = 2,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("food_draft_short_verdict_input")
                                )

                                OutlinedTextField(
                                    value = draft.whyDetails,
                                    onValueChange = { value ->
                                        onUpdateDraft { it.copy(whyDetails = value) }
                                    },
                                    label = { Text("Why & What Happens") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = draft.preparationSteps,
                                    onValueChange = { value ->
                                        onUpdateDraft { it.copy(preparationSteps = value) }
                                    },
                                    label = { Text("Preparation & Feeding Steps") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = draft.servingGuidance,
                                    onValueChange = { value ->
                                        onUpdateDraft { it.copy(servingGuidance = value) }
                                    },
                                    label = { Text("Serving Guidance") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = draft.partsToAvoid,
                                    onValueChange = { value ->
                                        onUpdateDraft { it.copy(partsToAvoid = value) }
                                    },
                                    label = { Text("Parts or Forms to Avoid") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = draft.nutritionalValue,
                                    onValueChange = { value ->
                                        onUpdateDraft { it.copy(nutritionalValue = value) }
                                    },
                                    label = { Text("Nutritional Value") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = flockNote,
                                    onValueChange = { flockNote = it },
                                    label = { Text("Personal Flock Note (Optional)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = saveAsFavorite,
                                        onCheckedChange = { saveAsFavorite = it }
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.BookmarkAdd,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Also bookmark in Saved",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                val canSave = draft.name.isNotBlank() && draft.shortVerdict.isNotBlank()
                                Button(
                                    onClick = { onSaveFood(saveAsFavorite, flockNote) },
                                    enabled = canSave,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 52.dp)
                                        .testTag("save_food_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (existing != null) {
                                            stringResource(R.string.update_in_guide_button)
                                        } else {
                                            stringResource(R.string.save_to_guide_button)
                                        },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ExistingFoodNoticeCard(
    existing: ChickenFoodEntity,
    onViewExisting: () -> Unit
) {
    val palette = LocalCluckSemanticColors.current.forVerdict(existing.verdict)
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = palette.container),
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.duplicate_food_notice),
                style = MaterialTheme.typography.bodySmall,
                color = palette.content,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = onViewExisting) {
                Text("View")
            }
        }
    }
}
