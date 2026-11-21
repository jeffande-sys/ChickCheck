package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.SafetyVerdict

@Immutable
data class VerdictPalette(
    val content: Color,
    val solid: Color,
    val container: Color,
    val border: Color
)

@Immutable
data class CluckSemanticColors(
    val safe: VerdictPalette,
    val prepRequired: VerdictPalette,
    val avoid: VerdictPalette,
    val dangerous: VerdictPalette
) {
    fun forVerdict(verdict: SafetyVerdict): VerdictPalette = when (verdict) {
        SafetyVerdict.SAFE -> safe
        SafetyVerdict.PREP_REQUIRED -> prepRequired
        SafetyVerdict.AVOID -> avoid
        SafetyVerdict.DANGEROUS -> dangerous
    }
}

private val LightSemanticColors = CluckSemanticColors(
    safe = VerdictPalette(SafeGreen, SafeGreenSolid, SafeGreenBg, SafeGreenBorder),
    prepRequired = VerdictPalette(PrepAmber, PrepAmberSolid, PrepAmberBg, PrepAmberBorder),
    avoid = VerdictPalette(AvoidBurntOrange, AvoidOrangeSolid, AvoidBurntOrangeBg, AvoidBurntOrangeBorder),
    dangerous = VerdictPalette(DangerCrimson, DangerRedSolid, DangerCrimsonBg, DangerCrimsonBorder)
)

private val DarkSemanticColors = CluckSemanticColors(
    safe = VerdictPalette(SafeGreenDark, SafeGreenSolid, SafeGreenBgDark, SafeGreenBorderDark),
    prepRequired = VerdictPalette(PrepAmberDark, PrepAmberSolid, PrepAmberBgDark, PrepAmberBorderDark),
    avoid = VerdictPalette(AvoidBurntOrangeDark, AvoidOrangeSolid, AvoidBurntOrangeBgDark, AvoidBurntOrangeBorderDark),
    dangerous = VerdictPalette(DangerCrimsonDark, DangerRedSolid, DangerCrimsonBgDark, DangerCrimsonBorderDark)
)

val LocalCluckSemanticColors = staticCompositionLocalOf { LightSemanticColors }

private val LightColorScheme = lightColorScheme(
    primary = CoopSage,
    onPrimary = Color.White,
    primaryContainer = CoopSageContainer,
    onPrimaryContainer = OnCoopSageContainer,
    secondary = HarvestGold,
    onSecondary = Color.White,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = OnHarvestGoldContainer,
    tertiary = TerracottaRed,
    onTertiary = Color.White,
    tertiaryContainer = TerracottaContainer,
    onTertiaryContainer = OnTerracottaContainer,
    background = ParchmentCream,
    onBackground = EspressoInk,
    surface = WarmCardSurface,
    onSurface = EspressoInk,
    surfaceVariant = SoftOatSurface,
    onSurfaceVariant = BarnwoodMuted,
    surfaceContainerLow = SoftOatSurface,
    surfaceContainer = WarmSandVariant,
    outline = OutlineSand,
    outlineVariant = WarmSandVariant,
    error = DangerCrimson,
    onError = Color.White,
    errorContainer = DangerCrimsonBg,
    onErrorContainer = DangerCrimson
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF83C99F),
    onPrimary = Color(0xFF0A2E1B),
    primaryContainer = Color(0xFF1F4731),
    onPrimaryContainer = CoopSageContainer,
    secondary = Color(0xFFFFC168),
    onSecondary = Color(0xFF3E2200),
    secondaryContainer = Color(0xFF5A3400),
    onSecondaryContainer = HarvestGoldContainer,
    tertiary = Color(0xFFF39C8E),
    onTertiary = Color(0xFF471008),
    tertiaryContainer = Color(0xFF6B2015),
    onTertiaryContainer = TerracottaContainer,
    background = DarkSoilBackground,
    onBackground = DarkCreamInk,
    surface = DarkCoopSurface,
    onSurface = DarkCreamInk,
    surfaceVariant = DarkCoopSurfaceElevated,
    onSurfaceVariant = DarkMutedSand,
    surfaceContainerLow = DarkCoopSurface,
    surfaceContainer = DarkCoopSurfaceElevated,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = DangerCrimsonDark,
    onError = Color(0xFF450B08),
    errorContainer = DangerCrimsonBgDark,
    onErrorContainer = DangerCrimsonDark
)

val CluckShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun CluckCheckTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(LocalCluckSemanticColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CluckTypography,
            shapes = CluckShapes,
            content = content
        )
    }
}
