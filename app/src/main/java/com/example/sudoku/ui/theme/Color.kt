package com.example.sudoku.ui.theme

import androidx.compose.ui.graphics.Color

// -------------------------------------------------------
// Light Theme — section 5.1.1
// White background, light blue accent, light red error
// -------------------------------------------------------
val LightBackground     = Color(0xFFFFFFFF)
val LightSurface        = Color(0xFFF5F5F5)
val LightAccent         = Color(0xFFADD8E6)  // light blue — selected cell
val LightHighlight      = Color(0xFFE3F4F8)  // row/col highlight
val LightError          = Color(0xFFFFCDD2)  // light red — wrong move
val LightErrorText      = Color(0xFFD32F2F)
val LightLockedText     = Color(0xFF1A1A2E)  // pre-filled numbers
val LightPlayerText     = Color(0xFF1565C0)  // player entered numbers
val LightBorderThin     = Color(0xFFBDBDBD)  // 0.5dp cell borders
val LightBorderThick    = Color(0xFF424242)  // 2dp sector borders
val LightNoteText       = Color(0xFF757575)

// -------------------------------------------------------
// Dark Theme — section 5.1.2
// Black background, dark blue accent, orange error
// -------------------------------------------------------
val DarkBackground      = Color(0xFF1A1A2E)
val DarkSurface         = Color(0xFF16213E)
val DarkAccent          = Color(0xFF0F3460)  // dark blue — selected cell
val DarkHighlight       = Color(0xFF0D2137)  // row/col highlight
val DarkError           = Color(0xFF8B3A00)  // orange-brown — wrong move
val DarkErrorText       = Color(0xFFFF6D00)  // orange
val DarkLockedText      = Color(0xFFE0E0E0)  // pre-filled numbers
val DarkPlayerText      = Color(0xFF90CAF9)  // player entered numbers
val DarkBorderThin      = Color(0xFF424242)
val DarkBorderThick     = Color(0xFFE0E0E0)
val DarkNoteText        = Color(0xFF9E9E9E)