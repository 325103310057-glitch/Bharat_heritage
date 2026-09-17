package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = RoyalSaffron,
  onPrimary = Color.White,
  primaryContainer = DeepSaffron,
  onPrimaryContainer = LightSaffron,
  secondary = HeritageGold,
  onSecondary = RoyalNavy,
  tertiary = ForestGreen,
  background = DarkBackground,
  onBackground = SandstoneIvory,
  surface = DarkSurface,
  onSurface = SandstoneIvory,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = Color(0xFFCAD1DC)
)

private val LightColorScheme = lightColorScheme(
  primary = DeepSaffron,
  onPrimary = Color.White,
  primaryContainer = SaffronContainer,
  onPrimaryContainer = DeepSaffron,
  secondary = SapphireBlue,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFDBEAFE),
  onSecondaryContainer = RoyalNavy,
  tertiary = ForestGreen,
  onTertiary = Color.White,
  background = SandstoneIvory,
  onBackground = RoyalNavy,
  surface = Color.White,
  onSurface = RoyalNavy,
  surfaceVariant = WarmParchment,
  onSurfaceVariant = DeepNavy
)

@Composable
fun BharatHeritageTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) = BharatHeritageTheme(darkTheme, dynamicColor, content)

