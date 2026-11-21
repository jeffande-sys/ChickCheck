package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChickenFoodEntity
import com.example.data.FoodCategory
import com.example.data.SafetyVerdict
import com.example.ui.theme.LocalCluckSemanticColors

fun verdictIcon(verdict: SafetyVerdict): ImageVector = when (verdict) {
    SafetyVerdict.SAFE -> Icons.Outlined.CheckCircle
    SafetyVerdict.PREP_REQUIRED -> Icons.Outlined.Assignment
    SafetyVerdict.AVOID -> Icons.Outlined.ErrorOutline
    SafetyVerdict.DANGEROUS -> Icons.Filled.Warning
}

fun verdictInlineIcon(verdict: SafetyVerdict): ImageVector = when (verdict) {
    SafetyVerdict.SAFE -> Icons.Filled.Eco
    SafetyVerdict.PREP_REQUIRED -> Icons.Outlined.Schedule
    SafetyVerdict.AVOID -> Icons.Filled.Eco
    SafetyVerdict.DANGEROUS -> Icons.Filled.Warning
}

fun verdictCardBadgeIcon(verdict: SafetyVerdict): ImageVector = when (verdict) {
    SafetyVerdict.SAFE -> Icons.Filled.Check
    SafetyVerdict.PREP_REQUIRED -> Icons.Outlined.Assignment
    SafetyVerdict.AVOID -> Icons.Outlined.ErrorOutline
    SafetyVerdict.DANGEROUS -> Icons.Filled.Warning
}

fun quickCheckIcon(verdict: SafetyVerdict): ImageVector = when (verdict) {
    SafetyVerdict.SAFE -> Icons.Filled.VerifiedUser
    SafetyVerdict.PREP_REQUIRED -> Icons.Outlined.Schedule
    SafetyVerdict.AVOID -> Icons.Filled.Eco
    SafetyVerdict.DANGEROUS -> Icons.Filled.Warning
}

fun verdictGradientBrush(baseColor: Color): Brush = Brush.verticalGradient(
    colors = listOf(
        lerp(baseColor, Color.White, 0.14f),
        baseColor,
        lerp(baseColor, Color.Black, 0.10f)
    )
)

@Composable
fun VerdictBadge(
    verdict: SafetyVerdict,
    modifier: Modifier = Modifier,
    useFullTitle: Boolean = false
) {
    val palette = LocalCluckSemanticColors.current.forVerdict(verdict)
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        contentColor = Color.White,
        shape = RoundedCornerShape(50),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .background(verdictGradientBrush(palette.solid))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = verdictIcon(verdict),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = if (useFullTitle) verdict.fullTitle else verdict.shortLabel.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun FoodListCard(
    food: ChickenFoodEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    showBookmarkButton: Boolean = false
) {
    val palette = LocalCluckSemanticColors.current.forVerdict(food.verdict)
    val thumbColors = categoryThumbnailGradient(food.category)

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("food_card_${food.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 1.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botanical tinted visual tile with soft inner highlight & elevation
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.5.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f)),
                modifier = Modifier
                    .width(82.dp)
                    .height(62.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.linearGradient(thumbColors))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.55f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = food.emoji,
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Icon(
                        imageVector = verdictInlineIcon(food.verdict),
                        contentDescription = null,
                        tint = palette.solid,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = food.shortVerdict,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (food.userNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Note: ${food.userNote}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (showBookmarkButton) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("favorite_toggle_${food.id}")
                ) {
                    Icon(
                        imageVector = if (food.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
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

            // Embossed gradient verdict badge with elevation
            Surface(
                color = Color.Transparent,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.5.dp,
                modifier = Modifier
                    .width(86.dp)
                    .border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .background(verdictGradientBrush(palette.solid))
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = food.verdict.shortLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = verdictCardBadgeIcon(food.verdict),
                        contentDescription = food.verdict.fullTitle,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun categoryThumbnailGradient(category: FoodCategory): List<Color> = when (category) {
    FoodCategory.FRUITS -> listOf(Color(0xFFFFF2ED), Color(0xFFFCDCD0))
    FoodCategory.VEGETABLES -> listOf(Color(0xFFECF8ED), Color(0xFFD2EDD5))
    FoodCategory.GRAINS_SEEDS -> listOf(Color(0xFFFFF7E5), Color(0xFFF9E6BE))
    FoodCategory.PROTEINS_DAIRY -> listOf(Color(0xFFF5F2FB), Color(0xFFE6DFF3))
    FoodCategory.HERBS_PLANTS -> listOf(Color(0xFFE9F6EE), Color(0xFFCCE8D6))
    FoodCategory.SCRAPS_PANTRY -> listOf(Color(0xFFF6F0E8), Color(0xFFE7DDD0))
}

@Composable
fun VerdictDot(
    verdict: SafetyVerdict,
    modifier: Modifier = Modifier
) {
    val palette = LocalCluckSemanticColors.current.forVerdict(verdict)
    Box(
        modifier = modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(palette.solid)
    )
}
