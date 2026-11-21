package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CoopRulesScreen(modifier: Modifier = Modifier) {
    var flockSize by remember { mutableFloatStateOf(6f) }
    val hens = flockSize.roundToInt().coerceIn(1, 25)
    // Standard laying hen eats ~0.5 cup (120g) of balanced feed daily; 10% treat rule ≈ 1.5 tbsp per hen
    val treatTablespoons = (hens * 1.5f).roundToInt()
    val treatCupsFormatted = String.format("%.1f", treatTablespoons / 16f)
    val feedCupsFormatted = String.format("%.1f", hens * 0.5f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column {
                Text(
                    text = "Coop Feeding Rules",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Practical kitchen-scrap guidelines to protect egg flavor, shell strength, and flock digestion.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Interactive 90/10 Flock Treat Calculator
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Balance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily 90/10 Treat Calculator",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "90% of a hen's diet should be balanced layer/grower feed. Keep scraps and treats within 10%.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Flock Size",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "$hens ${if (hens == 1) "chicken" else "chickens"}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Slider(
                        value = flockSize,
                        onValueChange = { flockSize = it },
                        valueRange = 1f..25f,
                        steps = 23,
                        modifier = Modifier.testTag("flock_size_slider")
                    )

                    // Ratio visual bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(9f)
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "90% Complete Feed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "~$feedCupsFormatted cups / day",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "10% Max Treats",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$treatTablespoons tbsp (~$treatCupsFormatted c)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            RuleGuideCard(
                icon = Icons.Filled.Egg,
                title = "How Food Affects Egg Flavor & Yolk Color",
                points = listOf(
                    "Avoid onions, heavy garlic, asparagus, and strong oily fish: sulfur and fatty compounds transfer into developing yolks and taint taste.",
                    "Bright yolks come from carotenoids in kale, dandelion greens, pumpkin, sweet potato, red peppers, and small corn treats.",
                    "Never feed raw wet eggshells — bake and crush them finely so hens don't learn to break fresh eggs."
                )
            )

            RuleGuideCard(
                icon = Icons.Filled.WarningAmber,
                title = "Common Preparation Mistakes",
                points = listOf(
                    "Dried beans: Always soak and boil vigorously for 20–30 minutes; raw kidney beans contain lethal phytohemagglutinin.",
                    "Potatoes: Peel away all green skins and eyes and cook until soft to neutralize solanine.",
                    "Mower clippings: Long cut grass strands tangle in the crop and cause impaction; let hens graze live grass instead.",
                    "Moldy leftovers: Visible mold carries mycotoxins and botulism — never use chickens as a compost bin for spoiled food."
                )
            )

            RuleGuideCard(
                icon = Icons.Filled.Grain,
                title = "Insoluble Grit vs. Oyster Shell",
                points = listOf(
                    "Insoluble Grit (granite pebbles): Acts as the chicken's 'teeth' inside the gizzard to grind grains, seeds, and fibrous veggies.",
                    "Calcium (crushed oyster shell or baked eggshells): Dissolves in digestion to build sturdy eggshells. Offer free-choice in a separate dish for laying hens only."
                )
            )

            RuleGuideCard(
                icon = Icons.Filled.LocalDining,
                title = "Protein vs. Carbohydrate Balance",
                points = listOf(
                    "Laying hens need ~16–18% protein daily. Filling up on starchy rice, bread, pasta, or corn dilutes protein and drops egg production.",
                    "High-fat treats like mealworms and sunflower seeds are wonderful during autumn molt, but excess amounts cause fatty liver syndrome."
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuleGuideCard(
    icon: ImageVector,
    title: String,
    points: List<String>
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
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            points.forEachIndexed { index, point ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (index != points.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
