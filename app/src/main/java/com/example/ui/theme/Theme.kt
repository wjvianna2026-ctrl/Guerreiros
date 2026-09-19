package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldAmber,
    onPrimary = Color(0xFF1C1300),
    primaryContainer = Color(0xFF452B00),
    onPrimaryContainer = GoldAmberLight,
    secondary = ElectricCyan,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5B),
    onSecondaryContainer = ElectricCyan,
    tertiary = EmeraldPaid,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = Color(0xFF005234),
    onTertiaryContainer = EmeraldPaidLight,
    background = DarkBgDeep,
    onBackground = TextPrimary,
    surface = DarkBgSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkBgCard,
    onSurfaceVariant = TextSecondary,
    outline = TitaniumBorder,
    outlineVariant = TitaniumBorderSubtle,
    error = ScarletOverdue,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

