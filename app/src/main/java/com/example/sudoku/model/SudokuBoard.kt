package com.example.sudoku.model

class SudokuBoard {

    val grid: Array<Array<Cell>> = Array(9) { row ->
        Array(9) { col -> Cell(row, col) }
    }

    fun loadPuzzle(puzzle: Array<IntArray>, solution: Array<IntArray>) {
        for (row in 0..8) {
            for (col in 0..8) {
                val value = puzzle[row][col]
                grid[row][col] = Cell(
                    row = row,
                    col = col,
                    value = value,
                    isLocked = value != 0
                )
            }
        }
    }

    fun getCell(row: Int, col: Int): Cell = grid[row][col]

    fun setValue(row: Int, col: Int, value: Int) {
        val cell = grid[row][col]
        if (!cell.isLocked) {
            cell.value = value
            cell.notes.clear()
        }
    }

    fun clearCell(row: Int, col: Int) {
        val cell = grid[row][col]
        if (!cell.isLocked) {
            cell.value = 0
            cell.isError = false
            cell.notes.clear()
        }
    }

    fun updateHighlights(selectedRow: Int, selectedCol: Int) {
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = grid[row][col]
                cell.isSelected = (row == selectedRow && col == selectedCol)
                cell.isHighlighted = (row == selectedRow || col == selectedCol)
            }
        }
    }

    fun getBoxStart(index: Int): Int = (index / 3) * 3
}