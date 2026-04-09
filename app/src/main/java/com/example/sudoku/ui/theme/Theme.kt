package com.example.sudoku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary      = LightAccent,
    background   = LightBackground,
    surface      = LightSurface,
    error        = LightErrorText,
    onPrimary    = LightLockedText,
    onBackground = LightLockedText,
    onSurface    = LightLockedText,
)

private val DarkColorScheme = darkColorScheme(
    primary      = DarkAccent,
    background   = DarkBackground,
    surface      = DarkSurface,
    error        = DarkErrorText,
    onPrimary    = DarkLockedText,
    onBackground = DarkLockedText,
    onSurface    = DarkLockedText,
)

data class SudokuColors(
    val background: Color,
    val surface: Color,
    val accent: Color,
    val highlight: Color,
    val error: Color,
    val errorText: Color,
    val lockedText: Color,
    val playerText: Color,
    val borderThin: Color,
    val borderThick: Color,
    val noteText: Color
)

val LocalSudokuColors = compositionLocalOf {
    SudokuColors(
        background  = LightBackground,
        surface     = LightSurface,
        accent      = LightAccent,
        highlight   = LightHighlight,
        error       = LightError,
        errorText   = LightErrorText,
        lockedText  = LightLockedText,
        playerText  = LightPlayerText,
        borderThin  = LightBorderThin,
        borderThick = LightBorderThick,
        noteText    = LightNoteText
    )
}

@Composable
fun SudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val sudokuColors = if (darkTheme) {
        SudokuColors(
            background  = DarkBackground,
            surface     = DarkSurface,
            accent      = DarkAccent,
            highlight   = DarkHighlight,
            error       = DarkError,
            errorText   = DarkErrorText,
            lockedText  = DarkLockedText,
            playerText  = DarkPlayerText,
            borderThin  = DarkBorderThin,
            borderThick = DarkBorderThick,
            noteText    = DarkNoteText
        )
    } else {
        SudokuColors(
            background  = LightBackground,
            surface     = LightSurface,
            accent      = LightAccent,
            highlight   = LightHighlight,
            error       = LightError,
            errorText   = LightErrorText,
            lockedText  = LightLockedText,
            playerText  = LightPlayerText,
            borderThin  = LightBorderThin,
            borderThick = LightBorderThick,
            noteText    = LightNoteText
        )
    }

    CompositionLocalProvider(LocalSudokuColors provides sudokuColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = SudokuTypography,
            content     = content
        )
    }
}