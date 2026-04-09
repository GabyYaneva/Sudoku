package com.example.sudoku.model

data class Cell(
    val row: Int,
    val col: Int,
    var value: Int = 0,
    var isLocked: Boolean = false,
    var isError: Boolean = false,
    var isSelected: Boolean = false,
    var isHighlighted: Boolean = false,
    var notes: MutableSet<Int> = mutableSetOf()
)