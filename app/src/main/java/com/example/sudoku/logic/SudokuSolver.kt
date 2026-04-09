package com.example.sudoku.logic

class SudokuSolver {

    private val validator = SudokuValidator()

    fun solve(board: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    for (num in 1..9) {
                        if (validator.isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solve(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }
    fun countSolutions(board: Array<IntArray>, limit: Int = 2): Int {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    var count = 0
                    for (num in 1..9) {
                        if (validator.isValid(board, row, col, num)) {
                            board[row][col] = num
                            count += countSolutions(board, limit)
                            board[row][col] = 0
                            if (count >= limit) return count
                        }
                    }
                    return count
                }
            }
        }
        return 1
    }
}