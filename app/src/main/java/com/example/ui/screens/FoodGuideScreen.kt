package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChickenFoodEntity
import com.example.data.FoodCategory
import com.example.data.SafetyVerdict
import com.example.ui.FoodGuideUiState
import com.example.ui.components.FoodListCard
import com.example.ui.components.quickCheckIcon
import com.example.ui.theme.LocalCluckSemanticColors
import com.example.ui.theme.PlusJakartaSansFontFamily

@Composable
fun FoodGuideScreen(
    uiState: FoodGuideUiState,
    onQueryChange: (String) -> Unit,
    onVerdictSelect: (SafetyVerdict?) -> Unit,
    onCategorySelect: (FoodCategory?) -> Unit,
    onClearFilters: () -> Unit,
    onFoodClick: (Int) -> Unit,
    onToggleFavorite: (ChickenFoodEntity) -> Unit,
    onOpenLookupFood: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showCategoryFilters by rememberSaveable { mutableStateOf(false) }
    val hasActiveFilter = uiState.query.isNotBlank() ||
        uiState.selectedVerdict != null ||
        uiState.selectedCategory != null

    val screenBackdropBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFCF9F2),
            MaterialTheme.colorScheme.background,
            Color(0xFFF2EBDC)
        )
    )

    if (uiState.isSeeding) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(screenBackdropBrush),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackdropBrush)
    ) {
        val columns = if (maxWidth >= 720.dp) 2 else 1
        val isCompactWidth = maxWidth < 385.dp

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Illustrated Hen (framed with soft depth) + Bold CHICK / lighter Check Header
            item(span = { GridItemSpan(columns) }) {
                ChickCheckBrandHeader(isCompactWidth = isCompactWidth)
            }

            // Elevated Search Field + Category Filter Toggle
            item(span = { GridItemSpan(columns) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.query,
                            onValueChange = onQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("food_search_input"),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search_placeholder),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (uiState.query.isNotEmpty()) {
                                        IconButton(
                                            onClick = { onQueryChange("") },
                                            modifier = Modifier.testTag("clear_search_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = stringResource(R.string.clear_search)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { showCategoryFilters = !showCategoryFilters },
                                        modifier = Modifier.testTag("category_filter_button")
                                    ) {
                                        Box {
                                            Icon(
                                                imageVector = Icons.Filled.Tune,
                                                contentDescription = stringResource(R.string.filter_categories),
                                                tint = if (showCategoryFilters || uiState.selectedCategory != null) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                            if (uiState.selectedCategory != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary)
                                                        .align(Alignment.TopEnd)
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                        )
                    }

                    AnimatedVisibility(visible = showCategoryFilters || uiState.selectedCategory != null) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = uiState.selectedCategory == null,
                                    onClick = { onCategorySelect(null) },
                                    label = { Text("All Categories") }
                                )
                            }
                            items(FoodCategory.entries) { category ->
                                FilterChip(
                                    selected = uiState.selectedCategory == category,
                                    onClick = { onCategorySelect(category) },
                                    label = { Text(category.label) }
                                )
                            }
                        }
                    }
                }
            }

            // QUICK CHECKS Legend Cards (tap to filter by verdict)
            item(span = { GridItemSpan(columns) }) {
                QuickChecksSection(
                    selectedVerdict = uiState.selectedVerdict,
                    onSelectVerdict = onVerdictSelect
                )
            }

            // FOOD LIST Header Row
            item(span = { GridItemSpan(columns) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.food_list_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasActiveFilter) {
                            TextButton(
                                onClick = onClearFilters,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.reset_filters),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                        Text(
                            text = "${uiState.foods.size} of ${uiState.totalCatalogCount} shown",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (uiState.foods.isEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    EmptySearchState(
                        query = uiState.query,
                        onLookupQuery = { onOpenLookupFood(uiState.query) },
                        onReset = onClearFilters
                    )
                }
            } else {
                items(
                    items = uiState.foods,
                    key = { it.id }
                ) { food ->
                    FoodListCard(
                        food = food,
                        onClick = { onFoodClick(food.id) },
                        onToggleFavorite = { onToggleFavorite(food) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChickCheckBrandHeader(isCompactWidth: Boolean) {
    val primaryGreen = MaterialTheme.colorScheme.primary
    val secondaryGreen = lerp(primaryGreen, Color.White, 0.16f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 5.dp,
            border = BorderStroke(2.dp, Color.White),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(if (isCompactWidth) 94.dp else 108.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_chickcheck_hen),
                contentDescription = stringResource(R.string.hero_hen_cd),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = primaryGreen,
                            letterSpacing = (-0.8).sp
                        )
                    ) {
                        append("CHICK")
                    }
                    withStyle(
                        SpanStyle(
                            fontFamily = PlusJakartaSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = secondaryGreen,
                            letterSpacing = (-0.4).sp
                        )
                    ) {
                        append("Check")
                    }
                },
                fontSize = if (isCompactWidth) 31.sp else 36.sp,
                lineHeight = if (isCompactWidth) 34.sp else 39.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.app_tagline).uppercase(),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = stringResource(R.string.hero_slogan),
                fontSize = 11.5.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(primaryGreen, lerp(primaryGreen, Color(0xFF4CA855), 0.5f))
                        )
                    )
            )
        }
    }
}

@Composable
private fun QuickChecksSection(
    selectedVerdict: SafetyVerdict?,
    onSelectVerdict: (SafetyVerdict?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.quick_checks_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth < 340.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickCheckCard(
                            verdict = SafetyVerdict.SAFE,
                            description = stringResource(R.string.quick_check_safe_desc),
                            selected = selectedVerdict == SafetyVerdict.SAFE,
                            onClick = { onSelectVerdict(SafetyVerdict.SAFE) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        QuickCheckCard(
                            verdict = SafetyVerdict.PREP_REQUIRED,
                            description = stringResource(R.string.quick_check_prep_desc),
                            selected = selectedVerdict == SafetyVerdict.PREP_REQUIRED,
                            onClick = { onSelectVerdict(SafetyVerdict.PREP_REQUIRED) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickCheckCard(
                            verdict = SafetyVerdict.AVOID,
                            description = stringResource(R.string.quick_check_avoid_desc),
                            selected = selectedVerdict == SafetyVerdict.AVOID,
                            onClick = { onSelectVerdict(SafetyVerdict.AVOID) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        QuickCheckCard(
                            verdict = SafetyVerdict.DANGEROUS,
                            description = stringResource(R.string.quick_check_danger_desc),
                            selected = selectedVerdict == SafetyVerdict.DANGEROUS,
                            onClick = { onSelectVerdict(SafetyVerdict.DANGEROUS) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val descriptions = mapOf(
                        SafetyVerdict.SAFE to stringResource(R.string.quick_check_safe_desc),
                        SafetyVerdict.PREP_REQUIRED to stringResource(R.string.quick_check_prep_desc),
                        SafetyVerdict.AVOID to stringResource(R.string.quick_check_avoid_desc),
                        SafetyVerdict.DANGEROUS to stringResource(R.string.quick_check_danger_desc)
                    )
                    SafetyVerdict.entries.forEach { verdict ->
                        QuickCheckCard(
                            verdict = verdict,
                            description = descriptions.getValue(verdict),
                            selected = selectedVerdict == verdict,
                            onClick = { onSelectVerdict(verdict) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickCheckCard(
    verdict: SafetyVerdict,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalCluckSemanticColors.current.forVerdict(verdict)
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            if (selected) palette.container else Color.White,
            if (selected) lerp(palette.container, palette.border, 0.25f) else palette.container
        )
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        shadowElevation = if (selected) 5.dp else 2.dp,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) palette.solid else palette.border),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("verdict_chip_${verdict.name}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (verdict == SafetyVerdict.AVOID) {
                Surface(
                    shape = CircleShape,
                    color = palette.solid,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = quickCheckIcon(verdict),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Surface(
                    shape = CircleShape,
                    color = palette.solid.copy(alpha = 0.12f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = quickCheckIcon(verdict),
                            contentDescription = null,
                            tint = palette.solid,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = verdict.shortLabel.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.content,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = description,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptySearchState(
    query: String,
    onLookupQuery: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.empty_search_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.empty_search_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (query.isNotBlank()) {
                Button(
                    onClick = onLookupQuery,
                    modifier = Modifier.testTag("empty_lookup_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.empty_lookup_button, query.trim()))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onReset) {
                    Text(stringResource(R.string.reset_filters))
                }
            } else {
                Button(onClick = onReset) {
                    Text(stringResource(R.string.reset_filters))
                }
            }
        }
    }
}
