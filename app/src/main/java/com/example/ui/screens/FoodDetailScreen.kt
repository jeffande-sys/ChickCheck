package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChickenFoodEntity
import com.example.ui.components.VerdictBadge
import com.example.ui.components.verdictIcon
import com.example.ui.theme.LocalCluckSemanticColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FoodDetailScreen(
    food: ChickenFoodEntity?,
    onBack: () -> Unit,
    onToggleFavorite: (ChickenFoodEntity) -> Unit,
    onSaveNote: (Int, String) -> Unit,
    onDeleteCustomFood: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = food?.name ?: "Food Details",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                actions = {
                    if (food != null) {
                        if (food.id > 60 && onDeleteCustomFood != null) {
                            IconButton(
                                onClick = { onDeleteCustomFood(food.id) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("delete_custom_food_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = stringResource(R.string.delete_custom_food),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = { onToggleFavorite(food) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("detail_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (food.isFavorite) {
                                    Icons.Filled.Bookmark
                                } else {
                                    Icons.Outlined.BookmarkBorder
                                },
                                contentDescription = stringResource(
                                    if (food.isFavorite) R.string.favorite_remove else R.string.favorite_add
                                ),
                                tint = if (food.isFavorite) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (food == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val palette = LocalCluckSemanticColors.current.forVerdict(food.verdict)
        var noteText by remember(food.id) { mutableStateOf(food.userNote) }
        var noteSavedFeedback by remember(food.id) { mutableStateOf(false) }

        LaunchedEffect(food.userNote) {
            noteText = food.userNote
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hero Summary + Verdict Banner
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(palette.container),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = food.emoji, fontSize = 36.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = food.category.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = food.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (food.aliases.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Also searched as: ${food.aliases}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bold Verdict Banner
                        Surface(
                            color = palette.container,
                            contentColor = palette.content,
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = verdictIcon(food.verdict),
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = food.verdict.fullTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = food.shortVerdict,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // Why & What Happens
                DetailSectionCard(
                    title = stringResource(R.string.section_why),
                    icon = Icons.Filled.Info
                ) {
                    Text(
                        text = food.whyDetails,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Preparation & Feeding Guide
                DetailSectionCard(
                    title = stringResource(R.string.section_prep),
                    icon = Icons.Filled.Restaurant
                ) {
                    Text(
                        text = food.preparationSteps,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Serving & Frequency",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = food.servingGuidance,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = palette.container.copy(alpha = 0.55f),
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = stringResource(R.string.section_avoid_parts),
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.content
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = food.partsToAvoid,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Nutritional Value (Dedicated Required Section)
                DetailSectionCard(
                    title = stringResource(R.string.section_nutrition),
                    icon = Icons.Filled.Spa
                ) {
                    val highlights = food.macroHighlights
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    if (highlights.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            highlights.forEach { tag ->
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.LocalDining,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = food.nutritionalValue,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (food.excessWarning.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val warningPalette = LocalCluckSemanticColors.current.prepRequired
                        Surface(
                            color = warningPalette.container,
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, warningPalette.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ReportProblem,
                                    contentDescription = null,
                                    tint = warningPalette.content,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Quantity & Health Caution",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = warningPalette.content
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = food.excessWarning,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Personal Flock Note
                DetailSectionCard(
                    title = stringResource(R.string.section_flock_note),
                    icon = Icons.Filled.EditNote
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = {
                            noteText = it
                            noteSavedFeedback = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("flock_note_input"),
                        placeholder = {
                            Text(stringResource(R.string.note_placeholder))
                        },
                        minLines = 2,
                        maxLines = 4,
                        shape = MaterialTheme.shapes.small
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerdictBadge(verdict = food.verdict)
                        Button(
                            onClick = {
                                onSaveNote(food.id, noteText)
                                noteSavedFeedback = true
                            },
                            modifier = Modifier.testTag("save_note_button")
                        ) {
                            if (noteSavedFeedback) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Saved")
                            } else {
                                Text(stringResource(R.string.save_note))
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
private fun DetailSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
