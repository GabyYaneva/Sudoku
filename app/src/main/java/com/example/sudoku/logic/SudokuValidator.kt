package com.example.sudoku.logic

class SudokuValidator {

    fun isValidInRow(board: Array<IntArray>, row: Int, num: Int): Boolean {
        for (col in 0..8) {
            if (board[row][col] == num) return false
        }
        return true
    }

    fun isValidInCol(board: Array<IntArray>, col: Int, num: Int): Boolean {
        for (row in 0..8) {
            if (board[row][col] == num) return false
        }
        return true
    }

    fun isValidInBox(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        val boxRowStart = (row / 3) * 3
        val boxColStart = (col / 3) * 3

        for (r in boxRowStart..boxRowStart + 2) {
            for (c in boxColStart..boxColStart + 2) {
                if (board[r][c] == num) return false
            }
        }
        return true
    }

    fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        return isValidInRow(board, row, num) &&
                isValidInCol(board, col, num) &&
                isValidInBox(board, row, col, num)
    }

    fun isBoardComplete(board: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                val num = board[row][col]
                if (num == 0) return false
                board[row][col] = 0
                if (!isValid(board, row, col, num)) {
                    board[row][col] = num
                    return false
                }
                board[row][col] = num
            }
        }
        return true
    }
}